package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.shooterfeeder.LaunchSequence;
import frc.robot.commands.swerve.DriveRobotCentric;
import frc.robot.subsystems.ShooterFeederSubsystem;
import frc.robot.subsystems.SwerveSubsystem;

public class DriveBackAndShoot extends SequentialCommandGroup {
    public DriveBackAndShoot(SwerveSubsystem swerve, ShooterFeederSubsystem shooterFeeder) {
        System.out.println("running drivebackandshoot");
        addCommands(
            new DriveRobotCentric(swerve, 0.5, 0).withTimeout(3),
            // Shoot for the rest of auto
            new LaunchSequence(shooterFeeder).withTimeout(11.0)
        );
    }
}   