// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.ArrayList;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.Pair;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.VoltageUnit;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterFeederConstants;
import frc.robot.Constants.ShooterFeederConstants.ShooterFeederStates;
import frc.robot.utils.UnitsUtil.InterpolatingMeasureMap;

public class ShooterFeederSubsystem extends SubsystemBase {
  /** Creates a new ShooterFeederSubsystem. */

  private final SparkMax m_shooterMotor;
  private final SparkMax m_transitionFeederMotor; 

  private final RelativeEncoder m_shooterEncoder;

  private boolean m_shooterRunning = false;
  private boolean m_transitionRunning = false;

  private final DoublePublisher m_shooterVoltagePub;
  private final DoublePublisher m_transitionFeederVoltagePub;

  public ShooterFeederSubsystem() {
    m_shooterMotor = new SparkMax(ShooterFeederConstants.kShooter_CANID, MotorType.kBrushless);
    m_transitionFeederMotor = new SparkMax(ShooterFeederConstants.kTransitionFeeder_CANID, MotorType.kBrushless);

    NetworkTable table = NetworkTableInstance.getDefault().getTable("ShooterFeeder");
    m_shooterVoltagePub    = table.getDoubleTopic("Shooter Voltage").publish();
    m_transitionFeederVoltagePub = table.getDoubleTopic("TransitionFeeder Voltage").publish();

    m_shooterEncoder = m_shooterMotor.getEncoder();

    configureMotors();
  }

  private void configureMotors() {
    SparkMaxConfig m_shooterConfig = new SparkMaxConfig();
    m_shooterConfig
      .idleMode(IdleMode.kCoast)
      .smartCurrentLimit(60)
      .inverted(false);
    
    SparkMaxConfig m_transitionFeederConfig = new SparkMaxConfig();
    m_transitionFeederConfig
      .idleMode(IdleMode.kBrake)
      .smartCurrentLimit(60)
      .inverted(false);

    m_shooterMotor.configure(m_shooterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_transitionFeederMotor.configure(m_transitionFeederConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

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

  public double getShooterVoltage() {
    return m_shooterMotor.getAppliedOutput() * m_shooterMotor.getBusVoltage();
  }

  public boolean atMaxVoltage() {
    return getShooterVoltage() >= (ShooterFeederConstants.kShooterVoltage - ShooterFeederConstants.kVoltageTolerance);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
