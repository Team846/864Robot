import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.{FeedbackDevice, NeutralMode, StatusFrameEnhanced, VelocityMeasPeriod}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.TalonEncoder
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

  val left /*Back*/ = new TalonSRX(50)
  val right /*Back*/ = new TalonSRX(41)
  val leftFollower /*Front*/ = new TalonSRX(51)
  val rightFollower /*Front*/ = new TalonSRX(40)

  import props._

  Set(left, right, leftFollower, rightFollower).foreach { it =>
    it.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTout)
    it.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_5Ms, escTout)
    it.configVelocityMeasurementWindow(3, escTout)

    it.configPeakOutputReverse(-1, escTout)
    it.configNominalOutputReverse(0, escTout)
    it.configNominalOutputForward(0, escTout)
    it.configPeakOutputForward(1, escTout)

    it.configAllowableClosedloopError(escIdx, 0, escTout)
    it.setSelectedSensorPosition(0, escIdx, escTout)
    it.setNeutralMode(NeutralMode.Coast)

    it.configVoltageCompSaturation(12, escTout)
    it.enableVoltageCompensation(false)

    it.configContinuousCurrentLimit(40, escTout)
    it.configPeakCurrentDuration(0, escTout)
    it.enableCurrentLimit(false)

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

  leftFollower.follow(left)
  rightFollower.follow(right)

  left.setInverted(false)
  leftFollower.setInverted(false)
  right.setInverted(false)
  rightFollower.setInverted(false)

  val leftEncoder = new TalonEncoder(left, props.encoderAngleOverTicks)
  val rightEncoder = new TalonEncoder(right, props.encoderAngleOverTicks)
  left.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 10, escTout)
  right.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 10, escTout)
  left.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 10, escTout)
  right.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 10, escTout)

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
