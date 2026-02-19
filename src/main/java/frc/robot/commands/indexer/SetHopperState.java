// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.indexer;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants.HopperConstants.HopperStates;
import frc.robot.subsystems.HopperSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class SetHopperState extends InstantCommand {

  HopperSubsystem m_indexSubsystem;
  HopperStates m_hopperStates;

  public SetHopperState(HopperSubsystem m_indexSubsystem, HopperStates m_hopperStates) {
    // Use addRequirements() here to declare subsystem dependencies.

    this.m_indexSubsystem = m_indexSubsystem;
    this.m_hopperStates = m_hopperStates;

    addRequirements(m_indexSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_indexSubsystem.setHopperState(m_hopperStates);
  }
}
