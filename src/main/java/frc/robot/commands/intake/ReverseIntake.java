package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants;
import frc.robot.subsystems.IntakeSubsystem;

/**
 * Spins the intake roller in reverse (ejecting / clearing a jam) while the
 * command is active.
 * Bind to L2 with whileTrue so the roller stops when the button is released.
 */
public class ReverseIntake extends Command {

    private final IntakeSubsystem m_intake;

    public ReverseIntake(IntakeSubsystem intake) {
        m_intake = intake;
        addRequirements(m_intake);
    }

    @Override
    public void initialize() {
        m_intake.spinIntake(IntakeConstants.kIntakeReverseVoltage);
    }

    @Override
    public void end(boolean interrupted) {
        m_intake.stopIntake();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}