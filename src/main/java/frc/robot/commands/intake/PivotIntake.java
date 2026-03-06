// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants.IntakeStates;
import frc.robot.subsystems.IntakeSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class PivotIntake extends Command {
  /** Creates a new PivotIntake. */

  private final IntakeSubsystem m_intakeSubsystem;
  private final IntakeStates m_state;

  public PivotIntake(IntakeSubsystem m_intakeSubsystem, IntakeStates m_state) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.m_intakeSubsystem = m_intakeSubsystem;
    this.m_state = m_state;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_intakeSubsystem.setState(m_state);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_intakeSubsystem.pivotAtSetpoint();
  }
}
