import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.vision.limelight.LimelightNetwork
import edu.wpi.first.wpilibj.{IterativeRobot, Joystick}


class Robot864 extends IterativeRobot {
  implicit val drivetrainHardware = new DrivetrainHardware
  val joystick = new Joystick(1)
  implicit val props = Signal.constant(new DrivetrainProperties)
  val drivetrainComponent = new Drivetrain.DrivetrainComponent
  val limeLightNetwork: LimelightNetwork = LimelightNetwork(WPIClock)


  //  val printer = targeting.distanceToTarget.foreach(p => println("distance:", p))
//  val secondPrinter = drivetrainHardware.forwardVelocity.foreach(p => println("vel:", p))

  override def teleopInit(): Unit = {
    drivetrainComponent.resetToDefault()

    new CollectCubeTask(drivetrainComponent).init()
  }

  override def disabledInit(): Unit = {
    drivetrainComponent.resetToDefault()
  }

  override def robotInit(): Unit = {
    limeLightNetwork.table.getEntry("ledMode").setDouble(1)
  }

  override def disabledPeriodic(): Unit = {

  }
}
