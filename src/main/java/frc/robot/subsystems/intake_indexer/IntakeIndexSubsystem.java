// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake_indexer;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeIndexConstants;
import frc.robot.Constants.IntakeIndexConstants.IntakeStates;

public class IntakeIndexSubsystem extends SubsystemBase {
  /** Creates a new IntakeIndexSubsystem. */

  private final SparkMax m_pivotMotor;
  private final SparkMax m_intakeMotor;
  private final SparkMax m_hopperMotor;

  private final SimpleMotorFeedforward m_intakeFeedforward;
  private final SimpleMotorFeedforward m_hopperFeedforward;

  private final AbsoluteEncoder m_pivotAbsoluteEncoder;

  IntakeStates m_intakeState;
  double m_intakeSetpoint;

  public IntakeIndexSubsystem() {
    m_pivotMotor = new SparkMax(IntakeIndexConstants.kPivotMotor_CANID, MotorType.kBrushless);
    m_intakeMotor = new SparkMax(IntakeIndexConstants.kIntakeMotor_CANID, MotorType.kBrushless);
    m_hopperMotor = new SparkMax(IntakeIndexConstants.kHopperMotor_CANID, MotorType.kBrushless);

    m_intakeFeedforward =  new SimpleMotorFeedforward(IntakeIndexConstants.kIntake_ks, IntakeIndexConstants.kIntake_kv);
    m_hopperFeedforward = new SimpleMotorFeedforward(IntakeIndexConstants.kHopper_ks, IntakeIndexConstants.kHopper_kv);

    m_pivotAbsoluteEncoder = m_pivotMotor.getAbsoluteEncoder();

    m_intakeState = IntakeStates.kIn;

    m_intakeSetpoint = m_intakeState.getIntakeSetpoint();

    configureMotors();
  }

  public void configureMotors() {
    SparkMaxConfig m_pivotConfig = new SparkMaxConfig();
    SparkMaxConfig m_intakeConfig = new SparkMaxConfig();
    SparkMaxConfig m_hopperConfig = new SparkMaxConfig();

    m_pivotConfig.smartCurrentLimit(35).idleMode(IdleMode.kBrake).inverted(false);
    m_intakeConfig.smartCurrentLimit(35).idleMode(IdleMode.kCoast);
    m_hopperConfig.smartCurrentLimit(35).idleMode(IdleMode.kCoast);

    m_pivotMotor.configure(m_pivotConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_intakeMotor.configure(m_intakeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_hopperMotor.configure(m_hopperConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  /* Intake Methods */

  public void spinIntake(double voltage) {
    m_intakeMotor.setVoltage(voltage);
  }

  public void setIntake(double voltage) {
    m_intakeMotor.setVoltage(m_intakeFeedforward.calculate(voltage));
  }

  public void setIntakeState(IntakeStates tempState) {
    m_intakeState = tempState;
    m_intakeSetpoint = m_intakeState.getIntakeSetpoint();
  }

  public IntakeStates getIntakeStates() {
    return m_intakeState;
  }

  public double getPivotEncoder() {
    return (m_pivotAbsoluteEncoder.getPosition());
  }

  /* Indexer Methods */

  public void spinBelt(double voltage) {
    m_hopperMotor.setVoltage(voltage);
  }

  public void setBelt(double voltage) {
    m_hopperMotor.setVoltage(m_hopperFeedforward.calculate(voltage));
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
