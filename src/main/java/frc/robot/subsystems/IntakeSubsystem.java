package frc.robot.subsystems;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.IntakeConstants.IntakeStates;

public class IntakeSubsystem extends SubsystemBase {

    private final SparkMax m_intakeMotor;
    private final SparkMax m_pivotMotor;

    private final AbsoluteEncoder m_pivotEncoder;

    // TODO: Tune kP, kI, kD for pivot control
    private final ProfiledPIDController m_pivotPID = new ProfiledPIDController(
        0.0, 0.0, 0.0,
        new TrapezoidProfile.Constraints(0.0, 0.0) // TODO: Tune constraints
    );

    // TODO: Tune kS, kG, kV for pivot feedforward
    private final ArmFeedforward m_pivotFF = new ArmFeedforward(0.0, 0.0, 0.0);

    private IntakeStates m_state = IntakeStates.kIn;

    private final DoublePublisher m_intakeVoltagePub;
    private final DoublePublisher m_pivotVoltagePub;
    private final DoublePublisher m_pivotPositionPub;
    private final DoublePublisher m_pivotSetpointPub;

    public IntakeSubsystem() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Intake Subsystem");
        m_intakeVoltagePub  = table.getDoubleTopic("Intake Voltage").publish();
        m_pivotVoltagePub   = table.getDoubleTopic("Pivot Voltage").publish();
        m_pivotPositionPub  = table.getDoubleTopic("Pivot Position").publish();
        m_pivotSetpointPub  = table.getDoubleTopic("Pivot Setpoint").publish();

        m_intakeMotor = new SparkMax(IntakeConstants.kIntakeMotor_CANID, MotorType.kBrushless);
        m_pivotMotor  = new SparkMax(IntakeConstants.kPivotMotor_CANID, MotorType.kBrushless);

        m_pivotEncoder = m_pivotMotor.getAbsoluteEncoder();

        m_pivotPID.enableContinuousInput(0, 1);

        configureMotors();
    }

    private void configureMotors() {
        SparkMaxConfig intakeConfig = new SparkMaxConfig();
        intakeConfig.smartCurrentLimit(35).idleMode(IdleMode.kCoast);

        SparkMaxConfig pivotConfig = new SparkMaxConfig();
        pivotConfig.smartCurrentLimit(35).idleMode(IdleMode.kBrake);

        m_intakeMotor.configure(intakeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_pivotMotor.configure(pivotConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void setState(IntakeStates state) {
        m_state = state;
    }

    public IntakeStates getState() {
        return m_state;
    }

    public void spinIntake(double voltage) {
        m_intakeMotor.setVoltage(voltage);
    }

    public void stopIntake() {
        m_intakeMotor.setVoltage(0.0);
    }

    public double getPivotPosition() {
        return m_pivotEncoder.getPosition();
    }

    public double getIntakeVoltage() {
        return m_intakeMotor.getAppliedOutput() * m_intakeMotor.getBusVoltage();
    }

    public double getPivotVoltage() {
        return m_pivotMotor.getAppliedOutput() * m_pivotMotor.getBusVoltage();
    }

    public boolean pivotAtSetpoint() {
        return m_pivotPID.atSetpoint();
    }

    private double getPivotOutput() {
        return MathUtil.clamp(
            m_pivotPID.calculate(getPivotPosition(), m_state.getPivotSetpoint())
            + m_pivotFF.calculate(m_state.getPivotSetpoint() * 2 * Math.PI, 0),
            -6, 6
        );
    }

    private void updateLog() {
        m_intakeVoltagePub.set(getIntakeVoltage());
        m_pivotVoltagePub.set(getPivotVoltage());
        m_pivotPositionPub.set(getPivotPosition());
        m_pivotSetpointPub.set(m_state.getPivotSetpoint());
    }

    @Override
    public void periodic() {
        m_pivotMotor.setVoltage(getPivotOutput());
        updateLog();
    }
}