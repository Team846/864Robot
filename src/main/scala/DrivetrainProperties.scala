import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveProperties
import com.lynbrookrobotics.potassium.control.PIDConfig
import squants.motion._
import squants.space._
import squants.{Acceleration, Length, Percent, Velocity}
import com.lynbrookrobotics.potassium.units._
import com.lynbrookrobotics.potassium.units.GenericValue._
import squants.time.Seconds

class DrivetrainProperties extends TwoSidedDriveProperties {
  override val maxLeftVelocity: Velocity = FeetPerSecond(21.9)
  override val maxRightVelocity: Velocity = FeetPerSecond(23.1)
  override val leftControlGains: ForwardVelocityGains = PIDConfig(
    Percent(100) / FeetPerSecond(5),
    Percent(0) / Feet(1),
    Percent(0) / FeetPerSecondSquared(1)
  )
  override val rightControlGains: ForwardVelocityGains = PIDConfig(
    Percent(100) / FeetPerSecond(5),
    Percent(0) / Feet(1),
    Percent(0) / FeetPerSecondSquared(1)
  )
  override val maxTurnVelocity: AngularVelocity = DegreesPerSecond(5)
  override val maxAcceleration: Acceleration = FeetPerSecondSquared(4)
  override val defaultLookAheadDistance: Length = Feet(2)
  override val turnControlGains: TurnVelocityGains = null
  override val forwardPositionControlGains: ForwardPositionGains = PIDConfig(
    Percent(100) / Feet(4),
    Percent(0) / (Feet(1) * Seconds(1)),
    Percent(0) / FeetPerSecond(1)
  )
  override val turnPositionControlGains: TurnPositionGains = PIDConfig(
    Percent(100) / Degrees(50),
    Percent(0) / (Degrees(180) * Seconds(1)),
    Percent(0) / DegreesPerSecond(1)
  )
}
