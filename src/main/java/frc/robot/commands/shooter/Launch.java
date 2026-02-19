// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterSubsystem;

public class Launch extends Command {

  private final ShooterSubsystem m_shooterSubsystem;

  public Launch(ShooterSubsystem shooterSubsystem) {
    this.m_shooterSubsystem = shooterSubsystem;
    addRequirements(m_shooterSubsystem);
  }

  @Override
  public void execute() {
    m_shooterSubsystem.runShooter();
    m_shooterSubsystem.runTransition();
  }

  @Override
  public void end(boolean interrupted) {
    m_shooterSubsystem.stopAll();
  }

  @Override
  public boolean isFinished() {
    return false; 
  }
}