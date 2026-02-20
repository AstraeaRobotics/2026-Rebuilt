package frc.robot.commands.climb;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants.ClimbConstants.ClimbStates;
import frc.robot.subsystems.ClimbSubsystem;

public class SetClimbState extends InstantCommand {

    private final ClimbSubsystem m_climb;
    private final ClimbStates m_state;

    public SetClimbState(ClimbSubsystem climb, ClimbStates state) {
        m_climb = climb;
        m_state = state;
        addRequirements(climb);
    }

    @Override
    public void initialize() {
        m_climb.setClimbState(m_state);
    }
}