import java.io.{File, PrintWriter}

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.commons.drivetrain.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.{TalonEncoder, WPIClock}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import squants.space.{Degrees, Feet, Inches}
import squants.time.Milliseconds
import squants.{Each, Length, Velocity}

class DrivetrainHardware extends TwoSidedDriveHardware {
  // Test with 1 and 10 ms
  val period = Milliseconds(10)
  // test with true and false
  val isFrameRateSetTo1ms = false

  // ............................

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

  override val leftVelocity: Stream[Velocity] = Stream.periodic(period) {
    leftEncoder.getAngularVelocity onRadius wheelRadius
  }(WPIClock)

  override val rightVelocity: Stream[Velocity] =  Stream.periodic(period) {
    rightEncoder.getAngularVelocity onRadius wheelRadius
  }(WPIClock)

  if (isFrameRateSetTo1ms) {
    println("setting status frame rate to 1 ms")
    leftFront.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 1, 10)
  }
  println(s"cur frame rate is ${leftFront.getStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 10)}")

  val writer = new PrintWriter(new File(s"encoder angle period: ${period.toMilliseconds} frame rate changed: $isFrameRateSetTo1ms.csv"))
  writer.println("time (ms), angle 1 (deg), angle 2(deg)")


  override val leftPosition: Stream[Length] = Stream.periodic(period) {
    val angle = leftEncoder.getAngle
    val ret = angle onRadius wheelRadius

    writer.println(s"${WPIClock.currentTime.toMilliseconds}, ${angle.toDegrees} ${leftEncoder.getAngle.toDegrees}")
    ret
  }(WPIClock)

  override val rightPosition: Stream[Length] = Stream.periodic(period) {
    rightEncoder.getAngle onRadius wheelRadius
  }(WPIClock)

  override val track: Length = Feet(2.5)
}
