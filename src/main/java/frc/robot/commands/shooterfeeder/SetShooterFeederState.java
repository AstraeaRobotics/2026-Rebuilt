package frc.robot.commands.shooterfeeder;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants.ShooterFeederConstants.ShooterFeederStates;
import frc.robot.subsystems.ShooterFeederSubsystem;

public class SetShooterFeederState extends InstantCommand {

    private final ShooterFeederSubsystem m_shooterFeeder;
    private final ShooterFeederStates m_state;

    public SetShooterFeederState(ShooterFeederSubsystem shooterFeeder, ShooterFeederStates state) {
        m_shooterFeeder = shooterFeeder;
        m_state         = state;
        addRequirements(m_shooterFeeder);
    }

    @Override
    public void initialize() {
        m_shooterFeeder.setState(m_state);
    }
}