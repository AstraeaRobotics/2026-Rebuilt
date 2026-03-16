// package frc.robot.subsystems;

// import com.ctre.phoenix6.configs.CANcoderConfiguration;
// import com.ctre.phoenix6.configs.TalonFXConfiguration;
// import com.ctre.phoenix6.controls.PositionVoltage;
// import com.ctre.phoenix6.controls.VoltageOut;
// import com.ctre.phoenix6.hardware.CANcoder;
// import com.ctre.phoenix6.hardware.TalonFX;
// import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
// import com.ctre.phoenix6.signals.NeutralModeValue;
// import com.ctre.phoenix6.signals.SensorDirectionValue;

// import edu.wpi.first.math.controller.ElevatorFeedforward;
// import edu.wpi.first.networktables.DoublePublisher;
// import edu.wpi.first.networktables.NetworkTable;
// import edu.wpi.first.networktables.NetworkTableInstance;
// import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import frc.robot.Constants.ClimbConstants;
// import frc.robot.Constants.ClimbConstants.ClimbStates;

// public class ClimbSubsystem extends SubsystemBase {

//     private final TalonFX m_climbMotor;
//     private final CANcoder m_encoder;

//     private final PositionVoltage m_positionControl;
//     private final VoltageOut m_voltageControl;

//     // Elevator FF since climb is linear (gravity acts constantly)
//     // TODO: Tune kS, kG, kV, kA
//     private final ElevatorFeedforward m_feedforward = new ElevatorFeedforward(
//         ClimbConstants.kClimb_ks,
//         ClimbConstants.kClimb_kg,
//         ClimbConstants.kClimb_kv,
//         ClimbConstants.kClimb_ka
//     );

//     private final DoublePublisher m_climbSetpointPub;
//     private final DoublePublisher m_climbVoltagePub;
//     private final DoublePublisher m_climbPositionPub;

//     private ClimbStates m_climbState;
//     private double m_climbSetpoint;

//     public ClimbSubsystem() {
//         m_climbMotor = new TalonFX(ClimbConstants.kClimbMotor_CANID);
//         m_encoder    = new CANcoder(ClimbConstants.kCANcoder_CANID);

//         m_positionControl = new PositionVoltage(0).withSlot(0);
//         m_voltageControl  = new VoltageOut(0);

//         NetworkTable table    = NetworkTableInstance.getDefault().getTable("Climb Subsystem");
//         m_climbPositionPub    = table.getDoubleTopic("Climb Position").publish();
//         m_climbSetpointPub    = table.getDoubleTopic("Climb Setpoint").publish();
//         m_climbVoltagePub     = table.getDoubleTopic("Climb Voltage").publish();

//         m_climbState   = ClimbStates.kGround;
//         m_climbSetpoint = m_climbState.getClimbSetpoint();

//         configureMotors();
//     }

//   private void configureMotors() {
//     CANcoderConfiguration encoderConfig = new CANcoderConfiguration();
//     encoderConfig.MagnetSensor.SensorDirection = SensorDirectionValue.CounterClockwise_Positive; // TODO: check direction
//     encoderConfig.MagnetSensor.MagnetOffset    = 0.0; // TODO: set magnet offset
//     m_encoder.getConfigurator().apply(encoderConfig);

//     TalonFXConfiguration config = new TalonFXConfiguration();

//     config.CurrentLimits.SupplyCurrentLimit       = 40;
//     config.CurrentLimits.SupplyCurrentLimitEnable = true;
//     config.CurrentLimits.StatorCurrentLimit       = 80;
//     config.CurrentLimits.StatorCurrentLimitEnable = true;

//     config.Slot0.kP = ClimbConstants.kClimb_kP;
//     config.Slot0.kI = ClimbConstants.kClimb_kI;
//     config.Slot0.kD = ClimbConstants.kClimb_kD;

//     config.Feedback.FeedbackRemoteSensorID = ClimbConstants.kCANcoder_CANID;
//     config.Feedback.FeedbackSensorSource   = FeedbackSensorSourceValue.RemoteCANcoder;

//     config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
//     config.MotorOutput.Inverted    = ClimbConstants.kClimbMotorInverted;

//     config.SoftwareLimitSwitch.ForwardSoftLimitEnable    = true;
//     config.SoftwareLimitSwitch.ForwardSoftLimitThreshold = ClimbConstants.kMaxHeight;
//     config.SoftwareLimitSwitch.ReverseSoftLimitEnable    = true;
//     config.SoftwareLimitSwitch.ReverseSoftLimitThreshold = ClimbConstants.kMinHeight;

//     m_climbMotor.getConfigurator().apply(config);
//     m_climbMotor.setPosition(0);
//   }

//     public void setClimbState(ClimbStates state) {
//         m_climbState    = state;
//         m_climbSetpoint = m_climbState.getClimbSetpoint();
//         setPosition(m_climbSetpoint);
//     }

//     public void setPosition(double position) {
//         // FF calculates voltage needed to hold against gravity, PID handles position error
//         double ff = m_feedforward.calculate(0, 0);
//         m_climbMotor.setControl(m_positionControl.withPosition(position).withFeedForward(ff));
//     }

//     public void setVoltage(double voltage) {
//         m_climbMotor.setControl(m_voltageControl.withOutput(voltage));
//     }

//     public void stop() {
//         m_climbMotor.stopMotor();
//     }

//     public double getPosition() {
//         return m_encoder.getAbsolutePosition().getValueAsDouble();
//     }

//     public double getVelocity() {
//         return m_climbMotor.getVelocity().getValueAsDouble();
//     }

//     public ClimbStates getClimbState() {
//         return m_climbState;
//     }

//     public void updateLog() {
//         m_climbPositionPub.set(getPosition());
//         m_climbVoltagePub.set(m_climbMotor.getMotorVoltage().getValueAsDouble());
//         m_climbSetpointPub.set(m_climbSetpoint);
//     }

//     @Override
//     public void periodic() {
//         updateLog();
//     }
// }