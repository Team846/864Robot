import java.io.{File, PrintStream}

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

  override def startCompetition(): Unit = {
    HAL.observeUserProgramStarting()

    //    new Compressor(1).start()

    implicit val clock: Clock = WPIClock
    implicit val coreTicks: Stream[_] = Stream.periodic(Milliseconds(5))(Unit)

    implicit val props: DrivetrainProperties = new DrivetrainProperties
    implicit val sigProps: Signal[DrivetrainProperties] = Signal.constant(props)

    implicit val hardw: DrivetrainHardware = new DrivetrainHardware

    val drivetrainComponent = new Drivetrain
    drivetrainComponent.resetToDefault()

    //    val joystick = new Joystick(0)
    //    drivetrainComponent.setController(
    //      drivetrain.velocityControl(
    //        Stream.periodic(Milliseconds(25)) {
    //          -joystick.getY()
    //        }
    //          .map(it => FeetPerSecond(it * 24))
    //          .map(it => TwoSided(it, it))
    //      )
    //    )
    //
    //    drivetrainComponent.setController(
    //      Stream.periodic(Milliseconds(1000)) {
    //        TwoSided(OpenLoop(Percent(1000)), OpenLoop(Percent(1000)))
    //      }
    //    )

    val target = FeetPerSecond(15)
    drivetrainComponent.setController(drivetrain.velocityControl(coreTicks.map { _ =>
      //                TwoSided(props.maxLeftVelocity, props.maxRightVelocity)
      TwoSided(target, target)
    }))

    //    drivetrainComponent.setController(drivetrain.positionControl(coreTicks.map { _ =>
    //      TwoSided(Feet(14), Feet(14))
    //    }))


    val logPath = "/tmp/ctrl.csv"
    new File(logPath).delete()
    val logger = new PrintStream(logPath)
    logger.println(s"target\t${target.toFeetPerSecond}")
    logger.println(s"left ft/s\tright ft/s")
    val cancel = hardw.leftVelocity
      .zip(hardw.rightVelocity)
      .foreach { case (lv, rv) =>
        if (isEnabled) {
          logger.println(s"${lv.toFeetPerSecond}\t${rv.toFeetPerSecond}")
          logger.flush()
        }
      }

    while (true) {
      m_ds.waitForData()
      if (Math.random() > 0.9) println("POOP!!!")
    }
  }
}
