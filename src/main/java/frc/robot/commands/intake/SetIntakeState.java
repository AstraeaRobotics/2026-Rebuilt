package frc.robot.commands.intake;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants.IntakeConstants.IntakeStates;
import frc.robot.subsystems.IntakeSubsystem;

public class SetIntakeState extends InstantCommand {

    private final IntakeSubsystem m_intake;
    private final IntakeStates m_state;

    public SetIntakeState(IntakeSubsystem intake, IntakeStates state) {
        m_intake = intake;
        m_state  = state;
        addRequirements(intake);
    }

    @Override
    public void initialize() {
        m_intake.setState(m_state);
    }
}