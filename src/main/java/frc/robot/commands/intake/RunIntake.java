package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants;
import frc.robot.subsystems.IntakeSubsystem;

/**
 * Spins the intake roller forward (intaking) while the command is active.
 * Bind to R2 with whileTrue so the roller stops when the button is released.
 */
public class RunIntake extends Command {

    private final IntakeSubsystem m_intake;

    public RunIntake(IntakeSubsystem intake) {
        m_intake = intake;
        addRequirements(m_intake);
    }

    @Override
    public void initialize() {
        m_intake.spinIntake(IntakeConstants.kIntakeVoltage);
    }

    @Override
    public void end(boolean interrupted) {
        m_intake.stopIntake();
    }

    // Runs until interrupted (button released)
    @Override
    public boolean isFinished() {
        return false;
    }
}