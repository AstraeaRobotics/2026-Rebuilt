// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.vision;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.shooterfeeder.ShootFixedVoltage;
import frc.robot.commands.shooterfeeder.ShootPoseVoltage;
import frc.robot.subsystems.ShooterFeederSubsystem;
import frc.robot.subsystems.SwerveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class VisionShoot extends SequentialCommandGroup {
  /** Creates a new VisionShoot. */
  public VisionShoot(VisionSubsystem m_visionSubsystem,
                     SwerveSubsystem m_swerveSubsystem,
                     ShooterFeederSubsystem m_shooterFeederSubsystem,
                     boolean m_poseShootingEnable,
                     double m_fixedShooterVoltage,
                     double m_transitionVoltage,
                     double m_timeout) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(
      new AlignXRotation(m_visionSubsystem, m_swerveSubsystem, false),
      m_poseShootingEnable
        ? new ShootPoseVoltage(m_shooterFeederSubsystem, m_swerveSubsystem, m_transitionVoltage).withTimeout(m_timeout)
        : new ShootFixedVoltage(m_shooterFeederSubsystem, m_fixedShooterVoltage, m_timeout).withTimeout(m_timeout)
    );
  }
}
