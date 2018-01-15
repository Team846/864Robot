import java.util.InputMismatchException

import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.commons.drivetrain.NoOperation
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedDrive
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.control.OffloadedSignal.{AngularPositionGains, AngularVelocityGains}
import com.lynbrookrobotics.potassium.control.{OffloadedSignal, OpenLoop, PositionControl, VelocityControl}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.motion.AngularVelocity
import squants.{Angle, Dimensionless, Time}

package object drivetrain extends OffloadedDrive {
  self =>
  override type Properties = DrivetrainProperties
  override type Hardware = DrivetrainHardware

  // TODO: This method is huge, refactor in 3 separate methods
  override protected def output(h: Hardware, s: TwoSided[OffloadedSignal]): Unit = {
    s match {
      case TwoSided(OpenLoop(ls), OpenLoop(rs)) =>
        h.left.set(ControlMode.PercentOutput, ls.toEach)
        h.right.set(ControlMode.PercentOutput, rs.toEach)

      case TwoSided(PositionControl(lg, ls), PositionControl(rg, rs)) =>
        import h.{escTimeConst, leftEncoder, rightEncoder}
        val (lnp, lni, lnd, lns) = angleToEscNative(
          lg, ls, escTimeConst, leftEncoder.conversionFactor
        )
        val (rnp, rni, rnd, rns) = angleToEscNative(
          rg, rs, escTimeConst, rightEncoder.conversionFactor
        )

        import h.{escIdx, escTimeout, left, right}
        left.config_kP(escIdx, lnp, escTimeout)
        left.config_kI(escIdx, lni, escTimeout)
        left.config_kD(escIdx, lnd, escTimeout)

        right.config_kP(escIdx, rnp, escTimeout)
        right.config_kI(escIdx, rni, escTimeout)
        right.config_kD(escIdx, rnd, escTimeout)

        left.set(ControlMode.Position, lns)
        right.set(ControlMode.Position, rns)

      case TwoSided(VelocityControl(lg, ls), VelocityControl(rg, rs)) =>
        import h.{escTimeConst, leftEncoder, rightEncoder}
        val (lnp, lni, lnd, lnf, lns) = angleToEscNative(
          lg, ls, escTimeConst, leftEncoder.conversionFactor
        )
        val (rnp, rni, rnd, rnf, rns) = angleToEscNative(
          rg, rs, escTimeConst, rightEncoder.conversionFactor
        )

        import h.{escIdx, escTimeout, left, right}
        left.config_kP(escIdx, lnp, escTimeout)
        left.config_kI(escIdx, lni, escTimeout)
        left.config_kD(escIdx, lnd, escTimeout)
        left.config_kF(escIdx, lnf, escTimeout)

        right.config_kP(escIdx, rnp, escTimeout)
        right.config_kI(escIdx, rni, escTimeout)
        right.config_kD(escIdx, rnd, escTimeout)
        right.config_kF(escIdx, rnf, escTimeout)

        left.set(ControlMode.Velocity, lns)
        right.set(ControlMode.Velocity, rns)

      case _ => throw new InputMismatchException(s"signal is of an awkward type: $s")
    }
  }

  private def angleToEscNative(gains: AngularPositionGains,
                               signal: Angle,
                               escTimeConst: Time,
                               encConv: Ratio[Angle, Dimensionless]) = {
    val c = encConv.num.toDegrees / encConv.den.toEach
    import gains.{kd, ki, kp}
    (
      (kp.num.toEach / kp.den.toDegrees) * c,
      (ki.num.toEach / (ki.den / escTimeConst).toDegrees) * c,
      (kd.num.toEach / (kd.den * escTimeConst).toDegrees) * c,
      signal.toDegrees / c
    )
  }

  private def angleToEscNative(gains: AngularVelocityGains,
                               signal: AngularVelocity,
                               escTimeConst: Time,
                               encConv: Ratio[Angle, Dimensionless]) = {
    val c = encConv.num.toDegrees / encConv.den.toEach
    import gains.{kd, kf, ki, kp}
    (
      (kp.num.toEach / (kp.den * escTimeConst).toDegrees) * c,
      (ki.num.toEach / ki.den.toDegrees) * c,
      (kd.num.toEach / (kd.den * escTimeConst * escTimeConst).toDegrees) * c,
      (kf.num.toEach / (kf.den * escTimeConst).toDegrees) * c,
      (signal * escTimeConst).toDegrees / c
    )
  }

  override protected def getControlMode(implicit hardware: Hardware, props: Properties) = NoOperation

  override protected def driveClosedLoop(signal: Stream[TwoSided[OffloadedSignal]])
                                        (implicit hardware: Hardware,
                                         props: Signal[Properties]) = signal

  class Drivetrain(implicit hardware: Hardware, props: Signal[Properties]) extends Component[DriveSignal] {
    override def defaultController: Stream[TwoSided[OffloadedSignal]] = self.defaultController

    override def applySignal(signal: TwoSided[OffloadedSignal]) = output(hardware, signal)
  }
}
