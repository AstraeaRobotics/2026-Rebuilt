// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.IntakeConstants.IntakeStates;
import frc.robot.subsystems.IntakeSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class IntakeFuel extends SequentialCommandGroup {
  /** Creates a new IntakeFuel. */
  public IntakeFuel(IntakeSubsystem m_intakeSubsystem, double m_intakeVoltage) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(
      new PivotIntake(m_intakeSubsystem, IntakeStates.kOut),
      new SpinIntake(m_intakeSubsystem, m_intakeVoltage),
      new PivotIntake(m_intakeSubsystem, IntakeStates.kIn)
    );
  }
}
