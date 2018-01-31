import com.lynbrookrobotics.potassium.commons.drivetrain.UnicycleControlMode
import com.lynbrookrobotics.potassium.{Signal, streams}
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedVelocity
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.UnicycleSignal
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.vision.TargetTracking
import com.lynbrookrobotics.potassium.vision.limelight.{CameraProperties, LimelightNetwork}
import edu.wpi.first.wpilibj.{IterativeRobot, Joystick}
import squants.Percent
import squants.motion.FeetPerSecond
import squants.space.{Degrees, Feet}
import squants.time.Milliseconds

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
//    val absoluteTargetDistance = drivetrainHardware.forwardPosition.zipAsync(targeting.distanceToTarget.map(p => {
//      if (math.random > 0.99) println(p)
//      p.map(x => Feet(2) - x).getOrElse(Feet(0))
//    })).map(t => t._1 + t._2)

//    val forwardPositionControlling = Drivetrain.UnicycleControllers.forwardPositionControl(absoluteTargetDistance)._1

//    drivetrainComponent.setController(out.withCheck(out =>
//      if (math.random > 0.99) println(out)
//    ))

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
