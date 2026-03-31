package frc.robot.commands.climb;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ClimbSubsystem;

public class ClimbUp extends Command {

    private final ClimbSubsystem m_climb;

    public ClimbUp(ClimbSubsystem climb) {
        m_climb = climb;
        addRequirements(climb);
    }

    @Override
    public void execute() {
        m_climb.setVoltage(8.0);
    }

    @Override
    public void end(boolean interrupted) {
        m_climb.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}