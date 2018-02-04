import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.streams._
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import lift.Lift
import squants.space.Inches
import squants.time.Milliseconds

class Robot864 extends RobotBase {

  override def startCompetition(): Unit = {
    implicit val clock: Clock = WPIClock
    implicit val coreTicks: Stream[_] = Stream.periodic(Milliseconds(5))(Unit)

    //    implicit val props: DrivetrainProperties = new DrivetrainProperties
    //    implicit val sigProps: Signal[DrivetrainProperties] = Signal.constant(props)

    //    implicit val hard: DrivetrainHardware = new DrivetrainHardware
    //
    //    val drivetrainComponent = new Drivetrain
    //    drivetrainComponent.resetToDefault()
    //
    //    val target = FeetPerSecond(15)
    //    drivetrainComponent.setController(drivetrain.velocityControl(coreTicks.map { _ =>
    //      TwoSided(target, target)
    //    }))

    //    val logPath = "/tmp/control.csv"
    //    new File(logPath).delete()
    //    val logger = new PrintStream(logPath)
    //
    //    logger.println(s"dt sec\tleft ft/s\tright ft/s")
    //    val cancel = hard.leftVelocity
    //      .zip(hard.rightVelocity)
    //      .zipWithDt
    //      .foreach { case ((lv, rv), dt) =>
    //        if (isEnabled) {
    //          logger.println(s"${dt.toSeconds}\t${lv.toFeetPerSecond}\t${rv.toFeetPerSecond}")
    //          logger.flush()
    //        }
    //      }

    implicit val p = new Lift.Properties()
    implicit val sp = Signal.constant(p)
    implicit val h = new Lift.Hardware()
    val c = new Lift.Comp()

    val (error, controller) = Lift.positionControl(
      coreTicks.mapToConstant(Inches(10.5) + Inches(6))
    )
    c.setController(controller)
    error.foreach { it =>
      if (Math.random() > 0.99) println(it)
    }

    HAL.observeUserProgramStarting()
    while (true) {
      m_ds.waitForData()
    }
  }
}
