// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.shooter;

import java.util.ArrayList;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.Pair;
import edu.wpi.first.units.AngularVelocityUnit;
import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;
import frc.robot.utils.UnitsUtil.InterpolatingMeasureMap;
import static edu.wpi.first.units.Units.RPM;

import edu.wpi.first.units.Units;

public class ShooterSubsystem extends SubsystemBase {
  /** Creates a new ShooterSubsystem. */

  private final TalonFX m_leftShooter;
  private final TalonFX m_rightShooter; 

  private final VelocityVoltage m_leftVelocityControl = new VelocityVoltage(0);
  private final VelocityVoltage m_rightVelocityControl = new VelocityVoltage(0);

  private static final double MAX_SHOOTER_RPM = 6000;

  private double m_targetRPM = 0.0;

  public static InterpolatingMeasureMap<Distance, DistanceUnit, AngularVelocity, AngularVelocityUnit> shotDistanceVelocityMap;

  public ShooterSubsystem() {
    m_leftShooter = new TalonFX(ShooterConstants.kLeftShooter_CANID);
    m_rightShooter = new TalonFX(ShooterConstants.kRightShooter_CANID); 

    configureMotors();
    initializeInterpolationMap();
  }

  private void configureMotors() {
    var m_leftConfig = new TalonFXConfiguration();
    m_leftConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
    m_leftConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
    m_leftConfig.Slot0.kP = 0;
    m_leftConfig.Slot0.kI = 0;
    m_leftConfig.Slot0.kD = 0;
    m_leftConfig.Slot0.kV = 0;

    var m_rightConfig = new TalonFXConfiguration();
    m_rightConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
    m_rightConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive; 
    m_rightConfig.Slot0.kP = 0;
    m_rightConfig.Slot0.kI = 0;
    m_rightConfig.Slot0.kD = 0;
    m_rightConfig.Slot0.kV = 0;

    m_leftShooter.getConfigurator().apply(m_leftConfig);
    m_rightShooter.getConfigurator().apply(m_rightConfig);
  }

  private void initializeInterpolationMap() {
    ArrayList<Pair<Distance, AngularVelocity>> data = new ArrayList<>();

    // Example data points - replace w/ tested values
    data.add(Pair.of(Units.Meters.of(2.0), Units.RPM.of(2500)));
    data.add(Pair.of(Units.Meters.of(3.0), Units.RPM.of(3000)));
    data.add(Pair.of(Units.Meters.of(4.0), Units.RPM.of(3500)));
    data.add(Pair.of(Units.Meters.of(5.0), Units.RPM.of(4000)));
        
    shotDistanceVelocityMap = new InterpolatingMeasureMap<>(data);
  }

  public void runShooters(double rpm) {
    rpm = Math.min(rpm, MAX_SHOOTER_RPM);

    m_targetRPM = rpm;

    double rps = rpm / 60;

    m_leftShooter.setControl(m_leftVelocityControl.withVelocity(rps));
    m_rightShooter.setControl(m_rightVelocityControl.withVelocity(rps));
  }

  public void runShootersAtDistance(Distance distance) {
    AngularVelocity velocity = shotDistanceVelocityMap.get(distance);
    runShooters(velocity.in(RPM));
  }

  public void stopShooters() {
    m_leftShooter.stopMotor();
    m_rightShooter.stopMotor();
    m_targetRPM = 0.0;
  }

  public double getLeftVelocityRPM() {
    return m_leftShooter.getVelocity().getValueAsDouble() * 60.0;
  }

  public double getRightVelocityRPM() {
    return m_rightShooter.getVelocity().getValueAsDouble() * 60.0;
  }

  public double getAverageVelocityRPM() {
    return (getLeftVelocityRPM() + getRightVelocityRPM()) / 2.0;
  }

  public double getTargetRPM() {
    return m_targetRPM;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
