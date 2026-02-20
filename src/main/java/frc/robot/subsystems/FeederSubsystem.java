package frc.robot.subsystems;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.FeederConstants;

public class FeederSubsystem extends SubsystemBase {

    private final SparkMax m_feederMotor;

    private final DoublePublisher m_voltagePublisher;

    public FeederSubsystem() {
        NetworkTable table  = NetworkTableInstance.getDefault().getTable("Feeder Subsystem");
        m_voltagePublisher  = table.getDoubleTopic("Feeder Voltage").publish();

        m_feederMotor = new SparkMax(FeederConstants.kFeederMotor_CANID, MotorType.kBrushless);

        configureMotors();
    }

    private void configureMotors() {
        SparkMaxConfig feederConfig = new SparkMaxConfig();
        feederConfig.smartCurrentLimit(40).idleMode(IdleMode.kBrake).inverted(false);
        m_feederMotor.configure(feederConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    public void feed(double voltage) {
        m_feederMotor.setVoltage(voltage);
    }

    public void stop() {
        m_feederMotor.setVoltage(0.0);
    }

    public double getVoltage() {
        return m_feederMotor.getAppliedOutput() * m_feederMotor.getBusVoltage();
    }

    @Override
    public void periodic() {
        m_voltagePublisher.set(getVoltage());
    }
}