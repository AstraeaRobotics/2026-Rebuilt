// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.networktables.DoubleEntry;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
  /** Creates a new IntakeSubsystem. */

  private final SparkMax m_intakeMotor;
  private final SparkClosedLoopController m_intakeController;
  private final RelativeEncoder m_intakeEncoder;

  private final DoublePublisher m_voltagePub;
  private final DoublePublisher m_velocityPub;
  private final DoublePublisher m_setpointPub;

  public IntakeSubsystem() {
    NetworkTable table = NetworkTableInstance.getDefault().getTable("Intake Subsystem");
    m_voltagePub = table.getDoubleTopic("Intake Voltage").publish();
    m_velocityPub = table.getDoubleTopic("Current Intake Velocity RPS").publish();
    m_setpointPub = table.getDoubleTopic("Intake Setpoint RPS").publish();

    m_intakeMotor = new SparkMax(IntakeConstants.kIntakeMotor_CANID, MotorType.kBrushless);
    m_intakeEncoder = m_intakeMotor.getEncoder();
    m_intakeController = m_intakeMotor.getClosedLoopController();

    configureMotors();
  }

  public void configureMotors() {
    SparkMaxConfig m_intakeConfig = new SparkMaxConfig();

    m_intakeConfig.smartCurrentLimit(35).idleMode(IdleMode.kCoast);
    m_intakeConfig.closedLoop
      .feedForward
        .kS(IntakeConstants.kIntake_ks)
        .kV(IntakeConstants.kIntake_kv);

    m_intakeMotor.configure(m_intakeConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public void spinIntake(double voltage) {
    m_intakeMotor.setVoltage(voltage);
  }

  public void setIntake(AngularVelocity speed) {
    m_intakeController.setSetpoint(speed.in(RotationsPerSecond), ControlType.kVelocity);
  }

  public void updateLog(){
    m_voltagePub.set(m_intakeMotor.getAppliedOutput());
    m_velocityPub.set(m_intakeEncoder.getVelocity());
    m_setpointPub.set(m_intakeController.getSetpoint());
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
