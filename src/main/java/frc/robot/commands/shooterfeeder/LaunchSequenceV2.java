// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.shooterfeeder;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.ShooterFeederSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class LaunchSequenceV2 extends SequentialCommandGroup {
  /** Creates a new LaunchSequenceV2. */
  public LaunchSequenceV2(ShooterFeederSubsystem m_sub) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(
      new Flywheel(m_sub).withTimeout(8),
      new ParallelDeadlineGroup(new Flywheel(m_sub), new Transition(m_sub))
    );
  }
}
