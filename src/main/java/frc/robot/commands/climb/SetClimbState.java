// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.climb;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants.ClimbConstants.ClimbStates;
import frc.robot.subsystems.ClimbSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class SetClimbState extends InstantCommand {

  private final ClimbSubsystem m_climbSubsystem;
  private final ClimbStates m_targetState;

  public SetClimbState(ClimbSubsystem m_climbSubsystem, ClimbStates m_targetState) {
    this.m_climbSubsystem = m_climbSubsystem;
    this.m_targetState = m_targetState;
    
    addRequirements(m_climbSubsystem);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_climbSubsystem.setClimbState(m_targetState);
  }
}
