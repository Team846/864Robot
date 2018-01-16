import java.util.InputMismatchException

import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.NoOperation
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedDrive
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSided
import com.lynbrookrobotics.potassium.control.{OffloadedSignal, OpenLoop, PositionControl, VelocityControl}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.motion.AngularVelocity
import squants.space.Radians
import squants.time.Hours
import squants.{Angle, Dimensionless, Percent, Time}

package object drivetrain extends OffloadedDrive {
  self =>
  override type Properties = DrivetrainProperties
  override type Hardware = DrivetrainHardware

  // TODO: This method is huge, refactor in 3 separate methods
  override protected def output(h: Hardware, s: TwoSided[OffloadedSignal]): Unit = {
    println("HERE")
    s match {
      case TwoSided(OpenLoop(ls), OpenLoop(rs)) =>
        h.left.set(ControlMode.PercentOutput, ls.toEach)
        h.right.set(ControlMode.PercentOutput, rs.toEach)

      case TwoSided(PositionControl(lg, ls), PositionControl(rg, rs)) =>
        import h.{escTimeConst, leftEncoder, rightEncoder}
        val lns = angleToEscNative(
          ls, escTimeConst, leftEncoder.conversionFactor
        )
        val rns = angleToEscNative(
          rs, escTimeConst, rightEncoder.conversionFactor
        )

        import h.{escIdx, escTimeout, left, right}
        left.config_kP(escIdx, lg.p, escTimeout)
        left.config_kI(escIdx, lg.i, escTimeout)
        left.config_kD(escIdx, lg.d, escTimeout)

        right.config_kP(escIdx, rg.p, escTimeout)
        right.config_kI(escIdx, rg.i, escTimeout)
        right.config_kD(escIdx, rg.d, escTimeout)

        left.set(ControlMode.Position, lns)
        right.set(ControlMode.Position, rns)

        println(s"lg: $lg")
        println(s"ls: $ls")
        println(h.rightEncoder)

      case TwoSided(VelocityControl(lg, ls), VelocityControl(rg, rs)) =>
        import h.{escTimeConst, leftEncoder, rightEncoder}
        val lns = angleToEscNative(
          ls, escTimeConst, leftEncoder.conversionFactor
        )
        val rns = angleToEscNative(
          rs, escTimeConst, rightEncoder.conversionFactor
        )

        import h.{escIdx, escTimeout, left, right}
        left.config_kP(escIdx, lg.p, escTimeout)
        left.config_kI(escIdx, lg.i, escTimeout)
        left.config_kD(escIdx, lg.d, escTimeout)
        left.config_kF(escIdx, lg.f, escTimeout)

        right.config_kP(escIdx, rg.p, escTimeout)
        right.config_kI(escIdx, rg.i, escTimeout)
        right.config_kD(escIdx, rg.d, escTimeout)
        right.config_kF(escIdx, rg.f, escTimeout)

        left.set(ControlMode.Velocity, lns)
        right.set(ControlMode.Velocity, rns)

      case _ => throw new InputMismatchException(s"signal is of an awkward type: $s")
    }
  }

  private def angleToEscNative(signal: Angle,
                               escTimeConst: Time,
                               encConv: Ratio[Angle, Dimensionless]) = {
    val c = encConv.num.toDegrees / encConv.den.toEach
    signal.toDegrees / c
  }

  private def angleToEscNative(signal: AngularVelocity,
                               escTimeConst: Time,
                               encConv: Ratio[Angle, Dimensionless]) = {
    val c = encConv.num.toDegrees / encConv.den.toEach
    (signal * escTimeConst).toDegrees / c
  }

  override protected def getControlMode(implicit hardware: Hardware, props: Properties) = NoOperation

  override protected def driveClosedLoop(signal: Stream[TwoSided[OffloadedSignal]])
                                        (implicit hardware: Hardware,
                                         props: Signal[Properties]) = signal

  class Drivetrain(implicit hardware: Hardware, props: Signal[Properties], clock: Clock) extends Component[DriveSignal] {
    override def defaultController: Stream[TwoSided[OffloadedSignal]] = Stream.periodic(Hours(1)) {
      val p = props.get
      TwoSided(
        OpenLoop(Percent(0)), OpenLoop(Percent(0))
      )
    }

    override def applySignal(signal: TwoSided[OffloadedSignal]) = output(hardware, signal)
  }

}
