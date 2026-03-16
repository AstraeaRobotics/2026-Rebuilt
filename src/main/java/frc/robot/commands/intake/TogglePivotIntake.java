// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.commands.intake;

// import edu.wpi.first.wpilibj2.command.InstantCommand;
// import frc.robot.Constants.IntakeConstants.IntakeStates;
// import frc.robot.subsystems.IntakeSubsystem;

// // NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// // information, see:
// // https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
// public class TogglePivotIntake extends InstantCommand {

//   private final IntakeSubsystem m_intakeSubsystem;

//   public TogglePivotIntake(IntakeSubsystem m_intakeSubsystem) {
//     // Use addRequirements() here to declare subsystem dependencies.
//     this.m_intakeSubsystem = m_intakeSubsystem;
//     addRequirements(m_intakeSubsystem);
//   }

//   // Called when the command is initially scheduled.
//   @Override
//   public void initialize() {
//     if (m_intakeSubsystem.getState() == IntakeStates.kIn) {
//       m_intakeSubsystem.setState(IntakeStates.kOut);
//     } 
    
//     else {
//       m_intakeSubsystem.setState(IntakeStates.kIn);
//     }
// }
// }
