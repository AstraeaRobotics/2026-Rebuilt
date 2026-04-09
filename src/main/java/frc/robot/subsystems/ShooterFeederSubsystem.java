

package frc.robot.subsystems;
import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
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

  private final SparkMax m_shooterMotor;
  private final SparkMax m_transitionFeederMotor;
  private final RelativeEncoder m_shooterEncoder;

  private final DoublePublisher m_shooterVoltagePub;
  private final DoublePublisher m_transitionFeederVoltagePub;
  private final DoublePublisher m_shooterVelocityPub;

  public ShooterFeederSubsystem() {
    m_shooterMotor          = new SparkMax(ShooterFeederConstants.kShooter_CANID,          MotorType.kBrushless);
    m_transitionFeederMotor = new SparkMax(ShooterFeederConstants.kTransitionFeeder_CANID, MotorType.kBrushless);
    m_shooterEncoder = m_shooterMotor.getEncoder();

    NetworkTable table = NetworkTableInstance.getDefault().getTable("ShooterFeeder");
    m_shooterVoltagePub          = table.getDoubleTopic("Shooter Voltage").publish();
    m_transitionFeederVoltagePub = table.getDoubleTopic("TransitionFeeder Voltage").publish();
    m_shooterVelocityPub         = table.getDoubleTopic("Shooter Velocity (RPM)").publish();

    configureMotors();
  }

  private void configureMotors() {
    SparkMaxConfig shooterConfig = new SparkMaxConfig();
    shooterConfig
      .idleMode(IdleMode.kCoast)
      .smartCurrentLimit(60)
      .inverted(false);

    SparkMaxConfig transitionConfig = new SparkMaxConfig();
    transitionConfig
      .idleMode(IdleMode.kBrake)
      .smartCurrentLimit(60)
      .inverted(false);

    m_shooterMotor.configure(shooterConfig,     ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
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
    return m_shooterMotor.getAppliedOutput() * m_shooterMotor.getBusVoltage();
  }

  public boolean atMaxVoltage() {
    return getShooterVoltage() >= (ShooterFeederConstants.kShooterVoltage - ShooterFeederConstants.kVoltageTolerance);
  }

  public double getShooterVelocity(){
    return m_shooterEncoder.getVelocity();
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