import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveHardware
import com.lynbrookrobotics.potassium.frc.{TalonController, TalonEncoder, WPIClock}
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import squants.space.{Degrees, Feet, Inches}
import squants.time.Milliseconds
import squants.{Each, Length, Velocity}

class DrivetrainHardware extends TwoSidedDriveHardware {
  val leftFront = new TalonController(51) //TalonController
  val rightFront = new TalonController(40)
  val leftBack = new TalonController(50)
  val rightBack = new TalonController(41)

  leftFront.follow(leftBack)
  rightFront.follow(rightBack)

  leftBack.talon.setInverted(true)
  leftFront.talon.setInverted(true)

  val clock = WPIClock

  val leftEncoder = new TalonEncoder(leftBack, Ratio(Degrees(360), Each(8192)))
  val rightEncoder = new TalonEncoder(rightBack, Ratio(Degrees(360), Each(8192)))

  val wheelRadius = Inches(3)

  val core = Stream.periodic(Milliseconds(10))(())(clock)

  override val leftVelocity: Stream[Velocity] = core.map { _ =>
    leftEncoder.getAngularVelocity onRadius wheelRadius
  }

  override val rightVelocity: Stream[Velocity] = core.map { _ =>
    rightEncoder.getAngularVelocity onRadius wheelRadius
  }

  override val leftPosition: Stream[Length] = core.map { _ =>
    leftEncoder.getAngle onRadius wheelRadius
  }

  override val rightPosition: Stream[Length] = core.map { _ =>
    rightEncoder.getAngle onRadius wheelRadius
  }

  override val track: Length = Feet(2.5)
}
