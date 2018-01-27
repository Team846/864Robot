import java.util.InputMismatchException

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.NoOperation
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedDrive
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.control.OffloadedSignal.{EscPositionGains, EscVelocityGains}
import com.lynbrookrobotics.potassium.control.{OffloadedSignal, OpenLoop, PositionControl, VelocityControl}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.Percent

package object drivetrain extends OffloadedDrive {
  self =>
  override type Properties = DrivetrainProperties
  override type Hardware = DrivetrainHardware

  // TODO: This method is huge, refactor in 3 separate methods
  override protected def output(h: Hardware, s: TwoSided[OffloadedSignal]): Unit = {
    h.left(s.left)
    h.right(s.right)
  }

  override protected def getControlMode(implicit hardware: Hardware, props: Properties) = NoOperation

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
