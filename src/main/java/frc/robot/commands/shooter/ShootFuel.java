package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterConstants.ShooterStates;
import frc.robot.subsystems.ShooterSubsystem;

public class ShootFuel extends Command {

    private final ShooterSubsystem m_shooter;
    private final double           m_shooterVoltage;
    private final double           m_transitionVoltage;

    public ShootFuel(ShooterSubsystem shooter, double shooterVoltage, double transitionVoltage) {
        m_shooter           = shooter;
        m_shooterVoltage    = shooterVoltage;
        m_transitionVoltage = transitionVoltage;
        addRequirements(shooter);
    }

    @Override
    public void initialize() {
        m_shooter.setState(ShooterStates.kSpinningUp);
        m_shooter.setShooterVoltage(m_shooterVoltage);
        m_shooter.setTransitionVoltage(m_transitionVoltage);

        SmartDashboard.putNumber("Shooter/TargetVoltage", m_shooterVoltage);
        SmartDashboard.putString("Shooter/State", "SpinningUp");
    }

    @Override
    public void execute() {
        m_shooter.setShooterVoltage(m_shooterVoltage);
        m_shooter.setTransitionVoltage(m_transitionVoltage);

        if (m_shooter.getState() == ShooterStates.kSpinningUp && m_shooter.atTargetVoltage()) {
            m_shooter.setState(ShooterStates.kLaunching);
            SmartDashboard.putString("Shooter/State", "Launching");
        }
    }

    @Override
    public void end(boolean interrupted) {
        m_shooter.setState(ShooterStates.kIdle);
        m_shooter.stopAll();
        SmartDashboard.putString("Shooter/State", "Idle");
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}