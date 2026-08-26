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
import edu.wpi.first.math.controller.ArmFeedforward;
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

    // ── Telemetry ────────────────────────────────────────────────────────────
    private final DoublePublisher m_intakeVoltagePub;

    public IntakeSubsystem() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Intake");
        m_intakeVoltagePub = table.getDoubleTopic("Intake Voltage").publish();
        m_intakeMotor  = new SparkMax(IntakeConstants.kIntakeMotor_CANID, MotorType.kBrushless);
        

        configureMotors();
    }

    private void configureMotors() {
        SparkMaxConfig intakeConfig = new SparkMaxConfig();
        intakeConfig.smartCurrentLimit(35).idleMode(IdleMode.kCoast);

        REVLibError intakeErr = m_intakeMotor.configure(intakeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        if (intakeErr != REVLibError.kOk) System.err.println("Intake motor config failed: " + intakeErr);
    }

    // ── Intake roller ────────────────────────────────────────────────────────

    public void spinIntake(double voltage) {
        m_intakeMotor.setVoltage(voltage);
    }

    public void stopIntake() {
        m_intakeMotor.setVoltage(0.0);
    }

    // ── Telemetry helpers ────────────────────────────────────────────────────

    public double getIntakeVoltage() {
        return m_intakeMotor.getAppliedOutput() * m_intakeMotor.getBusVoltage();
    }

    // ── Periodic ─────────────────────────────────────────────────────────────

    @Override
    public void periodic() {
        m_intakeVoltagePub.set(getIntakeVoltage());
    }
}