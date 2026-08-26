// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.RepeatCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
// import frc.robot.Constants.IntakeConstants.IntakeStates;
// import frc.robot.commands.intake.SetIntakeState;
import frc.robot.commands.shooterfeeder.Flywheel;
import frc.robot.commands.shooterfeeder.Transition;
// import frc.robot.commands.swerve.TeleopSwerve;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterFeederSubsystem;
import frc.robot.subsystems.SwerveSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class JitterShoot extends SequentialCommandGroup {
  /** Creates a new JitterShoot. */
  public JitterShoot(SwerveSubsystem m_sub1, ShooterFeederSubsystem m_sub2, IntakeSubsystem m_sub3) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    SequentialCommandGroup jitterCycle = new SequentialCommandGroup(
      new Transition(m_sub2).withTimeout(.6),
      new WaitCommand(.6)
    );

    // SequentialCommandGroup intakePush = new SequentialCommandGroup(
    //   new SetIntakeState(m_sub3, IntakeStates.kHorizontal),
    //   new SetIntakeState(m_sub3, IntakeStates.kMid)
    // );

    addCommands(
      new ParallelDeadlineGroup(
        new Flywheel(m_sub2), 
        new RepeatCommand(jitterCycle)
        )
    );
  }
}
