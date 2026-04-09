package frc.robot.subsystems;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CANcoderConfigurator;
import com.ctre.phoenix6.configs.CANdiConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ClimbSubsystem extends SubsystemBase {

    private static final int KRAKEN_CAN_ID = 7;
    private static final int ENCODER_CAN_ID = 0;

    /**
     * DO NOT RUN TRY CLIMB UNTIL YOU FIND THESE VALUES:
     */
    public static final double ENCODER_MAX_VAL = 10;
    public static final double ENCODER_MIN_VAL = 0;
    public static final double ENCODER_TOLERANCE = 0.5;

    private final TalonFX    m_climbMotor;
    private final CANcoder   m_climbEncoder;
    private final VoltageOut m_voltageControl;

    public ClimbSubsystem() {
        m_climbMotor     = new TalonFX(KRAKEN_CAN_ID);
        m_climbEncoder   = new CANcoder(ENCODER_CAN_ID);
        m_voltageControl = new VoltageOut(0);
        configureMotors();
    }

    private void configureMotors() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.CurrentLimits.SupplyCurrentLimit       = 40;
        config.CurrentLimits.SupplyCurrentLimitEnable = true;
        config.CurrentLimits.StatorCurrentLimit       = 80;
        config.CurrentLimits.StatorCurrentLimitEnable = true;
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        m_climbMotor.getConfigurator().apply(config);
    }

    public void setVoltage(double voltage) {
        m_climbMotor.setControl(m_voltageControl.withOutput(voltage));
    }

    public double getPosition(){
        return m_climbEncoder.getPosition().getValueAsDouble();
    }

    public void stop() {
        m_climbMotor.stopMotor();
    }

    @Override
    public void periodic() {}
}