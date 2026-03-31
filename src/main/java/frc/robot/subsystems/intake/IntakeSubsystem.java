// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import frc.robot.Constants.IntakeConstants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
  /** Creates a new IntakeSubsystem. */

  private final SparkMax m_pivotIntake;
  private final SparkMax m_moveIntake;

  public IntakeSubsystem() {
    m_pivotIntake = new SparkMax(IntakeConstants.kPivotMotor_CANID, MotorType.kBrushless);
    m_moveIntake = new SparkMax(IntakeConstants.kIntakeMotor_CANID, MotorType.kBrushless);

    configureMotors();
  }

  public void configureMotors() {}

  public void setPivotSpeed(double speed) {
    m_pivotIntake.set(speed);
  }

  public void setMovePivotSpeed(double speed) {
    m_moveIntake.set(speed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
