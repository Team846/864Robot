import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj.{Joystick, RobotBase}
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

    class PrintTask(msg: String) extends FiniteTask() {
      override protected def onStart(): Unit = {
        println(msg)
        finished()
      }

      override protected def onEnd() = Unit
    }

    val j1 = new Joystick(0)
    val j2 = new Joystick(1)

    coreTicks.eventWhen(_ => j1.getTrigger).foreach(
      new Lift.positionTasks.WhileAtPosition(
        coreTicks.map(_ => Inches(10.5 + 6) + Inches(j1.getY() * 6)),
        Inches(0)
      )(c).apply(new PrintTask("at ")).toContinuous
    )

    HAL.observeUserProgramStarting()
    while (true) {
      m_ds.waitForData()
    }
  }
}
