// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeIndexConstants;

public class IntakeSubsystem extends SubsystemBase {
  /** Creates a new IntakeSubsystem. */

  private final SparkMax m_intakeMotor;
  
  private final SimpleMotorFeedforward m_intakeFeedforward;

  public IntakeSubsystem() {
    m_intakeMotor = new SparkMax(IntakeIndexConstants.kIntakeMotor_CANID, MotorType.kBrushless);

    m_intakeFeedforward =  new SimpleMotorFeedforward(IntakeIndexConstants.kIntake_ks, IntakeIndexConstants.kIntake_kv);

    configureMotors();
  }

  public void configureMotors() {
    SparkMaxConfig m_intakeConfig = new SparkMaxConfig();

    m_intakeConfig.smartCurrentLimit(35).idleMode(IdleMode.kCoast);

    m_intakeMotor.configure(m_intakeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public void spinIntake(double voltage) {
    m_intakeMotor.setVoltage(voltage);
  }

  public void setIntake(double voltage) {
    m_intakeMotor.setVoltage(m_intakeFeedforward.calculate(voltage));
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
