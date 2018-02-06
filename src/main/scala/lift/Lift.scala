package lift

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.{FeedbackDevice, NeutralMode}
import com.lynbrookrobotics.potassium.Component
import com.lynbrookrobotics.potassium.commons.lift.LiftHardware
import com.lynbrookrobotics.potassium.commons.lift.offloaded.{OffloadedLift, OffloadedProperties}
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.control.offload.EscConfig._
import com.lynbrookrobotics.potassium.control.offload.OffloadedSignal.OpenLoop
import com.lynbrookrobotics.potassium.control.offload.{EscConfig, OffloadedSignal}
import com.lynbrookrobotics.potassium.frc.LazyTalon
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.GenericValue._
import com.lynbrookrobotics.potassium.units._
import drivetrain.TalonManager
import squants.motion.FeetPerSecond
import squants.space.{Feet, Inches, Length}
import squants.time.Seconds
import squants.{Each, Percent, QuantityRange}

object Lift extends OffloadedLift {

  class Hardware(implicit val coreTicks: Stream[_], val p: Properties) extends LiftHardware {
    private val tOut = 0
    private val idx = 0
    val master = new LazyTalon(new TalonSRX(p.masterPort), idx, tOut,
      defaultPeakOutputReverse = -1, defaultPeakOutputForward = 1
    )
    val slave = new LazyTalon(new TalonSRX(p.slavePort), idx, tOut,
      defaultPeakOutputReverse = -1, defaultPeakOutputForward = 1
    )
    TalonManager.setToDefault(master.t)
    TalonManager.setToDefault(slave.t)

    slave.t.follow(master.t)
    slave.t.setInverted(true)
    master.t.setInverted(true)

    master.t.setNeutralMode(NeutralMode.Brake)
    slave.t.setNeutralMode(NeutralMode.Brake)

    master.t.setSensorPhase(false) // increase as lift moves up
    master.t.configSelectedFeedbackSensor(FeedbackDevice.Analog, idx, tOut)
    private val sc = master.t.getSensorCollection
    println(s"master.t.setSelectedSensorPosition(${720 - sc.getAnalogInRaw}, idx, tOut)") // DO NOT REMOVE THIS LINE
    master.t.setSelectedSensorPosition(720 - sc.getAnalogInRaw, idx, tOut)

    implicit val escConfig: EscConfig[Length] = p.escConfig
    private val forwardLim = ticks(p.safeRange.upper).toEach.toInt
    private val reverseLim = ticks(p.safeRange.lower).toEach.toInt
    println(s"forward limit = $forwardLim, reverse limit = $reverseLim")
    master.t.configForwardSoftLimitThreshold(forwardLim, tOut)
    master.t.configReverseSoftLimitThreshold(reverseLim, tOut)
    master.t.configForwardSoftLimitEnable(true, tOut)
    master.t.configReverseSoftLimitEnable(true, tOut)

    //    master.t.configContinuousCurrentLimit(20, tOut)
    //    master.t.configPeakCurrentDuration(0, tOut)
    //    slave.t.configContinuousCurrentLimit(20, tOut)
    //    slave.t.configPeakCurrentDuration(0, tOut)

    override def position: Stream[Length] = coreTicks.map { _ =>
      p.escConfig.ticksPerUnit.recip * Each(master.t.getSelectedSensorPosition(idx))
    }
  }

  class Properties extends OffloadedProperties {
    val masterPort = 5
    val slavePort = 7

    val safeRange = QuantityRange(Inches(10.5), Inches(10.5) + Feet(1))

    override def positionGains = PIDConfig(
      Percent(100) / Inches(6),
      Percent(0) / (Inches(1) * Seconds(1)),
      Percent(0) / FeetPerSecond(1)
    )

    override val escConfig = EscConfig(
      ticksPerUnit = Ratio(Each(587 - 446), Inches(12))
    )
  }

  class Comp(implicit coreTicks: Stream[_], h: Hardware) extends Component[OffloadedSignal] {
    override def defaultController: Stream[OffloadedSignal] =
      coreTicks.mapToConstant(OpenLoop(Percent(0)))

    override def applySignal(signal: OffloadedSignal): Unit = {
      h.master.applyCommand(signal)
    }
  }

}
