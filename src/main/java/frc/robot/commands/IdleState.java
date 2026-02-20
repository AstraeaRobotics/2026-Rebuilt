package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants.IntakeConstants.IntakeStates;
import frc.robot.Constants.ShooterConstants.ShooterStates;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class IdleState extends InstantCommand {

    public IdleState(IntakeSubsystem intake, FeederSubsystem feeder, ShooterSubsystem shooter) {
        super(
            () -> {
                intake.setState(IntakeStates.kIn);
                intake.stopIntake();
                feeder.stop();
                shooter.setState(ShooterStates.kIdle);
                shooter.stopAll();
            },
            intake, feeder, shooter
        );
    }
}