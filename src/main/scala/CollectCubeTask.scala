import Drivetrain.DrivetrainComponent
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.UnicycleSignal
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.vision.TargetTracking
import com.lynbrookrobotics.potassium.vision.limelight.{CameraProperties, LimelightNetwork}
import squants.Percent
import squants.space.{Degrees, Feet}

class CollectCubeTask(drivetrainComponent: DrivetrainComponent)
                     (implicit val drivetrainHardware: DrivetrainHardware,
                      implicit val props: Signal[Drivetrain.UnicycleControllers.DrivetrainProperties]) extends FiniteTask {

  def onStart(): Unit = {
    val limeLightNetwork: LimelightNetwork = LimelightNetwork(WPIClock)
    implicit val camProps: CameraProperties =  CameraProperties(Degrees(2), Degrees(0), Feet(17/12), Feet(11/12))
    val targeting = new TargetTracking(limeLightNetwork.xOffsetAngle, limeLightNetwork.percentArea)

    val xOffsets = targeting.angleToTarget.map(p => -p)

    val turnPosition = drivetrainHardware.turnPosition.zipAsync(xOffsets).map{t =>
      println("t =", t)
      t._1 + t._2
    }
    val turnController = Drivetrain.UnicycleControllers.turnPositionControl(turnPosition)._1

    val out = Drivetrain.UnicycleControllers.lowerLevelOpenLoop(
      turnController.map{ p =>
        println(p)
        UnicycleSignal(Percent(-15), p.turn max Percent(-20) min Percent(20))
      }
    )

    drivetrainComponent.setController(out.withCheck(_ =>
      targeting.distanceToTarget.foreach(p =>
        if (!p.exists(_ >= Feet(2))) {
          println("p =", p)
          finished()
        }
    )))
  }

  def onEnd(): Unit = {
    drivetrainComponent.resetToDefault()
  }
}
