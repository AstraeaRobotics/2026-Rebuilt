package frc.robot.subsystems;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.PersistMode;
import com.revrobotics.REVLibError;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.FeedbackSensor;
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
    private final SparkClosedLoopController m_pivotController;

    private IntakeStates m_state = IntakeStates.kHorizontal;

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

        m_pivotEncoder    = m_pivotMotor.getAbsoluteEncoder();
        m_pivotController = m_pivotMotor.getClosedLoopController();

        configureMotors();
    }

    private void configureMotors() {
        SparkMaxConfig intakeConfig = new SparkMaxConfig();
        intakeConfig
            .smartCurrentLimit(35)
            .idleMode(IdleMode.kCoast);

        SparkMaxConfig pivotConfig = new SparkMaxConfig();
        pivotConfig
            .smartCurrentLimit(35)
            .idleMode(IdleMode.kBrake);

        // Use absolute encoder as feedback source
        pivotConfig.closedLoop
            .feedbackSensor(FeedbackSensor.kAbsoluteEncoder)
            .pid(
                IntakeConstants.kPivot_kP,
                IntakeConstants.kPivot_kI,
                IntakeConstants.kPivot_kD
            )
            .feedForward
                .kS(IntakeConstants.kPivot_kS)
                .kCos(IntakeConstants.kPivot_kCos)
                .kCosRatio(IntakeConstants.kPivot_kCosRatio);

        // Soft limits to protect the mechanism
        pivotConfig.softLimit
            .forwardSoftLimit(IntakeConstants.kPivotMaxPosition)
            .forwardSoftLimitEnabled(true)
            .reverseSoftLimit(IntakeConstants.kPivotMinPosition)
            .reverseSoftLimitEnabled(true);

        REVLibError intakeErr = m_intakeMotor.configure(intakeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        REVLibError pivotErr  = m_pivotMotor.configure(pivotConfig,   ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);

        if (intakeErr != REVLibError.kOk) System.err.println("Intake motor config failed: " + intakeErr);
        if (pivotErr  != REVLibError.kOk) System.err.println("Pivot motor config failed: "  + pivotErr);
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

    public double getPivotPosition() {
        return m_pivotEncoder.getPosition();
    }

    private void drivePivot() {
        double setpoint = MathUtil.clamp(
            m_state.getPivotSetpoint(),
            IntakeConstants.kPivotMinPosition,
            IntakeConstants.kPivotMaxPosition
        );
        m_pivotController.setReference(setpoint, ControlType.kPosition);
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