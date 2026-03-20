package frc.robot.subsystems;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.PersistMode;
import com.revrobotics.REVLibError;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.IntakeConstants.IntakeStates;

public class IntakeSubsystem extends SubsystemBase {

    private final SparkMax        m_intakeMotor;
    private final SparkMax        m_pivotMotor;
    private final AbsoluteEncoder m_pivotEncoder;

    // ── Control ──────────────────────────────────────────────────────────────
    private final PIDController m_pivotPID;
    private boolean             m_closedLoop = false;

    // ── Telemetry ────────────────────────────────────────────────────────────
    private final DoublePublisher m_intakeVoltagePub;
    private final DoublePublisher m_pivotVoltagePub;
    private final DoublePublisher m_pivotPositionPub;
    private final DoublePublisher m_pivotSetpointPub;
    private final DoublePublisher m_pivotErrorPub;
    private final StringPublisher m_statePub;

    private IntakeStates m_desiredState = IntakeStates.kIn;

    public IntakeSubsystem() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Intake");
        m_intakeVoltagePub = table.getDoubleTopic("Intake Voltage").publish();
        m_pivotVoltagePub  = table.getDoubleTopic("Pivot Voltage").publish();
        m_pivotPositionPub = table.getDoubleTopic("Pivot Position").publish();
        m_pivotSetpointPub = table.getDoubleTopic("Pivot Setpoint").publish();
        m_pivotErrorPub    = table.getDoubleTopic("Pivot Error").publish();
        m_statePub         = table.getStringTopic("State").publish();

        m_intakeMotor  = new SparkMax(IntakeConstants.kIntakeMotor_CANID, MotorType.kBrushless);
        m_pivotMotor   = new SparkMax(IntakeConstants.kPivotMotor_CANID,  MotorType.kBrushless);
        m_pivotEncoder = m_pivotMotor.getAbsoluteEncoder();

        m_pivotPID = new PIDController(
            IntakeConstants.kPivot_kP,
            IntakeConstants.kPivot_kI,
            IntakeConstants.kPivot_kD
        );
        m_pivotPID.setTolerance(0.01);

        configureMotors();
    }

    private void configureMotors() {
        SparkMaxConfig intakeConfig = new SparkMaxConfig();
        intakeConfig.smartCurrentLimit(35).idleMode(IdleMode.kCoast);

        SparkMaxConfig pivotConfig = new SparkMaxConfig();
        pivotConfig.smartCurrentLimit(60).idleMode(IdleMode.kBrake);

        REVLibError intakeErr = m_intakeMotor.configure(intakeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        REVLibError pivotErr  = m_pivotMotor.configure(pivotConfig,   ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        if (intakeErr != REVLibError.kOk) System.err.println("Intake motor config failed: " + intakeErr);
        if (pivotErr  != REVLibError.kOk) System.err.println("Pivot motor config failed: "  + pivotErr);
    }

    // ── State control ────────────────────────────────────────────────────────

    public void setState(IntakeStates state) {
        m_desiredState = state;
        m_pivotPID.setSetpoint(state.getPivotSetpoint());
        m_closedLoop = true;
    }

    public IntakeStates getState() {
        return m_desiredState;
    }

    public boolean atSetpoint() {
        return m_pivotPID.atSetpoint();
    }

    // ── Intake roller ────────────────────────────────────────────────────────

    public void spinIntake(double voltage) {
        m_intakeMotor.setVoltage(voltage);
    }

    public void stopIntake() {
        m_intakeMotor.setVoltage(0.0);
    }

    // ── Pivot open-loop (for manual tuning / override) ───────────────────────

    public void runPivot(double voltage) {
        m_closedLoop = false;
        m_pivotMotor.setVoltage(voltage);
    }

    public void stopPivot() {
        m_closedLoop = false;
        m_pivotMotor.setVoltage(0.0);
    }

    public double getPivotPosition() {
        return m_pivotEncoder.getPosition();
    }

    // ── Telemetry helpers ────────────────────────────────────────────────────

    public double getIntakeVoltage() {
        return m_intakeMotor.getAppliedOutput() * m_intakeMotor.getBusVoltage();
    }

    public double getPivotVoltage() {
        return m_pivotMotor.getAppliedOutput() * m_pivotMotor.getBusVoltage();
    }

    // ── Periodic ─────────────────────────────────────────────────────────────

    @Override
    public void periodic() {
        double position = getPivotPosition();

        if (m_closedLoop) {
            double voltage = MathUtil.clamp(m_pivotPID.calculate(position), -12.0, 12.0);
            m_pivotMotor.setVoltage(voltage);
        }

        m_intakeVoltagePub.set(getIntakeVoltage());
        m_pivotVoltagePub.set(getPivotVoltage());
        m_pivotPositionPub.set(position);
        m_pivotSetpointPub.set(m_desiredState.getPivotSetpoint());
        m_pivotErrorPub.set(m_pivotPID.getError());
        m_statePub.set(m_desiredState.name());
    }
}