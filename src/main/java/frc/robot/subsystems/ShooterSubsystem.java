// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;

public class ShooterSubsystem extends SubsystemBase {
  /** Creates a new ShooterSubsystem. */

  private SparkMax m_shooterMotor;
  private SparkMax m_transitionMotor;

  private final RelativeEncoder m_shooterEncoder;

  private boolean m_shooterRunning    = false;
  private boolean m_transitionRunning = false;

  private SparkMaxConfig m_shooterConfig;
  private SparkMaxConfig m_transitionConfig;

  public ShooterSubsystem() {
    m_shooterMotor = new SparkMax(ShooterConstants.kShooter_CANID, MotorType.kBrushless);
    m_transitionMotor = new SparkMax(ShooterConstants.kTransition_CANID, MotorType.kBrushless);

    m_shooterEncoder = m_shooterMotor.getEncoder();

    m_shooterConfig = new SparkMaxConfig();
    m_transitionConfig = new SparkMaxConfig();

    configureMotors();
  }

  public void configureMotors() {
    m_shooterConfig
      .idleMode(IdleMode.kCoast)
      .smartCurrentLimit(60)
      .inverted(false);

    m_shooterMotor.configure(
      m_shooterConfig,
      ResetMode.kResetSafeParameters,
      PersistMode.kPersistParameters
    );

    m_transitionConfig
      .idleMode(IdleMode.kBrake)
      .smartCurrentLimit(60)
      .inverted(false);

    m_transitionMotor.configure(
      m_transitionConfig,
      ResetMode.kResetSafeParameters,
      PersistMode.kPersistParameters
    );
  }

  public void runShooter() {
    m_shooterMotor.setVoltage(ShooterConstants.kShooterVoltage);
    m_shooterRunning = true;
  }

  public void runTransition() {
    m_transitionMotor.setVoltage(ShooterConstants.kTransitionVoltage);
    m_transitionRunning = true;
  }

  public void ejectTransition() {
    m_transitionMotor.setVoltage(-(ShooterConstants.kTransitionVoltage));
    m_transitionRunning = true;
  }

  public void stopShooter() {
    m_shooterMotor.setVoltage(0.0);
    m_shooterRunning = false;
  }

  public void stopTransition() {
    m_transitionMotor.setVoltage(0.0);
    m_transitionRunning = false;
  }

  public void stopAll() {
    stopShooter();
    stopTransition();
  }

  public double getShooterVoltage() {
    return m_shooterMotor.getAppliedOutput() * m_shooterMotor.getBusVoltage();
  }

  public boolean atMaxVoltage() {
    return getShooterVoltage() >= (ShooterConstants.kShooterVoltage - ShooterConstants.kVoltageTolerance);
  }

  public double getShooterRPM() {
    return m_shooterEncoder.getVelocity();
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber ("Shooter/MeasuredVoltage", getShooterVoltage());
    SmartDashboard.putNumber ("Shooter/TargetVoltage", ShooterConstants.kShooterVoltage);
    SmartDashboard.putNumber ("Shooter/VoltageGap", ShooterConstants.kShooterVoltage - getShooterVoltage());
    SmartDashboard.putBoolean("Shooter/AtMaxVoltage", atMaxVoltage());
    SmartDashboard.putBoolean("Shooter/ShooterRunning", m_shooterRunning);
    SmartDashboard.putBoolean("Shooter/TransitionRunning", m_transitionRunning);
    SmartDashboard.putNumber ("Shooter/ShooterCurrentAmps", m_shooterMotor.getOutputCurrent());
    SmartDashboard.putNumber ("Shooter/TransitionCurrentAmps", m_transitionMotor.getOutputCurrent());
    SmartDashboard.putNumber ("Shooter/TransitionMeasuredVoltage", m_transitionMotor.getAppliedOutput() * m_transitionMotor.getBusVoltage());
    SmartDashboard.putNumber ("Shooter/RPM", getShooterRPM());
  }
}
