// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.swerve;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveSubsystem;
import frc.robot.utils.SwerveUtil;

public class TeleopSwerve extends Command {

  SwerveSubsystem m_SwerveSubsystem;

  DoubleSupplier m_driveX;
  DoubleSupplier m_driveY;
  DoubleSupplier m_rotation;

  private final BooleanSupplier slowModeButton;
  private boolean slowModeActive = false;
  private boolean lastButtonState = false;

  public TeleopSwerve(SwerveSubsystem swerveSub, DoubleSupplier driveX, DoubleSupplier driveY, DoubleSupplier rotation, BooleanSupplier slowModeButton) {
    m_driveX = driveX;
    m_driveY = driveY;
    m_rotation = rotation;
    m_SwerveSubsystem = swerveSub;
    this.slowModeButton = slowModeButton;

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

    if (currentButtonState && !lastButtonState) {
      slowModeActive = !slowModeActive;

      if (slowModeActive) {
        System.out.println("Slow Mode ON");
        SmartDashboard.putString("Drive Mode", "SLOW");
      } else {
        System.out.println("Slow Mode OFF");
        SmartDashboard.putString("Drive Mode", "NORMAL");
      }
    }

    lastButtonState = currentButtonState;

    if (Math.abs(m_driveX.getAsDouble()) > 0.2 || Math.abs(m_driveY.getAsDouble()) > 0.2) {
      m_SwerveSubsystem.drive(
        SwerveUtil.driveInputToChassisSpeeds(
          m_driveX.getAsDouble(), m_driveY.getAsDouble(), m_rotation.getAsDouble(), m_SwerveSubsystem.getHeading()),
        slowModeActive);
    }
    else if (Math.abs(m_rotation.getAsDouble()) > 0.2) {
      m_SwerveSubsystem.drive(
        SwerveUtil.driveInputToChassisSpeeds(
          0, 0, m_rotation.getAsDouble(), m_SwerveSubsystem.getHeading()),
        slowModeActive);
    }
    else {
      m_SwerveSubsystem.drive(
        SwerveUtil.driveInputToChassisSpeeds(0, 0, 0, m_SwerveSubsystem.getHeading()),
        slowModeActive);
    }
  }

  @Override
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {
    return false;
  }
}