import java.io.{File, PrintStream}

import com.ctre.phoenix.motorcontrol._
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.streams._
import drivetrain.Drivetrain
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import squants.motion.FeetPerSecond
import squants.time.Milliseconds

class Robot864 extends RobotBase {

  def escConfig(implicit props: DrivetrainProperties, hard: DrivetrainHardware): Unit = {
    println("Configuring TalonSRXs")

    // 254 DEFAULT
    import hard.{escIdx, escTout}
    hard.left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTout)
    hard.right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTout)

    import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced._
    Set(hard.left, hard.right).foreach { it =>
      it.setStatusFramePeriod(Status_10_MotionMagic, 100, escTout)
      it.setStatusFramePeriod(Status_1_General, 5, escTout)
      it.setStatusFramePeriod(Status_2_Feedback0, 100, escTout)
      it.setStatusFramePeriod(Status_12_Feedback1, 100, escTout)
      it.setStatusFramePeriod(Status_3_Quadrature, 100, escTout)
    }

    StatusFrame.values().foreach { it =>
      hard.rightFollower.setStatusFramePeriod(it, 1000, escTout)
      hard.leftFollower.setStatusFramePeriod(it, 1000, escTout)
    }

    // 254 DRIVE
    Set(hard.left, hard.right).foreach { it =>
      it.setStatusFramePeriod(Status_2_Feedback0, 5, escTout)
      it.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, escTout)
      it.configVelocityMeasurementWindow(32, escTout)
    }
  }

  override def startCompetition(): Unit = {
    HAL.observeUserProgramStarting()

    implicit val clock: Clock = WPIClock
    implicit val coreTicks: Stream[_] = Stream.periodic(Milliseconds(5))(Unit)

    implicit val props: DrivetrainProperties = new DrivetrainProperties
    implicit val sigProps: Signal[DrivetrainProperties] = Signal.constant(props)

    implicit val hard: DrivetrainHardware = new DrivetrainHardware
    escConfig

    val drivetrainComponent = new Drivetrain
    drivetrainComponent.resetToDefault()

    val target = FeetPerSecond(15)
    drivetrainComponent.setController(drivetrain.velocityControl(coreTicks.map { _ =>
      TwoSided(target, target)
    }))

    val logPath = "/tmp/control.csv"
    new File(logPath).delete()
    val logger = new PrintStream(logPath)

    logger.println(s"dt (s)\tleft ft/s\tright ft/s")
    val cancel = hard.leftVelocity
      .zip(hard.rightVelocity)
      .zipWithDt
      .foreach { case ((lv, rv), dt) =>
        if (isEnabled) {
          logger.println(s"${dt.toSeconds}\t${lv.toFeetPerSecond}\t${rv.toFeetPerSecond}")
          logger.flush()
        }
      }

    while (true) m_ds.waitForData()
  }
}
