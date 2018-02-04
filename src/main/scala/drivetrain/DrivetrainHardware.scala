package drivetrain

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced._
import com.ctre.phoenix.motorcontrol._
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.{LazyTalon, TalonEncoder}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import drivetrain.TalonManager._
import squants.space.Inches
import squants.time.Seconds
import squants.{Length, Velocity}

class DrivetrainHardware(implicit props: DrivetrainProperties,
                         clock: Clock,
                         coreTicks: Stream[_])
  extends TwoSidedDriveHardware {

  override val track = Inches(21.75)

  import props._

  private val escIdx = 0
  private val escTout = 0

  val left /*Back*/ = new LazyTalon(new TalonSRX(leftPort), escIdx, escTout)
  val right /*Back*/ = new LazyTalon(new TalonSRX(rightPort), escIdx, escTout)
  val leftFollower /*Front*/ = new LazyTalon(new TalonSRX(leftFollowerPort), escIdx, escTout)
  val rightFollower /*Front*/ = new LazyTalon(new TalonSRX(rightFollowerPort), escIdx, escTout)

  Set(left, right, leftFollower, rightFollower)
    .map(_.t)
    .foreach(setToDefault)

  leftFollower.t.follow(left.t)
  rightFollower.t.follow(right.t)

  right.t.setInverted(true)
  rightFollower.t.setInverted(true)
  right.t.setSensorPhase(false)

  val leftEncoder = new TalonEncoder(left.t, encoderAngleOverTicks)
  left.t.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTout)
  val rightEncoder = new TalonEncoder(right.t, encoderAngleOverTicks)
  right.t.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTout)


  StatusFrame.values().foreach { it =>
    right.t.setStatusFramePeriod(it, 1000, escTout)
    left.t.setStatusFramePeriod(it, 1000, escTout)
    rightFollower.t.setStatusFramePeriod(it, 1000, escTout)
    leftFollower.t.setStatusFramePeriod(it, 1000, escTout)
  }

  Set(left, right).foreach { it =>
    it.t.setStatusFramePeriod(Status_1_General, 5, escTout)
    it.t.setStatusFramePeriod(Status_2_Feedback0, 10, escTout)

    it.t.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_5Ms, escTout)
    it.t.configVelocityMeasurementWindow(4, escTout)
  }


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
