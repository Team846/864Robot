import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedProperties
import com.lynbrookrobotics.potassium.control.OffloadedSignal.{EscPositionGains, EscVelocityGains}
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.units.GenericValue._
import com.lynbrookrobotics.potassium.units.{Ratio, _}
import squants.motion.{AngularVelocity, DegreesPerSecond, FeetPerSecond, FeetPerSecondSquared}
import squants.space.{Degrees, Feet, Inches}
import squants.time.Seconds
import squants.{Acceleration, Angle, Length, Percent, Velocity}

class DrivetrainProperties extends OffloadedProperties {
  override val maxLeftVelocity: Velocity = FeetPerSecond(21.9)
  override val maxRightVelocity: Velocity = FeetPerSecond(23.1)

  override val leftVelocityGains: ForwardVelocityGains = PIDConfig(
    Ratio(Percent(30), FeetPerSecond(5)),
    Ratio(Percent(0), Feet(1)),
    Ratio(Percent(0), FeetPerSecondSquared(1))
  )

  override val rightVelocityGains: ForwardVelocityGains = PIDConfig(
    Ratio(Percent(30), FeetPerSecond(5)),
    Ratio(Percent(0), Feet(1)),
    Ratio(Percent(0), FeetPerSecondSquared(1))
  )

  override val maxTurnVelocity: AngularVelocity = DegreesPerSecond(15)
  override val maxAcceleration: Acceleration = FeetPerSecondSquared(4)
  override val defaultLookAheadDistance: Length = Feet(2)

  override val turnControlGains: TurnVelocityGains = PIDConfig(
    Ratio(Percent(50), DegreesPerSecond(360)),
    Ratio(Percent(0), Degrees(1)),
    Percent(0) / (DegreesPerSecond(1).toGeneric / Seconds(1))
  )

  override val forwardPositionGains: ForwardPositionGains = PIDConfig(
    Percent(100) / Feet(2),
    Percent(0) / (Feet(1).toGeneric * Seconds(1)),
    Percent(0) / FeetPerSecond(1)
  )

  override val turnPositionGains: TurnPositionGains = PIDConfig(
    Percent(100) / Degrees(90),
    Percent(0) / (Degrees(1).toGeneric * Seconds(1)),
    Percent(0) / DegreesPerSecond(1)
  )

  override val wheelToEncoderGearRatio: Ratio[Angle, Angle] = Ratio(Degrees(1), Degrees(2))
  override val wheelDiameter: Length = Inches(6)

  override val escVelocityGains = EscVelocityGains(0.1, 0, 0, 0)
  override val escPositionGains = EscPositionGains(1, 0, 0)
}
