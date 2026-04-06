package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.shooterfeeder.Launch;
import frc.robot.commands.shooterfeeder.LaunchSequence;
import frc.robot.commands.swerve.DriveRobotCentric;
import frc.robot.commands.swerve.DriveToDistance;
import frc.robot.commands.swerve.TurnWheels;
import frc.robot.subsystems.ShooterFeederSubsystem;
import frc.robot.subsystems.SwerveSubsystem;

public class DriveBackAndShoot extends SequentialCommandGroup {
    public DriveBackAndShoot(SwerveSubsystem swerve, ShooterFeederSubsystem shooterFeeder) {
        System.out.println("running drivebackandshoot");
        addCommands(
            new DriveToDistance(swerve, 0, -1.75, 0),
            // Shoot for the rest of auto
            new LaunchSequence(shooterFeeder).withTimeout(11.0)
        );
    }
}   