import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.tasks.ContinuousTask
import com.lynbrookrobotics.potassium.vision.limelight.LimelightNetwork

class SignalDriverForTargetTask extends ContinuousTask {
  def onStart(): Unit = {
    val limelightNetwork: LimelightNetwork = LimelightNetwork(WPIClock)
    limelightNetwork.hasTarget.foreach(if (_) println("has target"))
  }

  def onEnd(): Unit = {

  }
}
