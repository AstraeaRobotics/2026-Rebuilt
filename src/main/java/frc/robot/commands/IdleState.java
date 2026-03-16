// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.commands;

// import edu.wpi.first.wpilibj2.command.InstantCommand;
// import frc.robot.Constants.IntakeConstants.IntakeStates;
// import frc.robot.Constants.ShooterFeederConstants.ShooterFeederStates;
// import frc.robot.subsystems.IntakeSubsystem;
// import frc.robot.subsystems.ShooterFeederSubsystem;

// // NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// // information, see:
// // https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
// public class IdleState extends InstantCommand {
//   public IdleState(IntakeSubsystem m_intakeSubsystem, ShooterFeederSubsystem m_shooterFeederSubsystem) {
//     // Use addRequirements() here to declare subsystem dependencies.
//     super(
//         () -> {
//             m_intakeSubsystem.setState(IntakeStates.kIn);
//             m_intakeSubsystem.stopIntake();
//             m_shooterFeederSubsystem.setState(ShooterFeederStates.kIdle);
//             m_shooterFeederSubsystem.stopAll();
//         },
//         m_intakeSubsystem, m_shooterFeederSubsystem
//     );
//   }
// }
