import com.ctre.CANTalon
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.commons.drivetrain.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.{TalonEncoder, WPIClock}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import squants.motion.MetersPerSecond
import squants.space.{Degrees, Feet, Inches, Meters}
import squants.time.{Milliseconds, Seconds}
import squants.{Each, Length, Velocity}

class DrivetrainHardware extends TwoSidedDriveHardware {
  val leftFront = new TalonSRX(10) //TalonController
  val rightFront = new TalonSRX(12)
  val leftBack = new TalonSRX(11)
  val rightBack = new TalonSRX(13)

  leftFront.follow(leftBack)
  rightFront.follow(rightBack)

  val clock = WPIClock

  val leftEncoder = new TalonEncoder(leftBack, Ratio(Degrees(360), Each(8192)))
  val rightEncoder = new TalonEncoder(rightBack, Ratio(Degrees(360), Each(8192)))

  val wheelRadius = Inches(3)

  override val leftVelocity: Stream[Velocity] = Stream.periodic(Milliseconds(10)) {
    leftEncoder.getAngularVelocity onRadius wheelRadius
  }(WPIClock)

  override val rightVelocity: Stream[Velocity] =  Stream.periodic(Milliseconds(10)) {
    rightEncoder.getAngularVelocity onRadius wheelRadius
  }(WPIClock)


  override val leftPosition: Stream[Length] =  Stream.periodic(Milliseconds(10)) {
    leftEncoder.getAngle onRadius wheelRadius
  }(WPIClock)
  override val rightPosition: Stream[Length] = Stream.periodic(Milliseconds(10)) {
    rightEncoder.getAngle onRadius wheelRadius
  }(WPIClock)

  override val track: Length = Feet(2.5)
}
