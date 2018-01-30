import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.{NeutralMode, StatusFrameEnhanced}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.{LazyTalon, TalonEncoder}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import squants.space.Inches
import squants.time.Seconds
import squants.{Length, Velocity}

class DrivetrainHardware(implicit props: DrivetrainProperties,
                         clock: Clock,
                         coreTicks: Stream[_])
  extends TwoSidedDriveHardware {

  override val track = Inches(21.75)

  import props._

  val escIdx = 0
  val escTout = 0

  val left /*Back*/ = new LazyTalon(new TalonSRX(leftPort), escIdx, escTout)
  val right /*Back*/ = new LazyTalon(new TalonSRX(rightPort), escIdx, escTout)
  val leftFollower /*Front*/ = new LazyTalon(new TalonSRX(leftFollowerPort), escIdx, escTout)
  val rightFollower /*Front*/ = new LazyTalon(new TalonSRX(rightFollowerPort), escIdx, escTout)

  Set(left, right, leftFollower, rightFollower)
    .map(_.t)
    .foreach { it =>
      it.setNeutralMode(NeutralMode.Coast)
      it.configOpenloopRamp(0, escTout)
      it.configClosedloopRamp(0, escTout)

      it.configPeakOutputReverse(-1, escTout)
      it.configNominalOutputReverse(0, escTout)
      it.configNominalOutputForward(0, escTout)
      it.configPeakOutputForward(1, escTout)
      it.configNeutralDeadband(0.001 /*min*/ , escTout)

      it.configVoltageCompSaturation(11, escTout)
      it.configVoltageMeasurementFilter(32, escTout)
      it.enableVoltageCompensation(true)

      it.configContinuousCurrentLimit(75, escTout)
      it.configPeakCurrentDuration(0, escTout)
      it.enableCurrentLimit(true)

      import StatusFrameEnhanced._
      Map(
        Status_1_General -> 10,
        Status_2_Feedback0 -> 20,
        Status_12_Feedback1 -> 20,
        Status_3_Quadrature -> 100,
        Status_4_AinTempVbat -> 100
      ).foreach { case (frame, period) =>
        it.setStatusFramePeriod(frame, period, escTout)
      }
    }

  leftFollower.t.follow(left.t)
  rightFollower.t.follow(right.t)

  right.t.setInverted(true)
  rightFollower.t.setInverted(true)
  right.t.setSensorPhase(false)

  val leftEncoder = new TalonEncoder(left.t, encoderAngleOverTicks)
  val rightEncoder = new TalonEncoder(right.t, encoderAngleOverTicks)

  private val t = Seconds(1)
  override val leftVelocity: Stream[Velocity] = coreTicks.map { _ =>
    val x = wheelOverEncoderGears * Ratio(leftEncoder.getAngularVelocity * t, t)
    (x.num / x.den) onRadius (wheelDiameter / 2)
  }

  override val rightVelocity: Stream[Velocity] = coreTicks.map { _ =>
    val x = wheelOverEncoderGears * Ratio(rightEncoder.getAngularVelocity * t, t)
    (x.num / x.den) onRadius (wheelDiameter / 2)
  }

  override val leftPosition: Stream[Length] = coreTicks.map { _ =>
    (wheelOverEncoderGears * leftEncoder.getAngle) onRadius (wheelDiameter / 2)
  }

  override val rightPosition: Stream[Length] = coreTicks.map { _ =>
    (wheelOverEncoderGears * rightEncoder.getAngle) onRadius (wheelDiameter / 2)
  }
}
