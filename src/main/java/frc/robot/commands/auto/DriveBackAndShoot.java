package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.shooterfeeder.LaunchSequenceV2;
import frc.robot.commands.swerve.DriveToDistance;
import frc.robot.subsystems.ShooterFeederSubsystem;
import frc.robot.subsystems.SwerveSubsystem;

public class DriveBackAndShoot extends SequentialCommandGroup {
    public DriveBackAndShoot(SwerveSubsystem swerve, ShooterFeederSubsystem shooterFeeder) {
        addCommands(
            new DriveToDistance(swerve, 0, -1.72, 0),
            // Shoot for the rest of auto
            new LaunchSequenceV2(shooterFeeder).withTimeout(18.0)
        );
    }
}   