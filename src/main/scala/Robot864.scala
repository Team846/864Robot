import edu.wpi.first.wpilibj.Joystick.AxisType
import edu.wpi.first.wpilibj.{IterativeRobot, Joystick, RobotBase}

class Robot864 extends IterativeRobot {
  val drivetrainHardware = new DrivetrainHardware
  val joystick = new Joystick(1)

  override def teleopPeriodic(): Unit = {
    println(joystick.getAxis(AxisType.kX))
  }
}
