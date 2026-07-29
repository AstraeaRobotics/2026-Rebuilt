

package frc.robot.subsystems;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterFeederConstants;

public class ShooterFeederSubsystem extends SubsystemBase {

  private final TalonFX m_shooterMotor;
  private final SparkMax m_transitionFeederMotor;

  private final DoublePublisher m_shooterVoltagePub;
  private final DoublePublisher m_transitionFeederVoltagePub;
  private final DoublePublisher m_shooterVelocityPub;

  public ShooterFeederSubsystem() {
    m_transitionFeederMotor = new SparkMax(ShooterFeederConstants.kTransitionFeeder_CANID, MotorType.kBrushless);
    m_shooterMotor = new TalonFX(ShooterFeederConstants.kShooter_CANID);

    NetworkTable table = NetworkTableInstance.getDefault().getTable("ShooterFeeder");
    m_shooterVoltagePub          = table.getDoubleTopic("Shooter Voltage").publish();
    m_transitionFeederVoltagePub = table.getDoubleTopic("TransitionFeeder Voltage").publish();
    m_shooterVelocityPub         = table.getDoubleTopic("Shooter Velocity (RPM)").publish();

    configureMotors();
  }

  private void configureMotors() {
    TalonFXConfiguration shooterConfig = new TalonFXConfiguration()
      .withMotorOutput(
        new MotorOutputConfigs()
          .withNeutralMode(NeutralModeValue.Coast)
      )
      .withCurrentLimits(
        new CurrentLimitsConfigs()
          .withStatorCurrentLimit(120)
          .withSupplyCurrentLimit(40)
          .withStatorCurrentLimitEnable(true)
          .withSupplyCurrentLimitEnable(true)
      );

    TalonFXConfigurator shooterConfigurator = m_shooterMotor.getConfigurator();
    shooterConfigurator.apply(shooterConfig);

    SparkMaxConfig transitionConfig = new SparkMaxConfig();
    transitionConfig
      .idleMode(IdleMode.kBrake)
      .smartCurrentLimit(60)
      .inverted(false);

    m_transitionFeederMotor.configure(transitionConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }


  // ── Direct motor controls (used by LaunchSequence / EjectTransition) ──────

  public void runShooter() {
    m_shooterMotor.setVoltage(ShooterFeederConstants.kShooterVoltage);
  }

  public void runTransition() {
    m_transitionFeederMotor.setVoltage(ShooterFeederConstants.kTransitionVoltage);
  }

  public void ejectTransition() {
    m_transitionFeederMotor.setVoltage(-(ShooterFeederConstants.kTransitionVoltage));
  }

  public void stopShooter() {
    m_shooterMotor.setVoltage(0);
  }

  public void stopTransition() {
    m_transitionFeederMotor.setVoltage(0);
  }

  public void stopAll() {
    stopShooter();
    stopTransition();
  }

  // ── Telemetry ─────────────────────────────────────────────────────────────

  public double getShooterVoltage() {
    return m_shooterMotor.getMotorVoltage().getValue().in(Volts);
  }

  public boolean atMaxVoltage() {
    return getShooterVoltage() >= (ShooterFeederConstants.kShooterVoltage - ShooterFeederConstants.kVoltageTolerance);
  }

  public double getShooterVelocity(){
    return m_shooterMotor.getVelocity().getValue().in(RotationsPerSecond);
  }

  private void updateLog() {
    m_shooterVoltagePub.set(getShooterVoltage());
    m_transitionFeederVoltagePub.set(m_transitionFeederMotor.getAppliedOutput() * m_transitionFeederMotor.getBusVoltage());
    m_shooterVelocityPub.set(getShooterVelocity());
  }

  // ── Periodic ──────────────────────────────────────────────────────────────

  @Override
  public void periodic() {
    updateLog();
  }
}