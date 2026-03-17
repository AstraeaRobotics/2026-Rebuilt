package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants.IntakeConstants.IntakeStates;
import frc.robot.subsystems.IntakeSubsystem;

/**
 * Instantly flips the intake pivot to the requested state.
 * The pivot motor will begin moving toward the new setpoint on the next
 * IntakeSubsystem.periodic() call.
 */
public class SetIntakeState extends InstantCommand {

    public SetIntakeState(IntakeSubsystem intake, IntakeStates state) {
        super(() -> intake.setState(state), intake);
    }
}