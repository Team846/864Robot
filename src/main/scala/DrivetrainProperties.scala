import com.lynbrookrobotics.potassium.commons.drivetrain._
import squants.motion.{AngularVelocity, DegreesPerSecond, FeetPerSecond, FeetPerSecondSquared}
import squants.space.Feet
import squants.{Acceleration, Each, Length, Velocity}

class DrivetrainProperties extends TwoSidedDriveProperties {
  override val maxLeftVelocity: Velocity = FeetPerSecond(15)
  override val maxRightVelocity: Velocity = FeetPerSecond(15)
  override val leftControlGains: ForwardVelocityGains = null
  override val rightControlGains: ForwardVelocityGains = null
  override val maxTurnVelocity: AngularVelocity = DegreesPerSecond(15)
  override val maxAcceleration: Acceleration = FeetPerSecondSquared(4)
  override val defaultLookAheadDistance: Length = Feet(2)
  override val turnControlGains: TurnVelocityGains = null
  override val forwardPositionControlGains: ForwardPositionGains = null
  override val turnPositionControlGains: TurnPositionGains = null
}
