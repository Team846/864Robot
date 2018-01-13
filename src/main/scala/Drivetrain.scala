import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.{Component, streams}
import com.lynbrookrobotics.potassium.commons.drivetrain.{NoOperation, TwoSidedDrive}
import com.lynbrookrobotics.potassium.streams._
import squants.Percent
import squants.time.Milliseconds

class Drivetrain(implicit hardware: DrivetrainHardware) extends TwoSidedDrive {
  override type Hardware = DrivetrainHardware
  override type Properties = DrivetrainProperties

//  override protected def output(hardware: DrivetrainHardware, signal: TwoSidedSignal): Unit = {
////    hardware.left.set(ControlMode.PercentOutput, signal.left.toEach)
////    hardware.right.set(ControlMode.PercentOutput, signal.right.toEach)
//    ???
//  }

  override protected def controlMode(implicit hardware: DrivetrainHardware, props: DrivetrainProperties) = NoOperation

  override type Drivetrain = this.type

  class DrivetrainComponent extends Component[TwoSidedSignal] {
    override def defaultController: streams.Stream[TwoSidedSignal] = {
      Stream.periodic(Milliseconds(10)){
        TwoSidedSignal(Percent(50), Percent(50))
      }
    }

    override def applySignal(signal: TwoSidedSignal): Unit = {
      hardware.leftBack.set(ControlMode.PercentOutput, -signal.left.toEach)
      hardware.leftFront.set(ControlMode.PercentOutput, signal.left.toEach)
      hardware.rightBack.set(ControlMode.PercentOutput, -signal.right.toEach)
      hardware.rightFront.set(ControlMode.PercentOutput, -signal.right.toEach)
    }
  }
}
