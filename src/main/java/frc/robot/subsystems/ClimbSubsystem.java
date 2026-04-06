package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ClimbSubsystem extends SubsystemBase {

    private static final int KRAKEN_CAN_ID = 7;

    private final TalonFX    m_climbMotor;
    private final VoltageOut m_voltageControl;

    public ClimbSubsystem() {
        m_climbMotor     = new TalonFX(KRAKEN_CAN_ID);
        m_voltageControl = new VoltageOut(0);
        configureMotors();
    }

    private void configureMotors() {
        m_climbMotor.setPosition(0);

        TalonFXConfiguration config = new TalonFXConfiguration();
        config.CurrentLimits.SupplyCurrentLimit       = 40;
        config.CurrentLimits.SupplyCurrentLimitEnable = true;
        config.CurrentLimits.StatorCurrentLimit       = 80;
        config.CurrentLimits.StatorCurrentLimitEnable = true;
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        // config.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
        // config.SoftwareLimitSwitch.ReverseSoftLimitThreshold = 0;

        config.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
        config.SoftwareLimitSwitch.ForwardSoftLimitThreshold = 125.011;

        m_climbMotor.getConfigurator().apply(config);
    }

    public void setVoltage(double voltage) {
        m_climbMotor.setControl(m_voltageControl.withOutput(voltage));
    }

    public void stop() {
        m_climbMotor.stopMotor();
    }

    public void periodic(){
        SmartDashboard.putNumber("Climb Pos", m_climbMotor.getPosition().getValueAsDouble());
    }
}