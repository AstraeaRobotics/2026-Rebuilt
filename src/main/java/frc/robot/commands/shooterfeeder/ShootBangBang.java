// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.shooterfeeder;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ShooterFeederSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class ShootBangBang extends Command {

  ShooterFeederSubsystem m_sub;
  private double target_RPM;
  private double tolerance;
  /** Creates a new ShootBangBang. */
  public ShootBangBang(ShooterFeederSubsystem m_sub, double target_RPM, double tolerance) {
    this.m_sub = m_sub;
    this.target_RPM = target_RPM;
    this.tolerance = tolerance;

    addRequirements(m_sub);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_sub.runShooter();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double currentRPM = m_sub.getShooterVelocity();

    if (currentRPM < target_RPM) {
      m_sub.runShooter();
    } else {
      m_sub.stopShooter();
    }

    if (Math.abs(currentRPM-target_RPM) <= tolerance) {
      m_sub.runTransition();
    } else {
      m_sub.stopTransition();
    }


  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_sub.stopAll();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
