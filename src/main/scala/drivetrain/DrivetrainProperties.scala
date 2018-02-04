package drivetrain

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedProperties
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.control.offload.EscConfig
import com.lynbrookrobotics.potassium.units.GenericValue._
import com.lynbrookrobotics.potassium.units.{Ratio, _}
import squants.motion._
import squants.space.{Feet, Inches, Turns}
import squants.time._
import squants.{Acceleration, Angle, Dimensionless, Each, Length, Percent, Velocity}

class DrivetrainProperties extends OffloadedProperties {
  val leftPort /*Back*/ = 50
  val rightPort /*Back*/ = 41
  val leftFollowerPort /*Front*/ = 51
  val rightFollowerPort /*Front*/ = 40

  override val maxLeftVelocity: Velocity = FeetPerSecond(18.8)
  override val maxRightVelocity: Velocity = FeetPerSecond(19.25)

  override val leftVelocityGains: ForwardVelocityGains = PIDConfig(
    Ratio(Percent(40), FeetPerSecond(5)),
    Ratio(Percent(0), Feet(5)),
    Ratio(Percent(0), FeetPerSecondSquared(5))
  )
  override val rightVelocityGains: ForwardVelocityGains = leftVelocityGains

  override val forwardPositionGains: ForwardPositionGains = PIDConfig(
    Percent(0) / Feet(5),
    Percent(0) / (Feet(5) * Seconds(1)),
    Percent(0) / FeetPerSecond(5)
  )

  override val turnVelocityGains: TurnVelocityGains = null
  override val turnPositionGains: TurnPositionGains = null
  override val maxTurnVelocity: AngularVelocity = null
  override val maxAcceleration: Acceleration = null
  override val defaultLookAheadDistance: Length = null
  override val blendExponent: Double = 0
  override val track: Length = null

  override val wheelDiameter: Length = Inches(4)
  override val wheelOverEncoderGears: Ratio[Angle, Angle] = Ratio(Turns(1), Turns(2))
  override val encoderAngleOverTicks: Ratio[Angle, Dimensionless] = Ratio(Turns(1), Each(4096))
  override val escConfig: EscConfig[Length] = EscConfig(
    ticksPerUnit = floorPerTick.recip
  )
}
