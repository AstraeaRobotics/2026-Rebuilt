package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterConstants.ShooterStates;
import frc.robot.subsystems.ShooterSubsystem;

public class ShootFuel extends Command {

    private final ShooterSubsystem m_shooter;

    public ShootFuel(ShooterSubsystem shooter) {
        m_shooter = shooter;
        addRequirements(shooter);
    }

    @Override
    public void initialize() {
        m_shooter.setState(ShooterStates.kSpinningUp);
        m_shooter.setShooterVoltage(ShooterStates.kSpinningUp.getShooterVoltage());
    }

    @Override
    public void execute() {
        m_shooter.setShooterVoltage(m_shooter.getState().getShooterVoltage());

        if (m_shooter.getState() == ShooterStates.kSpinningUp && m_shooter.atTargetVoltage()) {
            m_shooter.setState(ShooterStates.kLaunching);
        }

        if (m_shooter.getState() == ShooterStates.kLaunching) {
            m_shooter.setTransitionVoltage(m_shooter.getState().getTransitionVoltage());
        }
    }

    @Override
    public void end(boolean interrupted) {
        m_shooter.setState(ShooterStates.kIdle);
        m_shooter.stopAll();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}