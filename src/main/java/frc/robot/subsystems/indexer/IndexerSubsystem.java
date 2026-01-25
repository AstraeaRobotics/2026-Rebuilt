// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.indexer;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeIndexConstants;
import frc.robot.Constants.IntakeIndexConstants.HopperStates;

public class IndexerSubsystem extends SubsystemBase {
  /** Creates a new IndexerSubsystem. */

  private final SparkMax m_extendHopperMotor;
  private final SparkMax m_hopperMotor;
  private final SimpleMotorFeedforward m_hopperFeedforward;

  private final AbsoluteEncoder m_hopperAbsoluteEncoder;

  HopperStates m_hopperState;
  double m_hopperSetpoint;

  public IndexerSubsystem() {
    m_extendHopperMotor = new SparkMax(IntakeIndexConstants.kExtendHopper_CANID, MotorType.kBrushless);
    m_hopperMotor = new SparkMax(IntakeIndexConstants.kHopperMotor_CANID, MotorType.kBrushless);

    m_hopperFeedforward = new SimpleMotorFeedforward(IntakeIndexConstants.kHopper_ks, IntakeIndexConstants.kHopper_kv);

    m_hopperAbsoluteEncoder = m_extendHopperMotor.getAbsoluteEncoder();

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
    m_hopperMotor.configure(m_hopperConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public void setHopperState(HopperStates tempState) {
    m_hopperState = tempState;
    m_hopperSetpoint = m_hopperState.getHopperSetpoint();
  }

  public HopperStates getHopperStates() {
    return m_hopperState;
  }

  public void spinBelt(double voltage) {
    m_hopperMotor.setVoltage(voltage);
  }

  public void setBelt(double voltage) {
    m_hopperMotor.setVoltage(m_hopperFeedforward.calculate(voltage));
  }

  public double getHopperPivotEncoder() {
    return (m_hopperAbsoluteEncoder.getPosition());
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
