package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants;
import frc.robot.subsystems.IntakeSubsystem;

/**
 * Drives the pivot inward (toward kIn) while the button is held.
 * Bind with whileTrue — pivot stops when button is released.
 */
public class PivotIn extends Command {

    private final IntakeSubsystem m_intake;

    public PivotIn(IntakeSubsystem intake) {
        m_intake = intake;
        addRequirements(m_intake);
    }

    @Override
    public void initialize() {
        m_intake.runPivot(IntakeConstants.kPivotVoltage);
    }

    @Override
    public void end(boolean interrupted) {
        m_intake.stopPivot();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}