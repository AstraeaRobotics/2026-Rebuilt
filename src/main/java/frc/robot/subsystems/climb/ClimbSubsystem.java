// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.climb;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ClimbConstants;
import frc.robot.Constants.ClimbConstants.ClimbStates;

public class ClimbSubsystem extends SubsystemBase {
  /** Creates a new ClimbSubsystem. */

  private final TalonFX m_climbMotor;
  private final PositionVoltage m_positionControl;
  private final VoltageOut m_voltageControl;
  private final ElevatorFeedforward m_feedforward;

  private ClimbStates m_climbState;
  private double m_climbSetpoint;

  public ClimbSubsystem() {
    m_climbMotor = new TalonFX(ClimbConstants.kClimbMotor_CANID);
    
    m_positionControl = new PositionVoltage(0);
    m_voltageControl = new VoltageOut(0);
    m_feedforward = new ElevatorFeedforward(
        ClimbConstants.kClimb_ks, 
        ClimbConstants.kClimb_kg, 
        ClimbConstants.kClimb_kv, 
        ClimbConstants.kClimb_ka
    );
    
    m_climbState = ClimbStates.kNormal;
    m_climbSetpoint = m_climbState.getClimbSetpoint();
    
    configureMotors();
  }

  private void configureMotors() {
    TalonFXConfiguration config = new TalonFXConfiguration();
    
    config.CurrentLimits.SupplyCurrentLimit = 40;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.CurrentLimits.StatorCurrentLimit = 80;
    config.CurrentLimits.StatorCurrentLimitEnable = true;
    
    config.Slot0.kP = ClimbConstants.kClimb_kP;
    config.Slot0.kI = ClimbConstants.kClimb_kI;
    config.Slot0.kD = ClimbConstants.kClimb_kD;

    config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    config.MotorOutput.Inverted = ClimbConstants.kClimbMotorInverted;

    config.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
    config.SoftwareLimitSwitch.ForwardSoftLimitThreshold = ClimbConstants.kMaxHeight;
    config.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
    config.SoftwareLimitSwitch.ReverseSoftLimitThreshold = ClimbConstants.kMinHeight;
    
    m_climbMotor.getConfigurator().apply(config);
    m_climbMotor.setPosition(0);
  }

  public void setClimbState(ClimbStates state) {
    m_climbState = state;
    m_climbSetpoint = m_climbState.getClimbSetpoint();
    setPosition(m_climbSetpoint);
  }

  public void setPosition(double position) {
    m_climbMotor.setControl(m_positionControl.withPosition(position));
  }

  public void setVoltage(double voltage) {
    m_climbMotor.setControl(m_voltageControl.withOutput(voltage));
  }
  
  public void setVoltageWithFeedforward(double targetVelocity, double targetAcceleration) {
    double feedforwardVoltage = m_feedforward.calculate(targetVelocity, targetAcceleration);
    m_climbMotor.setControl(m_voltageControl.withOutput(feedforwardVoltage));
  }

  public void stop() {
    m_climbMotor.stopMotor();
  }

  public double getPosition() {
    return m_climbMotor.getPosition().getValueAsDouble();
  }
  
  public double getVelocity() {
    return m_climbMotor.getVelocity().getValueAsDouble();
  }

  public ClimbStates getClimbState() {
    return m_climbState;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
