package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants.IntakeStates;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;

public class EjectFuel extends Command {

    private final IntakeSubsystem m_intake;
    private final FeederSubsystem m_feeder;

    private final double m_ejectVoltage;
    private final double m_feederVoltage;

    public EjectFuel(IntakeSubsystem intake, FeederSubsystem feeder,
                        double ejectVoltage, double feederVoltage) {
        m_intake       = intake;
        m_feeder       = feeder;
        m_ejectVoltage = ejectVoltage;
        m_feederVoltage = feederVoltage;
        addRequirements(intake, feeder);
    }

    @Override
    public void initialize() {
        m_intake.setState(IntakeStates.kOut);
        m_intake.spinIntake(m_ejectVoltage);
        m_feeder.feed(m_feederVoltage);
    }

    @Override
    public void execute() {}

    @Override
    public void end(boolean interrupted) {
        m_intake.setState(IntakeStates.kIn);
        m_intake.stopIntake();
        m_feeder.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}