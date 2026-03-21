package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.shooterfeeder.Launch;
import frc.robot.commands.swerve.DriveRobotCentric;
import frc.robot.commands.swerve.DriveToDistance;
import frc.robot.subsystems.ShooterFeederSubsystem;
import frc.robot.subsystems.SwerveSubsystem;

public class DriveBackAndShoot extends SequentialCommandGroup {
    public DriveBackAndShoot(SwerveSubsystem swerve, ShooterFeederSubsystem shooterFeeder) {
        addCommands(
            // Drive backwards for ~3 seconds — tune time to get ~130 inches
            new ParallelDeadlineGroup(
                new WaitCommand(3.0),
                new DriveToDistance(swerve, 0, -2.8, 0)
            ),
            // Shoot for the rest of auto
            new Launch(shooterFeeder).withTimeout(11.0)
        );
    }
}