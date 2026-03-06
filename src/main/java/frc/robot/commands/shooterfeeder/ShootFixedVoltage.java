// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.shooterfeeder;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterFeederConstants.ShooterFeederStates;
import frc.robot.subsystems.ShooterFeederSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class ShootFixedVoltage extends Command {
  /** Creates a new ShootFixedVoltage. */

  private final ShooterFeederSubsystem m_shooterFeederSubsystem;
  private final double m_shooterVoltage;
  private final double m_transitionFeederVoltage;

  public ShootFixedVoltage(
    ShooterFeederSubsystem m_shooterFeederSubsystem, 
    double m_shooterVoltage,
    double m_transitionFeederVoltage) {
    // Use addRequirements() here to declare subsystem dependencies.

    this.m_shooterFeederSubsystem = m_shooterFeederSubsystem;
    this.m_shooterVoltage = m_shooterVoltage;
    this.m_transitionFeederVoltage = m_transitionFeederVoltage;

    addRequirements(m_shooterFeederSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_shooterFeederSubsystem.setState(ShooterFeederStates.kSpinningUp);
    m_shooterFeederSubsystem.setShooterVoltage(m_shooterVoltage);
    m_shooterFeederSubsystem.setTransitionVoltage(0.0);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (m_shooterFeederSubsystem.getState() == ShooterFeederStates.kSpinningUp 
          && m_shooterFeederSubsystem.atTargetVoltage()) {
      m_shooterFeederSubsystem.setState(ShooterFeederStates.kLaunching);
      m_shooterFeederSubsystem.setTransitionVoltage(m_transitionFeederVoltage);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_shooterFeederSubsystem.setState(ShooterFeederStates.kIdle);
    m_shooterFeederSubsystem.stopAll();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
