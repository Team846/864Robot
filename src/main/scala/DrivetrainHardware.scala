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

  import props._

  val left /*Back*/ = new TalonSRX(leftPort)
  val right /*Back*/ = new TalonSRX(rightPort)
  val leftFollower /*Front*/ = new TalonSRX(leftFollowerPort)
  val rightFollower /*Front*/ = new TalonSRX(rightFollowerPort)

  val escIdx = 0
  val escTout = 0
  
  Set(left, right, leftFollower, rightFollower).foreach { it =>
    it.setNeutralMode(NeutralMode.Coast)
    it.configOpenloopRamp(0, escTout)
    it.configClosedloopRamp(0, escTout)

    it.configPeakOutputReverse(-1, escTout)
    it.configNominalOutputReverse(0, escTout)
    it.configNominalOutputForward(0, escTout)
    it.configPeakOutputForward(1, escTout)

    it.configNeutralDeadband(0.001 /*min*/ , escTout)
    it.configVoltageCompSaturation(11, escTout)
    it.configVoltageMeasurementFilter(1, escTout)
    it.enableVoltageCompensation(false)

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

  right.setInverted(true)
  rightFollower.setInverted(true)
  right.setSensorPhase(false)

  val leftEncoder = new TalonEncoder(left, encoderAngleOverTicks)
  val rightEncoder = new TalonEncoder(right, encoderAngleOverTicks)

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
