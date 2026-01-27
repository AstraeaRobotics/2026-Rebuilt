// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.indexer;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.HopperConstants;
import frc.robot.Constants.HopperConstants.HopperStates;

public class IndexerSubsystem extends SubsystemBase {
  /** Creates a new IndexerSubsystem. */

  private final SparkMax m_extendHopperMotor;
  private final SparkMax m_belt;
  private final SimpleMotorFeedforward m_hopperFeedforward;

  HopperStates m_hopperState;
  double m_hopperSetpoint;

  public IndexerSubsystem() {
    m_extendHopperMotor = new SparkMax(HopperConstants.kExtendHopper_CANID, MotorType.kBrushless);
    m_belt = new SparkMax(HopperConstants.kHopperMotor_CANID, MotorType.kBrushless);

    m_hopperFeedforward = new SimpleMotorFeedforward(HopperConstants.kHopper_ks, HopperConstants.kHopper_kv);

    m_hopperState = HopperStates.kIn;

    m_hopperSetpoint = m_hopperState.getHopperSetpoint();

    configureMotors();
  }

  public void configureMotors() {
    SparkMaxConfig m_extendHopperConfig = new SparkMaxConfig();
    SparkMaxConfig m_hopperConfig = new SparkMaxConfig();

    m_extendHopperConfig.smartCurrentLimit(35).idleMode(IdleMode.kBrake).inverted(false);
    m_hopperConfig.smartCurrentLimit(35).idleMode(IdleMode.kCoast);

    m_extendHopperMotor.configure(m_extendHopperConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_belt.configure(m_hopperConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public void setHopperState(HopperStates tempState) {
    m_hopperState = tempState;
    m_hopperSetpoint = m_hopperState.getHopperSetpoint();
  }

  public HopperStates getHopperStates() {
    return m_hopperState;
  }

  public void spinBelt(double voltage) {
    m_belt.setVoltage(voltage);
  }

  public void setBelt(double voltage) {
    m_belt.setVoltage(m_hopperFeedforward.calculate(voltage));
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
