// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.ResetMode;
import com.revrobotics.PersistMode;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DrivebaseModuleConstants;
import frc.robot.utils.SwerveUtil;

public class SwerveModule extends SubsystemBase {
  /** Creates a new SwerveModule. */
  private SparkMax turnMotor;
  private SparkMax driveMotor;

  private SparkMaxConfig turnMotorConfig;
  private SparkMaxConfig driveMotorConfig;

  private PIDController turnPIDController;
  private SimpleMotorFeedforward driveFF;

  private AbsoluteEncoder turnEncoder;
  private RelativeEncoder driveEncoder;

  private int angularOffset;
  private String moduleName;

  private SwerveModuleState moduleState;

  private SlewRateLimiter slewRateLimiter;

  private Boolean isInverted;

  private enum DriveMode {SLOW, NORMAL, TURBO}
  private DriveMode lastDriveMode = DriveMode.NORMAL;

  public SwerveModule(int turnMotorID, int driveMotorID, int angularOffset, String moduleName, Boolean isInverted) {
    turnMotor = new SparkMax(turnMotorID, MotorType.kBrushless);
    driveMotor = new SparkMax(driveMotorID, MotorType.kBrushless);

    turnMotorConfig = new SparkMaxConfig();
    driveMotorConfig = new SparkMaxConfig();

    turnPIDController = new PIDController(DrivebaseModuleConstants.turnKP, 0, 0);

    turnEncoder = turnMotor.getAbsoluteEncoder();
    driveEncoder = driveMotor.getEncoder();

    this.angularOffset = angularOffset;
    this.moduleName = moduleName;

    this.moduleState = new SwerveModuleState(0, Rotation2d.fromDegrees(0));

    this.isInverted = isInverted;
    // ks - 0.25
    // kv - 6.6

    driveFF = new SimpleMotorFeedforward(DrivebaseModuleConstants.driveKS, DrivebaseModuleConstants.driveKV);
    slewRateLimiter = new SlewRateLimiter(13);

    configureMotors();
  }

  public void configureMotors() {
    turnPIDController.enableContinuousInput(0, 360);

    turnMotorConfig
    .smartCurrentLimit(50)
    .idleMode(IdleMode.kCoast)
    .inverted(true);
    turnMotorConfig.absoluteEncoder
    .positionConversionFactor(DrivebaseModuleConstants.kTurnEncoderPositionFactor)
    .velocityConversionFactor(DrivebaseModuleConstants.kTurnEncoderVelocityFactor)
    .inverted(true);

    turnMotor.configure(turnMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    driveMotorConfig
    .openLoopRampRate(0.3) //increase this?
    .smartCurrentLimit(60)
    .idleMode(IdleMode.kBrake)
    .inverted(isInverted);
    driveMotorConfig.encoder
    .positionConversionFactor(DrivebaseModuleConstants.kDriveEncoderPositionFactor)
    .velocityConversionFactor(DrivebaseModuleConstants.kDriveEncoderVelocityFactor);
    
    resetEncoder();

    driveMotor.configure(driveMotorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  public String getModuleName(){
    return this.moduleName;
  }

  public double getDistance(){
    return driveEncoder.getPosition();
  }

  public void resetEncoder() {
    driveEncoder.setPosition(0);
  }

  public double getAngle() {
    return (turnEncoder.getPosition() + angularOffset) % 360;
  }

  public SwerveModulePosition getModulePosition() {
    double distance = getDistance();
    Rotation2d angle = Rotation2d.fromDegrees(getAngle());
    return new SwerveModulePosition(distance, angle);
  }

  public SwerveModuleState getModuleState() {
    return moduleState;
  }

  public void setState(SwerveModuleState state, boolean slowMode, boolean turboMode) {
    moduleState = state;
    drive(slowMode, turboMode);
  }

  public void drive(boolean slowMode, boolean turboMode) {
    if (slowMode && turboMode) slowMode = false;

    double[] optimizedModule = SwerveUtil.optimizeModule(
      getAngle(), 
      moduleState.angle.getDegrees() + 180,
      moduleState.speedMetersPerSecond
    );

    turnMotor.set(-turnPIDController.calculate(getAngle(), optimizedModule[0]));

    DriveMode currentMode = turboMode ? DriveMode.TURBO : (slowMode ? DriveMode.SLOW : DriveMode.NORMAL);

    if (lastDriveMode == DriveMode.NORMAL && currentMode != DriveMode.NORMAL) {
      slewRateLimiter.reset(0);
    }
    lastDriveMode = currentMode;

    double voltage;
    if (turboMode) {
      voltage = MathUtil.clamp(driveFF.calculate(optimizedModule[1] * 3.5), -11, 11);
    }

    else if (slowMode) {
      voltage = slewRateLimiter.calculate(MathUtil.clamp(driveFF.calculate(optimizedModule[1] / 2.0), -10, 10));
    }

    else {
      voltage = slewRateLimiter.calculate(MathUtil.clamp(driveFF.calculate(optimizedModule[1] * 2.75), -10, 10));
    }

    driveMotor.setVoltage(voltage);
  }

  public void driveOpenLoop(double Voltage, double Angle){
    driveMotor.setVoltage(Voltage);
    turnMotor.set(-turnPIDController.calculate(getAngle(), Angle));
  }

  public double getVelocity() {
    return driveEncoder.getVelocity();
  }

  public double getVoltage() {
    return driveMotor.getBusVoltage() * driveMotor.getAppliedOutput();
  }

  public double getOutput() {
    return driveMotor.getAppliedOutput();
  }

  public double getCurrent() {
    return driveMotor.getOutputCurrent();
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Velocity Setpoint", moduleState.speedMetersPerSecond);
    // This method will be called once per scheduler run
  }
}