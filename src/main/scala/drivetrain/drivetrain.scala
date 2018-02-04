import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.NoOperation
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedDrive
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal.OpenLoop
import com.lynbrookrobotics.potassium.control.offload.{EscConfig, OffloadedSignal}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.Percent
import squants.space.Length

package object drivetrain extends OffloadedDrive {
  override type Properties = DrivetrainProperties
  override type Hardware = DrivetrainHardware

  // TODO: This method is huge, refactor in 3 separate methods
  override protected def output(h: Hardware, s: TwoSided[OffloadedSignal]): Unit = {
    h.left.applyCommand(s.left)
    h.right.applyCommand(s.right)
  }

  override protected def controlMode(implicit hardware: Hardware, props: Properties) = NoOperation

  class Drivetrain(implicit hardware: Hardware,
                   props: Signal[Properties],
                   clock: Clock,
                   coreTicks: Stream[_]) extends Component[DriveSignal] {
    override def defaultController: Stream[TwoSided[OffloadedSignal]] = coreTicks.map { _ =>
      TwoSided(OpenLoop(Percent(0)), OpenLoop(Percent(0)))
    }

    override def applySignal(signal: TwoSided[OffloadedSignal]) = output(hardware, signal)
  }

}
