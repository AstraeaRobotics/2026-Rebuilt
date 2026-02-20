package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterConstants.ShooterStates;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class FeedToShooter extends Command {

    private final ShooterSubsystem m_shooter;
    private final FeederSubsystem  m_feeder;
    private final double           m_feederVoltage;

    public FeedToShooter(ShooterSubsystem shooter, FeederSubsystem feeder, double feederVoltage) {
        m_shooter       = shooter;
        m_feeder        = feeder;
        m_feederVoltage = feederVoltage;
        addRequirements(feeder); 
    }

    @Override
    public void initialize() {
        m_feeder.stop();
    }

    @Override
    public void execute() {
        if (m_shooter.getState() == ShooterStates.kLaunching) {
            m_feeder.feed(m_feederVoltage);
        } else {
            m_feeder.stop();
        }
    }

    @Override
    public void end(boolean interrupted) {
        m_feeder.stop();
    }

    @Override
    public boolean isFinished() {
        return false; 
    }
}