import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.{Component}
import com.lynbrookrobotics.potassium.streams._
import squants.Percent
import squants.time.Milliseconds

class Drivetrain(implicit hardware: DrivetrainHardware, clock: Clock) extends Component[(Double, Double)] {
  override def defaultController: Stream[(Double, Double)] = {
    Stream.periodic(Milliseconds(10)) {
      (0.75, 0.75)
    }
  }

  override def applySignal(signal: (Double, Double)): Unit = {
    hardware.leftFront.set(ControlMode.PercentOutput, signal._1)
    hardware.leftBack.set(ControlMode.PercentOutput, signal._1)

    hardware.rightFront.set(ControlMode.PercentOutput, -signal._2)
    hardware.rightBack.set(ControlMode.PercentOutput, -signal._2)
  }
}