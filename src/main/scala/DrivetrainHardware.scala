import java.io.{File, PrintWriter}

import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.frc.{TalonEncoder, WPIClock}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import squants.space.{Degrees, Feet, Inches}
import squants.time.Milliseconds
import squants.{Each, Length, Velocity}

class DrivetrainHardware(implicit clock: Clock) {
  // Test with 1 and 10 ms
  val period = Milliseconds(1)
  // test with true and false
  val isFrameRateSetTo1ms = true

  // ............................

  val leftFront = new TalonSRX(51)
  val rightFront = new TalonSRX(41)
  val leftBack = new TalonSRX(50)
  val rightBack = new TalonSRX(40)

  leftFront.follow(leftBack)
  rightFront.follow(rightBack)

  val leftEncoder = new TalonEncoder(leftBack, Ratio(Degrees(360), Each(8192)))
  val rightEncoder = new TalonEncoder(rightBack, Ratio(Degrees(360), Each(8192)))

  val wheelRadius = Inches(3)

  val leftVelocity: Stream[Velocity] = Stream.periodic(period) {
    leftEncoder.getAngularVelocity onRadius wheelRadius
  }

  val rightVelocity: Stream[Velocity] = Stream.periodic(period) {
    rightEncoder.getAngularVelocity onRadius wheelRadius
  }

  if (isFrameRateSetTo1ms) {
    println("setting status frame rate to 1 ms")
    leftFront.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 1, 10)
  }
  println(s"cur frame rate is ${leftFront.getStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 10)}")

  val writer = new PrintWriter(new File(s"/tmp/encang_p${period.toMilliseconds}_f$isFrameRateSetTo1ms.csv"))
  writer.println("time (ms)\tangle 1 (deg)\tangle 2(deg)")


  val leftPosition: Stream[Length] = Stream.periodic(period) {
    val angle = leftEncoder.getAngle
    val ret = angle onRadius wheelRadius

    writer.println(s"${WPIClock.currentTime.toMilliseconds}\t${angle.toDegrees}\t${leftEncoder.getAngle.toDegrees}")
    ret
  }

  val rightPosition: Stream[Length] = Stream.periodic(period) {
    rightEncoder.getAngle onRadius wheelRadius
  }

  val track: Length = Feet(2.5)
}
