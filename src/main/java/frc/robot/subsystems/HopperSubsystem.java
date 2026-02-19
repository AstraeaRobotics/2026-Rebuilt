// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import static edu.wpi.first.units.Units.RotationsPerSecond;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkAbsoluteEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.HopperConstants;
import frc.robot.Constants.HopperConstants.HopperStates;

public class HopperSubsystem extends SubsystemBase {
  /** Creates a new IndexerSubsystem. */

  private final SparkMax m_pivot;
  private final SparkMax m_belt;

  private final SparkAbsoluteEncoder m_pivotAbsEncoder;
  
  private final SparkClosedLoopController m_beltController;
  private final SparkClosedLoopController m_pivotController;

  //TODO: Add belt telemetry and stuff if they actually build belt

  private final DoublePublisher m_pivotPositionPub;
  private final DoublePublisher m_pivotVoltagePub;
  private final DoublePublisher m_pivotSetpointPub;

  HopperStates m_hopperState;
  double m_hopperSetpoint;

  public HopperSubsystem() {
    m_pivot = new SparkMax(HopperConstants.kPivot_CANID, MotorType.kBrushless);
    m_belt = new SparkMax(HopperConstants.kHopperMotor_CANID, MotorType.kBrushless);

    m_beltController = m_belt.getClosedLoopController();
    m_pivotController = m_pivot.getClosedLoopController();
    m_pivotAbsEncoder = m_pivot.getAbsoluteEncoder();

    m_hopperState = HopperStates.kIn;
    m_hopperSetpoint = m_hopperState.getHopperSetpoint();

    NetworkTable table = NetworkTableInstance.getDefault().getTable("Hopper Subsystem");
    m_pivotPositionPub = table.getDoubleTopic("Pivot Encoder Position").publish();
    m_pivotVoltagePub = table.getDoubleTopic("Pivot Voltage").publish();
    m_pivotSetpointPub = table.getDoubleTopic("Pivot Setpoint").publish();

    configureMotors();
  }

  public void configureMotors() {
    SparkMaxConfig m_pivotConfig = new SparkMaxConfig();
    SparkMaxConfig m_beltConfig = new SparkMaxConfig();

    m_pivotConfig.smartCurrentLimit(35).idleMode(IdleMode.kBrake).inverted(false);
    m_pivotConfig.closedLoop
      .p(HopperConstants.kPivot_kp)
      .d(HopperConstants.kPivot_kd);
    m_pivotConfig.closedLoop
      .feedForward
        .kS(HopperConstants.kPivot_ks)
        .kV(HopperConstants.kPivot_kv)
        .kCosRatio(HopperConstants.kPivot_kcosRatio)
        .kCos(HopperConstants.kPivot_kcos);

    m_beltConfig.smartCurrentLimit(35).idleMode(IdleMode.kCoast);
    m_beltConfig.closedLoop
      .p(HopperConstants.kBelt_kp);
    m_beltConfig.closedLoop
      .feedForward
        .kS(HopperConstants.kBelt_ks)
        .kV(HopperConstants.kBelt_kv);

    m_pivot.configure(m_pivotConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_belt.configure(m_beltConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public void setHopperState(HopperStates tempState) {
    m_hopperState = tempState;
    m_hopperSetpoint = m_hopperState.getHopperSetpoint();
    m_pivotController.setSetpoint(m_hopperSetpoint, ControlType.kPosition);
  }

  public HopperStates getHopperStates() {
    return m_hopperState;
  }

  public void spinBelt(double voltage) {
    m_belt.setVoltage(voltage);
  }

  public void setBelt(AngularVelocity velocity) {
    m_beltController.setSetpoint(velocity.in(RotationsPerSecond), ControlType.kVelocity);
    
  }

  public void updateLog(){
    m_pivotPositionPub.set(m_pivotAbsEncoder.getPosition());
    m_pivotVoltagePub.set(m_pivot.getAppliedOutput());
    m_pivotSetpointPub.set(m_pivotController.getSetpoint());
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
