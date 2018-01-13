import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.{Component, streams}
import com.lynbrookrobotics.potassium.commons.drivetrain.{NoOperation, TwoSidedDrive}
import com.lynbrookrobotics.potassium.streams._
import squants.{Percent, Time}
import squants.time.Milliseconds

class Drivetrain(implicit hardware: DrivetrainHardware) extends TwoSidedDrive {
  override type Hardware = DrivetrainHardware
  override type Properties = DrivetrainProperties

  override protected def output(hardware: DrivetrainHardware, signal: TwoSidedSignal): Unit = {}

  override protected def controlMode(implicit hardware: DrivetrainHardware, props: DrivetrainProperties) = NoOperation

  implicit val clock = new Clock {override def apply(period: Time)(thunk: Time => Unit): Cancel = ???

    override def currentTime: Time = ???

    override def singleExecution(delay: Time)(thunk: => Unit): Unit = ???
  }
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
