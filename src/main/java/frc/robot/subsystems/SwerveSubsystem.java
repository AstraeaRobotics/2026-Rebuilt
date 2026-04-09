// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

import com.studica.frc.AHRS;
import com.studica.frc.AHRS.NavXComType;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.DoubleEntry;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DrivebaseConstants;

public class SwerveSubsystem extends SubsystemBase {
  /** Creates a new SwerveSubsystem. */
  SwerveDriveKinematics kinematics;
  AHRS gyro;

  SwerveModule[] swerveModules;

  Translation2d m_frontLeftLocation = new Translation2d(DrivebaseConstants.kWheelBase / 2, DrivebaseConstants.kTrackWidth / 2);
  Translation2d m_frontRightLocation = new Translation2d(DrivebaseConstants.kWheelBase / 2, -DrivebaseConstants.kTrackWidth / 2);
  Translation2d m_backLeftLocation = new Translation2d(-DrivebaseConstants.kWheelBase / 2, DrivebaseConstants.kTrackWidth / 2);
  Translation2d m_backRightLocation = new Translation2d(-DrivebaseConstants.kWheelBase / 2, -DrivebaseConstants.kTrackWidth / 2); 

  SwerveDrivePoseEstimator swerveDrivePoseEstimator;

  private final StructPublisher<Pose2d> posePublisher;
  private final StructPublisher<ChassisSpeeds> chassisSpeedsPublisher;
  private final StructArrayPublisher<SwerveModuleState> statePublisher;

  private final DoubleEntry configurationVoltageEntry;

  private final Map<String, DoublePublisher> voltagePublishers;
  private final Map<String, DoublePublisher> currentPublishers;
  private final Map<String, DoublePublisher> appliedOutPublishers;
  private final Map<String, DoublePublisher> velocityPublishers;

  private final Field2d m_field = new Field2d();

  DoubleSupplier m_driveX;

  RobotConfig config;

  public SwerveSubsystem() { 
    kinematics = new SwerveDriveKinematics(m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation);
    gyro = new AHRS(NavXComType.kMXP_SPI);

    swerveModules = new SwerveModule[4];
    swerveModules[0] = new SwerveModule(12, 11, 0, "front left", true);
    swerveModules[1] = new SwerveModule(14, 13, 0, "front right", true);
    swerveModules[2] = new SwerveModule(16, 15, 180, "back left", true);
    swerveModules[3] = new SwerveModule(18, 17, 0, "back right", true);
    
    swerveDrivePoseEstimator = new SwerveDrivePoseEstimator(kinematics, Rotation2d.fromDegrees(getHeading()), getModulePositions(), new Pose2d(new Translation2d(0, 0), Rotation2d.fromDegrees(0)));

    SmartDashboard.putData("Field", m_field);

    try {
      config = RobotConfig.fromGUISettings();
    } catch (Exception e) {
      e.printStackTrace();
    }

    AutoBuilder.configure(
      this::getPose, 
      this::resetRobotPose, 
      this::getRobotRelativeSpeeds, 
      (speeds, feedforwards) -> drive(speeds, false, false), 
      new PPHolonomicDriveController(new PIDConstants(2.1, 0, 0), new PIDConstants(2.0, 0, 0.1)), 
      config,
      () -> {
        return false;
      },
      this
    );

    gyro.reset();

    NetworkTable swerveTable = NetworkTableInstance.getDefault().getTable("Swerve");
    currentPublishers = new HashMap<>(4);
    voltagePublishers = new HashMap<>(4);
    appliedOutPublishers = new HashMap<>(4);
    velocityPublishers = new HashMap<>(4);

    posePublisher = swerveTable.getStructTopic("MyPose", Pose2d.struct).publish();
    statePublisher = swerveTable.getStructArrayTopic("Swerve Module States", SwerveModuleState.struct).publish();
    chassisSpeedsPublisher = swerveTable.getStructTopic("Chassis Speeds", ChassisSpeeds.struct).publish();
    configurationVoltageEntry = swerveTable.getDoubleTopic("Configuration Voltage").getEntry(0);
    configurationVoltageEntry.set(0);
    

    for (SwerveModule sm : swerveModules){
      String moduleName = sm.getModuleName();
      NetworkTable specificModuleTable = swerveTable.getSubTable(moduleName);
      DoublePublisher voltagePub = specificModuleTable.getDoubleTopic("Voltage " + moduleName).publish();
      DoublePublisher currentPub = specificModuleTable.getDoubleTopic("Current " + moduleName).publish();
      DoublePublisher appliedOutputPub = specificModuleTable.getDoubleTopic("Applied Out " + moduleName).publish();
      DoublePublisher velocityPub = specificModuleTable.getDoubleTopic("Velocity " + moduleName).publish();

      voltagePublishers.put(moduleName, voltagePub);
      currentPublishers.put(moduleName, currentPub);
      appliedOutPublishers.put(moduleName, appliedOutputPub);
      velocityPublishers.put(moduleName, velocityPub);
    }
  }

  private void updateTables(){
    posePublisher.set(getPose());
    statePublisher.set(getModuleStates());
    chassisSpeedsPublisher.set(getRobotRelativeSpeeds());

    for (SwerveModule sm : swerveModules){
      String moduleName = sm.getModuleName();

      voltagePublishers.get(moduleName).set(sm.getVoltage());
      currentPublishers.get(moduleName).set(sm.getCurrent());
      appliedOutPublishers.get(moduleName).set(sm.getOutput());
      velocityPublishers.get(moduleName).set(sm.getVelocity());
    }
  }

  public void drive(ChassisSpeeds speeds, boolean slowMode, boolean turboMode) {
    SwerveModuleState[] states = kinematics.toSwerveModuleStates(speeds);
    for (int i = 0; i < states.length; i++) {
      swerveModules[i].setState(states[i], slowMode, turboMode);
    }
  }

  private void runConfiguration(){
    for(SwerveModule sm : swerveModules){
      sm.driveOpenLoop(configurationVoltageEntry.get(), 0);
    }
  }

  public SwerveModuleState[] getModuleStates() {
    SwerveModuleState[] moduleStates = new SwerveModuleState[4];
    for(int i = 0; i < swerveModules.length; i++) {
      moduleStates[i] = swerveModules[i].getModuleState();
    }
    return moduleStates;
  }

  public SwerveModulePosition[] getModulePositions() {
    SwerveModulePosition[] positions = new SwerveModulePosition[4];

    for(int i = 0; i < swerveModules.length; i++) {
      positions[i] = swerveModules[i].getModulePosition();
    }
    
    return positions;
  }

  public ChassisSpeeds getRobotRelativeSpeeds() {
    return kinematics.toChassisSpeeds(getModuleStates());
  }

  public double getHeading() {
    return (gyro.getYaw() + 360) % 360;
  }

  public Pose2d getPose() {
    return swerveDrivePoseEstimator.getEstimatedPosition();
  }

  public void resetGyro() {
    gyro.reset();
    resetRobotPose(new Pose2d(new Translation2d(0, 0), new Rotation2d()));
  }

  public void resetRobotPose(Pose2d pose) {
    swerveDrivePoseEstimator.resetPosition(Rotation2d.fromDegrees(0), getModulePositions(), pose);
  }

  public void resetEncoders() {
    for(int i = 0; i < swerveModules.length; i++) {
      swerveModules[i].resetEncoder();
    }
  }

  public double getEncoderPosition() {
    return swerveModules[0].getDistance();
  }

  public Command RunConfiguration() {
    return new RunCommand(this::runConfiguration, this)
        .finallyDo(() -> drive(new ChassisSpeeds(), false, false));
  }

  @Override
  public void periodic() {
    swerveDrivePoseEstimator.update(Rotation2d.fromDegrees(-getHeading()), getModulePositions());

    m_field.setRobotPose(getPose());
    updateTables();

    SmartDashboard.putNumber("Robot X", getPose().getX());
    SmartDashboard.putNumber("Robot Y", getPose().getY());
    SmartDashboard.putNumber("Robot Heading", getPose().getRotation().getDegrees());
  }
}