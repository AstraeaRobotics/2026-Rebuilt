package frc.robot;

import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.climb.ClimbDown;
import frc.robot.commands.climb.ClimbUp;
import frc.robot.subsystems.ClimbSubsystem;

public class RobotContainer {

    private final ClimbSubsystem m_climb = new ClimbSubsystem();

    private final PS4Controller m_controller = new PS4Controller(0);

    private final JoystickButton kR1 = new JoystickButton(m_controller, PS4Controller.Button.kR1.value);
    private final JoystickButton kL1 = new JoystickButton(m_controller, PS4Controller.Button.kL1.value);

    public RobotContainer() {
        configureBindings();
    }

    private void configureBindings() {
        kR1.whileTrue(new ClimbUp(m_climb));
        kL1.whileTrue(new ClimbDown(m_climb));
    }

    public Command getAutonomousCommand() {
        return null;
    }
}