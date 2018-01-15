import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.streams._
import drivetrain.Drivetrain
import drivetrain.unicycleTasks._
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import squants.Percent
import squants.space.{Degrees, Inches}
import squants.time.Milliseconds

class Robot864 extends RobotBase {
  override def startCompetition(): Unit = {
    HAL.observeUserProgramStarting()

    implicit val clock: Clock = WPIClock

    implicit val props: DrivetrainProperties = new DrivetrainProperties
    implicit val sigProps: Signal[DrivetrainProperties] =
      Signal.constant(props)

    implicit val hardw: DrivetrainHardware = new DrivetrainHardware

    val drivetrain = new Drivetrain

    Stream.periodic(Milliseconds(100))(isAutonomous)
      .eventWhen(identity)
      .foreach(
        new DriveDistanceStraight(
          Inches(25),
          Inches(1),
          Degrees(5),
          Percent(15)
        )(drivetrain).toContinuous
      )

    while (true) m_ds.waitForData()
  }
}
