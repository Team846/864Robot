import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.{TwoSidedDrive, TwoSidedSignal}
import com.lynbrookrobotics.potassium.{Component, streams}
import com.lynbrookrobotics.potassium.commons.drivetrain.NoOperation
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.streams._
import squants.Percent
import squants.time.Milliseconds

object Drivetrain extends TwoSidedDrive {
  override type Hardware = DrivetrainHardware
  override type Properties = DrivetrainProperties

  val hardware = new DrivetrainHardware

  override protected def output(hardware: DrivetrainHardware, signal: TwoSidedSignal): Unit = {
    hardware.leftBack.talon.set(ControlMode.PercentOutput, signal.left.toEach)
    hardware.rightBack.talon.set(ControlMode.PercentOutput, signal.right.toEach)
  }

  override protected def controlMode(implicit hardware: DrivetrainHardware, props: DrivetrainProperties) = NoOperation

  implicit val clock = WPIClock

  class DrivetrainComponent extends Component[TwoSidedSignal] {
    override def defaultController: streams.Stream[TwoSidedSignal] = {
      Stream.periodic(Milliseconds(10)){
        TwoSidedSignal(Percent(0), Percent(0))
      }
    }

    override def applySignal(signal: TwoSidedSignal): Unit = {
      hardware.leftBack.talon.set(ControlMode.PercentOutput, signal.left.toEach)
      hardware.rightBack.talon.set(ControlMode.PercentOutput, signal.right.toEach)
    }
  }

  override type Drivetrain = DrivetrainComponent
}
