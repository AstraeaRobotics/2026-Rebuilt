package frc.robot.subsystems;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.math.MathUtil;
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

    private IntakeStates m_state = IntakeStates.kIn;

    private final DoublePublisher m_intakeVoltagePub;
    private final DoublePublisher m_pivotVoltagePub;
    private final DoublePublisher m_pivotPositionPub;
    private final DoublePublisher m_pivotSetpointPub;

    public IntakeSubsystem() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Intake");
        m_intakeVoltagePub = table.getDoubleTopic("Intake Voltage").publish();
        m_pivotVoltagePub  = table.getDoubleTopic("Pivot Voltage").publish();
        m_pivotPositionPub = table.getDoubleTopic("Pivot Position").publish();
        m_pivotSetpointPub = table.getDoubleTopic("Pivot Setpoint").publish();

        m_intakeMotor = new SparkMax(IntakeConstants.kIntakeMotor_CANID, MotorType.kBrushless);
        m_pivotMotor  = new SparkMax(IntakeConstants.kPivotMotor_CANID,  MotorType.kBrushless);

        m_pivotEncoder = m_pivotMotor.getAbsoluteEncoder();

        configureMotors();
    }

    private void configureMotors() {
        SparkMaxConfig intakeConfig = new SparkMaxConfig();
        intakeConfig.smartCurrentLimit(35).idleMode(IdleMode.kCoast);

        SparkMaxConfig pivotConfig = new SparkMaxConfig();
        pivotConfig.smartCurrentLimit(35).idleMode(IdleMode.kBrake);

        m_intakeMotor.configure(intakeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_pivotMotor.configure(pivotConfig,   ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    // ── State ────────────────────────────────────────────────────────────────

    public void setState(IntakeStates state) {
        m_state = state;
    }

    public IntakeStates getState() {
        return m_state;
    }

    // ── Intake roller ────────────────────────────────────────────────────────

    public void spinIntake(double voltage) {
        m_intakeMotor.setVoltage(voltage);
    }

    public void stopIntake() {
        m_intakeMotor.setVoltage(0.0);
    }

    // ── Pivot ────────────────────────────────────────────────────────────────

    /** Returns the absolute encoder position (0–1 rotations). */
    public double getPivotPosition() {
        return m_pivotEncoder.getPosition();
    }

    /**
     * Drives the pivot open-loop toward the current state's setpoint.
     * A small proportional-ish effort moves it; when it's close we just
     * hold with a small voltage so gravity doesn't back-drive it.
     *
     * Clamped so the pivot can never command past [kPivotMinPosition, kPivotMaxPosition].
     */
    private void drivePivot() {
        double position = getPivotPosition();
        double setpoint = MathUtil.clamp(
            m_state.getPivotSetpoint(),
            IntakeConstants.kPivotMinPosition,
            IntakeConstants.kPivotMaxPosition
        );

        double error = setpoint - position;

        // Very simple bang-ish drive: you can replace with a real kP later
        double output;
        if (Math.abs(error) < 0.01) {
            // Close enough – hold with a small voltage  TODO: tune
            output = IntakeConstants.kPivotHoldVoltage * Math.signum(error);
        } else {
            // Drive toward setpoint  TODO: tune this multiplier or swap for PID
            output = MathUtil.clamp(error * 12.0, -6.0, 6.0);
        }

        m_pivotMotor.setVoltage(output);
    }

    // ── Telemetry ────────────────────────────────────────────────────────────

    public double getIntakeVoltage() {
        return m_intakeMotor.getAppliedOutput() * m_intakeMotor.getBusVoltage();
    }

    public double getPivotVoltage() {
        return m_pivotMotor.getAppliedOutput() * m_pivotMotor.getBusVoltage();
    }

    private void updateLog() {
        m_intakeVoltagePub.set(getIntakeVoltage());
        m_pivotVoltagePub.set(getPivotVoltage());
        m_pivotPositionPub.set(getPivotPosition());
        m_pivotSetpointPub.set(m_state.getPivotSetpoint());
    }

    // ── Periodic ─────────────────────────────────────────────────────────────

    @Override
    public void periodic() {
        drivePivot();
        updateLog();
    }
}