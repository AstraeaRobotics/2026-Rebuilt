// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.shooterfeeder;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterFeederSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class Flywheel extends Command {
  /** Creates a new Flywheel. */

  ShooterFeederSubsystem m_sub;

  public Flywheel(ShooterFeederSubsystem m_sub) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.m_sub = m_sub;

    addRequirements(m_sub);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("Flywheel started");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_sub.runShooter();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_sub.stopAll();
    System.out.println("Flywheel ended, interrupted: " + interrupted);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
