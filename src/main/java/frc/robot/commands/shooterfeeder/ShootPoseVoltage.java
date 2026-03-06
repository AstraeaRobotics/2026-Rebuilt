// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.shooterfeeder;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterFeederConstants.ShooterFeederStates;
import frc.robot.Landmarks;
import frc.robot.subsystems.ShooterFeederSubsystem;
import frc.robot.subsystems.SwerveSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class ShootPoseVoltage extends Command {
  /** Creates a new ShootPoseVoltage. */

  private final ShooterFeederSubsystem m_shooterFeederSubsystem;
  private final SwerveSubsystem m_swerveSubsystem;
  private final double m_transitionVoltage;

  public ShootPoseVoltage(ShooterFeederSubsystem m_shooterFeederSubsystem, 
                          SwerveSubsystem m_swerveSubsystem, 
                          double m_transitionVoltage) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.m_shooterFeederSubsystem = m_shooterFeederSubsystem;
    this.m_swerveSubsystem = m_swerveSubsystem;
    this.m_transitionVoltage = m_transitionVoltage;

    addRequirements(m_shooterFeederSubsystem);
  }

  private double getDistanceToHub() {
    return m_swerveSubsystem.getPose()
      .getTranslation()
      .getDistance(Landmarks.hubPosition());
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_shooterFeederSubsystem.setState(ShooterFeederStates.kSpinningUp);
    m_shooterFeederSubsystem.setShooterVoltage(m_shooterFeederSubsystem.getVoltageForDistance(getDistanceToHub()));
    m_shooterFeederSubsystem.setTransitionVoltage(0.0);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (m_shooterFeederSubsystem.getState() == ShooterFeederStates.kSpinningUp) {
      // Keep updating flywheel voltage as robot moves into final position
      m_shooterFeederSubsystem.setShooterVoltage(m_shooterFeederSubsystem.getVoltageForDistance(getDistanceToHub()));

      if (m_shooterFeederSubsystem.atTargetVoltage()) {
          m_shooterFeederSubsystem.setState(ShooterFeederStates.kLaunching);
          m_shooterFeederSubsystem.setTransitionVoltage(m_transitionVoltage);
      }
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
