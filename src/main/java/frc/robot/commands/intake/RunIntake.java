package frc.robot.commands.intake;

import static edu.wpi.first.units.Units.Volts;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants;
import frc.robot.subsystems.IntakeSubsystem;

/**
 * Spins the intake roller forward (intaking) while the command is active.
 * Bind to R2 with whileTrue so the roller stops when the button is released.
 */
public class RunIntake extends Command {

    private final IntakeSubsystem m_intake;
    private final double m_volts;

    public RunIntake(IntakeSubsystem intake, double volts) {
        m_intake = intake;
        m_volts = volts;

        addRequirements(intake);
    }

    @Override
    public void initialize() {
        m_intake.spinIntake(m_volts);
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