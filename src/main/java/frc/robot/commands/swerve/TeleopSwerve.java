// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.swerve;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveSubsystem;
import frc.robot.utils.SwerveUtil;

public class TeleopSwerve extends Command {

  SwerveSubsystem m_SwerveSubsystem;
  private final double RATE_LIMIT = 1.2;

  SlewRateLimiter m_xRateLimiter;
  SlewRateLimiter m_yRateLimiter;

  DoubleSupplier m_driveX;
  DoubleSupplier m_driveY;
  DoubleSupplier m_rotation;

  private final BooleanSupplier slowModeButton;
  private final BooleanSupplier m_turboMode;
  private boolean slowModeActive = false;
  private boolean lastButtonState = false;

  public TeleopSwerve(SwerveSubsystem swerveSub, DoubleSupplier driveX, DoubleSupplier driveY, DoubleSupplier rotation, BooleanSupplier slowModeButton, BooleanSupplier turboMode) {
    m_driveX = driveX;
    m_driveY = driveY;
    m_rotation = rotation;
    m_SwerveSubsystem = swerveSub;
    m_xRateLimiter = new SlewRateLimiter(RATE_LIMIT);
    m_yRateLimiter = new SlewRateLimiter(RATE_LIMIT);
    this.slowModeButton = slowModeButton;
    this.m_turboMode = turboMode;

    addRequirements(swerveSub);
  }

  @Override
  public void initialize() {
    slowModeActive = false;
    lastButtonState = false;
    SmartDashboard.putString("Drive Mode", "NORMAL");
  }

  @Override
  public void execute() {
    boolean currentButtonState = slowModeButton.getAsBoolean();
    boolean turboActive = m_turboMode.getAsBoolean();

    if (currentButtonState && !lastButtonState) {
      slowModeActive = !slowModeActive;
    }
    lastButtonState = currentButtonState;

    if (turboActive) {
      SmartDashboard.putString("Drive Mode", "TURBO");
    } else if (slowModeActive) {
      SmartDashboard.putString("Drive Mode", "SLOW");
    } else {
      SmartDashboard.putString("Drive Mode", "NORMAL");
    }

    double x = m_driveX.getAsDouble();
    double y = m_driveY.getAsDouble();
    double rot = m_rotation.getAsDouble();

    boolean driving = Math.abs(x) > 0.2 || Math.abs(y) > 0.2;
    boolean rotating = Math.abs(rot) > 0.2;

    if (driving) {
      m_SwerveSubsystem.drive(
        SwerveUtil.driveInputToChassisSpeeds(x, y, rot, m_SwerveSubsystem.getHeading()),
        slowModeActive, turboActive
      );
    } else if (rotating) {
      m_SwerveSubsystem.drive(
        SwerveUtil.driveInputToChassisSpeeds(0, 0, rot, m_SwerveSubsystem.getHeading()),
        slowModeActive, turboActive
      );
    } else {
      m_SwerveSubsystem.drive(
        SwerveUtil.driveInputToChassisSpeeds(0, 0, 0, m_SwerveSubsystem.getHeading()),
        slowModeActive, turboActive
      );
    }
  }

  @Override
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {
    return false;
  }
}