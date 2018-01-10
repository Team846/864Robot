import com.ctre.CANTalon
import com.lynbrookrobotics.potassium.commons.drivetrain.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.{TalonEncoder, WPIClock}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import squants.motion.MetersPerSecond
import squants.space.{Degrees, Feet, Inches, Meters}
import squants.time.Seconds
import squants.{Each, Length, Velocity}

class DrivetrainHardware extends TwoSidedDriveHardware {
//  val leftFront = new CANTalon(1)
//  val rightFront = new CANTalon(2)
//  val leftBack = new CANTalon(3)
//  val rightBack = new CANTalon(4)

//  val leftEncoder = new TalonEncoder(leftFront, Ratio(Degrees(10), Each(10)))
//  val rightEncoder = new TalonEncoder(rightFront, Ratio(Degrees(10), Each(10)))

  val wheelRadius = Inches(3)

  override val leftVelocity: Stream[Velocity] = Stream.periodic(Seconds(10)) {
//    println(leftEncoder.getAngularVelocity)
//    leftEncoder.getAngularVelocity onRadius wheelRadius
      MetersPerSecond(0)
  }(WPIClock)

  override val rightVelocity: Stream[Velocity] =  Stream.periodic(Seconds(10)) {
//    println(rightEncoder.getAngularVelocity)
//    rightEncoder.getAngularVelocity onRadius wheelRadius
      MetersPerSecond(0)
  }(WPIClock)


  override val leftPosition: Stream[Length] =  Stream.periodic(Seconds(10)) {
//    leftEncoder.getAngle onRadius wheelRadius
      Meters(1)
  }(WPIClock)
  override val rightPosition: Stream[Length] = Stream.periodic(Seconds(10)) {
//    rightEncoder.getAngle onRadius wheelRadius
    Meters(1)
  }(WPIClock)

  override val track: Length = Feet(4)
}
