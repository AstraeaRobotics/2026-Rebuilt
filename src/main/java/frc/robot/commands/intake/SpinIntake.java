// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.commands.intake;

// import edu.wpi.first.wpilibj2.command.Command;
// import frc.robot.subsystems.IntakeSubsystem;

// /* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
// public class SpinIntake extends Command {
//   /** Creates a new SpinIntake. */

//   private final IntakeSubsystem m_intakeSubsystem;
//   private final double m_intakeVoltage;

//   public SpinIntake(IntakeSubsystem m_intakeSubsystem, double m_intakeVoltage) {
//     // Use addRequirements() here to declare subsystem dependencies.
//     this.m_intakeSubsystem = m_intakeSubsystem;
//     this.m_intakeVoltage = m_intakeVoltage;

//     addRequirements(m_intakeSubsystem);
//   }

//   // Called when the command is initially scheduled.
//   @Override
//   public void initialize() {
//     m_intakeSubsystem.spinIntake(m_intakeVoltage);
//   }

//   // Called every time the scheduler runs while the command is scheduled.
//   @Override
//   public void execute() {
//     m_intakeSubsystem.spinIntake(m_intakeVoltage);
//   }

//   // Called once the command ends or is interrupted.
//   @Override
//   public void end(boolean interrupted) {
//     m_intakeSubsystem.stopIntake();
//   }

//   // Returns true when the command should end.
//   @Override
//   public boolean isFinished() {
//     return false;
//   }
// }
