import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.frc.WPIClock
import edu.wpi.first.wpilibj.Joystick.AxisType
import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj.{IterativeRobot, Joystick, RobotBase}

class Robot864 extends RobotBase {

  override def startCompetition(): Unit = {
    HAL.observeUserProgramStarting()

    implicit val clock: Clock = WPIClock
    implicit val hardware = new DrivetrainHardware
    val drive = new Drivetrain()
    drive.resetToDefault()

    while(true) {
      println(HAL.getMatchTime)
      m_ds.waitForData()
    }
  }
}
