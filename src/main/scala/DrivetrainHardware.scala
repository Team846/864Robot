import java.io.{File, PrintWriter}

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.{FeedbackDevice, NeutralMode, StatusFrameEnhanced, VelocityMeasPeriod}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.frc.TalonEncoder
import com.lynbrookrobotics.potassium.streams._
import com.lynbrookrobotics.potassium.units.Ratio
import squants.space.Turns
import squants.time.Milliseconds
import squants.{Each, Length, Velocity}

class DrivetrainHardware(implicit clock: Clock) {
  // Test with 1 and 10 ms
  val streamPeriod = Milliseconds(10)
  // test with true and false
  val oneMsFrameRate = true

  private val encoderSetting = Ratio(Turns(1), Each(4096))

  // ............................

  val left /*Back*/ = new TalonSRX(50)
  val right /*Back*/ = new TalonSRX(41)
  val leftFollower /*Front*/ = new TalonSRX(51)
  val rightFollower /*Front*/ = new TalonSRX(40)

  val escTimeOut = 0
  val escIdx = 0
  Set(left, right, leftFollower, rightFollower).foreach { it =>
    println(s"Initializing TalonSRX #${it.getDeviceID}")
    it.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, escIdx, escTimeOut)
    it.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_5Ms, escTimeOut)
    it.configVelocityMeasurementWindow(3, escTimeOut)

    it.configPeakOutputReverse(-1, escTimeOut)
    it.configNominalOutputReverse(0, escTimeOut)
    it.configNominalOutputForward(0, escTimeOut)
    it.configPeakOutputForward(1, escTimeOut)

    it.configAllowableClosedloopError(escIdx, 0, escTimeOut)
    it.setSelectedSensorPosition(0, escIdx, escTimeOut)
    it.setNeutralMode(NeutralMode.Coast)

    it.configVoltageCompSaturation(12, escTimeOut)
    it.enableVoltageCompensation(false)

    it.configContinuousCurrentLimit(40, escTimeOut)
    it.configPeakCurrentDuration(0, escTimeOut)
    it.enableCurrentLimit(false)

    import StatusFrameEnhanced._
    Map(
      Status_1_General -> 10,
      Status_2_Feedback0 -> 20,
      Status_12_Feedback1 -> 20,
      Status_3_Quadrature -> 100,
      Status_4_AinTempVbat -> 100
    ).foreach { case (frame, period) =>
      it.setStatusFramePeriod(frame, period, escTimeOut)
    }
  }

  leftFollower.follow(left)
  rightFollower.follow(right)

  left.setInverted(true)
  leftFollower.setInverted(true)

  val leftEncoder = new TalonEncoder(left, encoderSetting)
  val rightEncoder = new TalonEncoder(right, encoderSetting)

  val writer = new PrintWriter(new File(s"/tmp/encang_p${streamPeriod.toMilliseconds}_f$oneMsFrameRate.csv"))
  writer.println("time (sec)\tleft (deg)\tright (deg)")
  private val cancel = Stream.periodic(streamPeriod)((leftEncoder.getAngle, rightEncoder.getAngle))
    .zipWithDt
    .foreach { case ((l, r), t) =>
      writer.println(s"${t.toSeconds}\t${l.toDegrees}\t${r.toDegrees}")
    }

  val leftVelocity: Stream[Velocity] = null
  val rightVelocity: Stream[Velocity] = null
  val leftPosition: Stream[Length] = null
  val rightPosition: Stream[Length] = null
  val track: Length = null
}
