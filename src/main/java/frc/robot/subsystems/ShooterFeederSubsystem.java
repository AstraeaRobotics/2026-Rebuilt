// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.ArrayList;

import com.revrobotics.PersistMode;
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

  private ShooterFeederStates m_state = ShooterFeederStates.kIdle;

  public static InterpolatingMeasureMap<Distance, DistanceUnit, Voltage, VoltageUnit> shotDistanceVoltageMap;

  private final DoublePublisher m_shooterVoltagePub;
  private final DoublePublisher m_transitionFeederVoltagePub;

  public ShooterFeederSubsystem() {
    m_shooterMotor = new SparkMax(ShooterFeederConstants.kShooter_CANID, MotorType.kBrushless);
    m_transitionFeederMotor = new SparkMax(ShooterFeederConstants.kTransitionFeeder_CANID, MotorType.kBrushless);

    NetworkTable table = NetworkTableInstance.getDefault().getTable("ShooterFeeder");
    m_shooterVoltagePub    = table.getDoubleTopic("Shooter Voltage").publish();
    m_transitionFeederVoltagePub = table.getDoubleTopic("TransitionFeeder Voltage").publish();

    configureMotors();
    initializeInterpolationMap();
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

  private void initializeInterpolationMap() {
    ArrayList<Pair<Distance, Voltage>> data = new ArrayList<>();

    // TODO: Replace with real tested values
    data.add(Pair.of(Units.Meters.of(2.0), Units.Volts.of(6.0)));
    data.add(Pair.of(Units.Meters.of(3.0), Units.Volts.of(7.5)));
    data.add(Pair.of(Units.Meters.of(4.0), Units.Volts.of(9.0)));
    data.add(Pair.of(Units.Meters.of(5.0), Units.Volts.of(11.0)));
    shotDistanceVoltageMap = new InterpolatingMeasureMap<>(data);
  }

  public void setShooterVoltage(double voltage) {
    m_shooterMotor.setVoltage(voltage);
  }

  public void setTransitionVoltage(double voltage) {
    m_transitionFeederMotor.setVoltage(voltage);
  }

  public void stopAll() {
    m_shooterMotor.setVoltage(0.0);
    m_transitionFeederMotor.setVoltage(0.0);
  }

  public void setState(ShooterFeederStates state) {
    m_state = state;
  }

  public ShooterFeederStates getState() {
    return m_state;
  }
  
  public double getShooterVoltage() {
    return m_shooterMotor.getAppliedOutput() * m_shooterMotor.getBusVoltage();
  }

  public double getTransitionVoltage() {
    return m_transitionFeederMotor.getAppliedOutput() * m_transitionFeederMotor.getBusVoltage();
  }

  public boolean atTargetVoltage() {
    return getShooterVoltage() >= (m_state.getShooterVoltage() - ShooterFeederConstants.kVoltageTolerance);
  }

  public double getVoltageForDistance(double distanceMeters) {
    return shotDistanceVoltageMap
      .get(Units.Meters.of(distanceMeters))
      .in(Units.Volts);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    m_shooterVoltagePub.set(getShooterVoltage());
    m_transitionFeederVoltagePub.set(getTransitionVoltage());
  }
}
