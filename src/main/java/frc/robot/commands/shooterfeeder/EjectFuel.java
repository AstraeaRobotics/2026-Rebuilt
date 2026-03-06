// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.shooterfeeder;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterFeederConstants.ShooterFeederStates;
import frc.robot.subsystems.ShooterFeederSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class EjectFuel extends Command {
  /** Creates a new EjectFuel. */

  private final ShooterFeederSubsystem m_shooterFeederSubsystem;
  private final double m_ejectVoltage;

  public EjectFuel(ShooterFeederSubsystem m_shooterFeederSubsystem, double m_ejectVoltage) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.m_shooterFeederSubsystem = m_shooterFeederSubsystem;
    this.m_ejectVoltage = m_ejectVoltage;

    addRequirements(m_shooterFeederSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_shooterFeederSubsystem.setState(ShooterFeederStates.kIdle);
    m_shooterFeederSubsystem.setShooterVoltage(0.0);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_shooterFeederSubsystem.setTransitionVoltage(m_ejectVoltage);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_shooterFeederSubsystem.stopAll();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
