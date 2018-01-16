import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.control.PositionControl
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.streams._
import drivetrain.Drivetrain
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import squants.space.Radians
import squants.time.Milliseconds

class Robot864 extends RobotBase {
  override def startCompetition(): Unit = {
    HAL.observeUserProgramStarting()

    implicit val clock: Clock = WPIClock

    implicit val props: DrivetrainProperties = new DrivetrainProperties
    implicit val sigProps: Signal[DrivetrainProperties] =
      Signal.constant(props)

    implicit val hardw: DrivetrainHardware = new DrivetrainHardware

    val drivetrainComponent = new Drivetrain
    drivetrainComponent.setController(Stream.periodic(Milliseconds(10)) {
      TwoSided(
        PositionControl(
          props.escPositionGains,
          Radians(6.28)
        ),
        PositionControl(
          props.escPositionGains,
          Radians(6.28)
        )
      )
    })

    while (true) m_ds.waitForData()
  }
}
