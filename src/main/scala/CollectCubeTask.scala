import Drivetrain.DrivetrainComponent
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.commons.drivetrain.unicycle.UnicycleSignal
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.tasks.FiniteTask
import com.lynbrookrobotics.potassium.vision.VisionTargetTracking
import com.lynbrookrobotics.potassium.vision.limelight.LimelightNetwork
import squants.Percent
import squants.space.Feet

class CollectCubeTask(drivetrainComponent: DrivetrainComponent)
                     (implicit val drivetrainHardware: DrivetrainHardware,
                      implicit val props: Signal[Drivetrain.UnicycleControllers.DrivetrainProperties]) extends FiniteTask {

  def onStart(): Unit = {
    val limeLightNetwork: LimelightNetwork = LimelightNetwork(WPIClock)
    val distance = VisionTargetTracking.distanceToTarget(limeLightNetwork.percentArea)
    val angle = VisionTargetTracking.angleToTarget(limeLightNetwork.xOffsetAngle)

    val turnPosition = drivetrainHardware.turnPosition.zipAsync(angle).map{t =>
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
      distance.foreach(p =>
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
