package drivetrain

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.{NeutralMode, StatusFrameEnhanced}

object TalonManager {
  private val tOut = 0

  def setToDefault(it: TalonSRX): Unit = {
    it.setNeutralMode(NeutralMode.Coast)
    it.configOpenloopRamp(0, tOut)
    it.configClosedloopRamp(0, tOut)

    it.configPeakOutputReverse(-1, tOut)
    it.configNominalOutputReverse(0, tOut)
    it.configNominalOutputForward(0, tOut)
    it.configPeakOutputForward(1, tOut)
    it.configNeutralDeadband(0.001 /*min*/ , tOut)

    it.configVoltageCompSaturation(11, tOut)
    it.configVoltageMeasurementFilter(32, tOut)
    it.enableVoltageCompensation(true)

    it.configContinuousCurrentLimit(75, tOut)
    it.configPeakCurrentDuration(0, tOut)
    it.enableCurrentLimit(true)

    import StatusFrameEnhanced._
    Map(
      Status_1_General -> 10,
      Status_2_Feedback0 -> 20,
      Status_12_Feedback1 -> 20,
      Status_3_Quadrature -> 100,
      Status_4_AinTempVbat -> 100
    ).foreach { case (frame, period) =>
      it.setStatusFramePeriod(frame, period, tOut)
    }
  }
}