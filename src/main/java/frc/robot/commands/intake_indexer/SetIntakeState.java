// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake_indexer;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants.IntakeIndexConstants.IntakeStates;
import frc.robot.subsystems.intake_indexer.IntakeIndexSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class SetIntakeState extends InstantCommand {

  IntakeIndexSubsystem m_intakeIndexSubsystem;
  IntakeStates m_intakeStates;

  public SetIntakeState(IntakeIndexSubsystem m_intakeIndexSubsystem, IntakeStates m_intakeStates) {
    // Use addRequirements() here to declare subsystem dependencies.

    this.m_intakeIndexSubsystem = m_intakeIndexSubsystem;
    this.m_intakeStates = m_intakeStates;

    addRequirements(m_intakeIndexSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_intakeIndexSubsystem.setIntakeState(m_intakeStates);
  }
}
