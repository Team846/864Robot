import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.offloaded.OffloadedProperties
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.units.GenericValue._
import com.lynbrookrobotics.potassium.units.{Ratio, _}
import squants.motion._
import squants.space.{Feet, Inches, Turns}
import squants.time._
import squants.{Acceleration, Angle, Dimensionless, Each, Length, Percent, Time, Velocity}

class DrivetrainProperties extends OffloadedProperties {
  val leftPort /*Back*/ = 50
  val rightPort /*Back*/ = 41
  val leftFollowerPort /*Front*/ = 51
  val rightFollowerPort /*Front*/ = 40

  override val maxLeftVelocity: Velocity = FeetPerSecond(18.5)
  override val maxRightVelocity: Velocity = FeetPerSecond(18.4)

  override val leftVelocityGains: ForwardVelocityGains = PIDConfig(
    Ratio(Percent(20), FeetPerSecond(5)),
    Ratio(Percent(0), Feet(5)),
    Ratio(Percent(0), FeetPerSecondSquared(5))
  )
  override val rightVelocityGains: ForwardVelocityGains = leftVelocityGains

  override val forwardPositionGains: ForwardPositionGains = PIDConfig(
    Percent(0) / Feet(5),
    Percent(0) / (Feet(5) * Seconds(1)),
    Percent(0) / FeetPerSecond(5)
  )

  override val turnControlGains: TurnVelocityGains = null
  override val turnPositionGains: TurnPositionGains = null
  override val maxTurnVelocity: AngularVelocity = null
  override val maxAcceleration: Acceleration = null
  override val defaultLookAheadDistance: Length = null

  override val escTimeConst: Time = Milliseconds(100)
  override val wheelDiameter: Length = Inches(4)
  override val wheelOverEncoderGears: Ratio[Angle, Angle] = Ratio(Turns(1), Turns(2))
  override val encoderAngleOverTicks: Ratio[Angle, Dimensionless] = Ratio(Turns(1), Each(4096))
  override val escNativeOutputOverPercent: Ratio[Dimensionless, Dimensionless] = Ratio(Each(1023), Percent(100))
}
