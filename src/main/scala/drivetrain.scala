import java.util.InputMismatchException

import com.ctre.phoenix.motorcontrol.ControlMode
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
    import h._
    s match {
      case TwoSided(OpenLoop(ls), OpenLoop(rs)) =>
        h.left.set(ControlMode.PercentOutput, ls.toEach)
        h.right.set(ControlMode.PercentOutput, rs.toEach)

      case TwoSided(PositionControl(lg, ls), PositionControl(rg, rs)) =>
        set(left, escIdx, escTout, lg)
        set(leftFollower, escIdx, escTout, lg)
        set(right, escIdx, escTout, rg)
        set(rightFollower, escIdx, escTout, rg)

        left.set(ControlMode.Position, ls.toEach)
        right.set(ControlMode.Position, rs.toEach)

      case TwoSided(VelocityControl(lg, ls), VelocityControl(rg, rs)) =>
        set(left, escIdx, escTout, lg)
        set(leftFollower, escIdx, escTout, lg)
        set(right, escIdx, escTout, rg)
        set(rightFollower, escIdx, escTout, rg)

        left.set(ControlMode.Velocity, ls.toEach)
        right.set(ControlMode.Velocity, rs.toEach)

      case _ => throw new InputMismatchException(s"signal is of an awkward type: $s")
    }
  }

  private def set(esc: TalonSRX, idx: Int, tOut: Int, g: EscVelocityGains): Unit = {
    import g._
    esc.config_kP(idx, p, tOut)
    esc.config_kI(idx, i, tOut)
    esc.config_kD(idx, d, tOut)
    esc.config_kF(idx, f, tOut)
  }

  private def set(esc: TalonSRX, idx: Int, tOut: Int, g: EscPositionGains): Unit = {
    import g._
    esc.config_kP(idx, p, tOut)
    esc.config_kI(idx, i, tOut)
    esc.config_kD(idx, d, tOut)
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
