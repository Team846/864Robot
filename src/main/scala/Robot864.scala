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
import squants.electro.Volts
import squants.motion.FeetPerSecond
import squants.time.Milliseconds

class Robot864 extends RobotBase {

  def escConfig(implicit props: DrivetrainProperties, hard: DrivetrainHardware): Unit = {
    println("Configuring TalonSRXs")

    import hard.{escIdx, escTout}
    hard.left.t.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTout)
    hard.right.t.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTout)

    import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced._
    StatusFrame.values().foreach { it =>
      hard.right.t.setStatusFramePeriod(it, 1000, escTout)
      hard.left.t.setStatusFramePeriod(it, 1000, escTout)
      hard.rightFollower.t.setStatusFramePeriod(it, 1000, escTout)
      hard.leftFollower.t.setStatusFramePeriod(it, 1000, escTout)
    }

    Set(hard.left, hard.right).foreach { it =>
      it.t.setStatusFramePeriod(Status_1_General, 5, escTout)
      it.t.setStatusFramePeriod(Status_2_Feedback0, 10, escTout)

      it.t.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_5Ms, escTout)
      it.t.configVelocityMeasurementWindow(4, escTout)
    }
  }

  override def startCompetition(): Unit = {
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

    logger.println(s"dt sec\tleft ft/s\tright ft/s")
    val cancel = hard.leftVelocity
      .zip(hard.rightVelocity)
      .zipWithDt
      .foreach { case ((lv, rv), dt) =>
        if (isEnabled) {
          logger.println(s"${dt.toSeconds}\t${lv.toFeetPerSecond}\t${rv.toFeetPerSecond}")
          logger.flush()
        }
      }

    HAL.observeUserProgramStarting()
    while (true) m_ds.waitForData()
  }
}
