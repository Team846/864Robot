import com.lynbrookrobotics.potassium.commons.drivetrain.UnicycleControlMode
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.UnicycleSignal
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.vision.TargetTracking
import com.lynbrookrobotics.potassium.vision.limelight.{CameraProperties, LimelightNetwork}
import edu.wpi.first.wpilibj.{IterativeRobot, Joystick}
import squants.Percent
import squants.space.{Degrees, Feet}

class Robot864 extends IterativeRobot {
  implicit val drivetrainHardware = new DrivetrainHardware
  val joystick = new Joystick(1)
  val limeLightNetwork: LimelightNetwork = LimelightNetwork(WPIClock)
  implicit val camProps: CameraProperties =  CameraProperties(Degrees(3.5), Degrees(0), Feet(10.5/12), Feet(11/12))
  val targeting = new TargetTracking(limeLightNetwork.xOffsetAngle, limeLightNetwork.percentArea)

  implicit val props = Signal.constant(new DrivetrainProperties)
  val drivetrainComponent = new Drivetrain.DrivetrainComponent

//  val printer = targeting.distanceToTarget.foreach(p => println("distance:", p))
//  val secondPrinter = drivetrainHardware.forwardVelocity.foreach(p => println("vel:", p))

  override def teleopInit(): Unit = {
    drivetrainComponent.resetToDefault()
    val absoluteTargetDistance = drivetrainHardware.forwardPosition.zipAsync(targeting.distanceToTarget.map(p => {
      if (math.random > 0.99) println(p)
      p.map(x => Feet(2) - x).getOrElse(Feet(0))
    })).map(t => t._1 + t._2)

    val forwardPositionControlling = Drivetrain.UnicycleControllers.forwardPositionControl(absoluteTargetDistance)._1

    val xOffsets = targeting.angleToTarget.map(p => -p)

    val turnPositionControlling = drivetrainHardware.turnPosition.zipAsync(xOffsets).map(t => t._1 + t._2)
    val turnController = Drivetrain.UnicycleControllers.turnPositionControl(turnPositionControlling)._1

    val forwardController = Drivetrain.UnicycleControllers.lowerLevelVelocityControl(forwardPositionControlling)

    val out = Drivetrain.UnicycleControllers.lowerLevelOpenLoop(
      forwardPositionControlling.map(p =>
        p.forward
      ).zip(turnController.map(p =>
        p.turn
      )).map(p =>
        UnicycleSignal(p._1 max Percent(-20) min Percent(20), p._2 max Percent(-20) min Percent(20))
      )
    )

    drivetrainComponent.setController(out.withCheck(out =>
      if (math.random > 0.99) println(out)
    ))

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
