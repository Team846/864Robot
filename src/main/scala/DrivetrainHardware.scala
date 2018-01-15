import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.{FeedbackDevice, VelocityMeasPeriod}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.TalonEncoder
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import squants.space.{Degrees, Inches}
import squants.time.Milliseconds
import squants.{Each, Length, Velocity}

class DrivetrainHardware(implicit props: DrivetrainProperties,
                         clock: Clock)
  extends TwoSidedDriveHardware {

  private val wheelRadius = props.wheelDiameter * 0.5
  override val track = Inches(21.75)

  val left /*Back*/ = new TalonSRX(11)
  val right /*Back*/ = new TalonSRX(13)
  private val leftFront = new TalonSRX(10)
  private val rightFront = new TalonSRX(12)

  val escTimeout = 10
  val escIdx = 0
  val escTimeConst = Milliseconds(100)
  Set(left, right, leftFront, rightFront).foreach { it =>
    it.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTimeout)
    it.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_5Ms, escTimeout)
    it.configVelocityMeasurementWindow(64, escTimeout)

    it.configNominalOutputForward(0, escTimeout)
    it.configNominalOutputReverse(0, escTimeout)
    it.configPeakOutputForward(1, escTimeout)
    it.configPeakOutputReverse(-1, escTimeout)

    it.configAllowableClosedloopError(escIdx, 1, escTimeout)
  }

  leftFront.follow(left)
  rightFront.follow(right)

  val leftEncoder = new TalonEncoder(left, Ratio(Degrees(360), Each(8192)))
  val rightEncoder = new TalonEncoder(right, Ratio(Degrees(360), Each(8192)))

  val period = Milliseconds(100)
  override val leftVelocity: Stream[Velocity] = Stream.periodic(period) {
    leftEncoder.getAngularVelocity onRadius wheelRadius
  }

  override val rightVelocity: Stream[Velocity] = Stream.periodic(period) {
    rightEncoder.getAngularVelocity onRadius wheelRadius
  }

  override val leftPosition: Stream[Length] = Stream.periodic(period) {
    leftEncoder.getAngle onRadius wheelRadius
  }

  override val rightPosition: Stream[Length] = Stream.periodic(period) {
    rightEncoder.getAngle onRadius wheelRadius
  }
}
