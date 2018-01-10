import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.commons.drivetrain.{NoOperation, TwoSidedDrive}

class DrivetrainComponents extends TwoSidedDrive {
  override type Hardware = DrivetrainHardware
  override type Properties = DrivetrainProperties

  override protected def output(hardware: DrivetrainHardware, signal: TwoSidedSignal): Unit = {
//    hardware.left.set(ControlMode.PercentOutput, signal.left.toEach)
//    hardware.right.set(ControlMode.PercentOutput, signal.right.toEach)
    ???
  }

  override protected def controlMode(implicit hardware: DrivetrainHardware, props: DrivetrainProperties) = NoOperation

  override type Drivetrain = this.type
}
