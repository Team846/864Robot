import com.lynbrookrobotics.potassium.commons.drivetrain.UnicycleControlMode
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.UnicycleSignal
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.vision.TargetTracking
import com.lynbrookrobotics.potassium.vision.limelight.{CameraProperties, LimelightNetwork}
import edu.wpi.first.wpilibj.{IterativeRobot, Joystick}
import squants.space.{Degrees, Feet}

class Robot864 extends IterativeRobot {
  implicit val drivetrainHardware = new DrivetrainHardware
  val joystick = new Joystick(1)
  val limeLightNetwork: LimelightNetwork = LimelightNetwork(WPIClock)
  implicit val camProps: CameraProperties =  CameraProperties(Degrees(-3.5), Degrees(0), Feet(10.5/12), Feet(11/12))
  val targeting = new TargetTracking(limeLightNetwork.xOffsetAngle, limeLightNetwork.percentArea)

  implicit val props = Signal.constant(new DrivetrainProperties)
  val drivetrainComponent = new Drivetrain.DrivetrainComponent

  val targetDistance = targeting.distanceToTarget.map(p => -p)

  val forwardPositionControlling = Drivetrain.UnicycleControllers.forwardPositionControl(targetDistance)._1

  val xOffsets = targeting.angleToTarget.map(p => -p)

  val turnPositionControlling = drivetrainHardware.turnPosition.zip(xOffsets).map(t => t._1 + t._2)
  val turnController = Drivetrain.UnicycleControllers.turnPositionControl(turnPositionControlling)._1

  val forwardController = Drivetrain.UnicycleControllers.lowerLevelOpenLoop(forwardPositionControlling)

  val out = Drivetrain.UnicycleControllers.lowerLevelOpenLoop(
    forwardPositionControlling.map(p =>
      p.forward
    ).zip(turnController.map(p =>
      p.turn
    )).map(p =>
      UnicycleSignal(p._1, p._2)
    )
  )

  out.foreach(_ => ())

//  val printer = targeting.distanceToTarget.foreach(p => println("distance:", p))
//  val secondPrinter = drivetrainHardware.forwardVelocity.foreach(p => println("vel:", p))

  override def teleopInit(): Unit = {
    drivetrainComponent.resetToDefault()

    drivetrainComponent.setController(forwardController)

  }

  override def disabledInit(): Unit = {
    drivetrainComponent.resetToDefault()
  }

  override def disabledPeriodic(): Unit = {

  }
}
