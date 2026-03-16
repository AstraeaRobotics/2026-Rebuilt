// package frc.robot.subsystems;

// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import frc.robot.Landmarks;
// import frc.robot.commands.IdleState;
// import frc.robot.commands.climb.SetClimbState;
// import frc.robot.commands.shooterfeeder.EjectFuel;
// import frc.robot.commands.intake.IntakeFuel;
// import frc.robot.commands.intake.TogglePivotIntake;
// import frc.robot.commands.shooterfeeder.ShootFixedVoltage;
// import frc.robot.commands.shooterfeeder.ShootPoseVoltage;
// import frc.robot.commands.vision.VisionShoot;
// import frc.robot.Constants.ClimbConstants.ClimbStates;

// public class Superstructure extends SubsystemBase {

//     private final IntakeSubsystem        m_intake;
//     private final ShooterFeederSubsystem m_shooterFeeder;
//     private final ClimbSubsystem         m_climb;
//     private final SwerveSubsystem        m_swerve;
//     private final VisionSubsystem        m_vision;

//     // Toggle — false = fixed voltage, true = pose-based
//     private boolean m_poseShootingEnabled = false;

//     // ── Tunable voltages (TODO: tune all of these) ────────────────────────────
//     public static final double kFixedShooterVoltage = 9.5;
//     public static final double kTransitionVoltage   = 10.0;
//     public static final double kIntakeRollerVoltage = 8.0;
//     public static final double kEjectRollerVoltage  = -6.0;
//     public static final double kShooterEjectVoltage = -6.0;
//     public static final double kShootCycleTimeout   = 3.5;

//     public Superstructure(IntakeSubsystem intake,
//                           ShooterFeederSubsystem shooterFeeder,
//                           ClimbSubsystem climb,
//                           SwerveSubsystem swerve,
//                           VisionSubsystem vision) {
//         m_intake        = intake;
//         m_shooterFeeder = shooterFeeder;
//         m_climb         = climb;
//         m_swerve        = swerve;
//         m_vision        = vision;
//     }

//     // ── Pose-shooting toggle ──────────────────────────────────────────────────

//     public void togglePoseShooting() {
//         m_poseShootingEnabled = !m_poseShootingEnabled;
//     }

//     public boolean isPoseShootingEnabled() {
//         return m_poseShootingEnabled;
//     }

//     // ── Command factories ─────────────────────────────────────────────────────

//     /**
//      * Hold to intake — pivots down, waits for setpoint, spins rollers.
//      * Retracts and stops on release. Bind with whileTrue().
//      */
//     public Command getIntakeCommand() {
//         return new IntakeFuel(m_intake, kIntakeRollerVoltage);
//     }

//     /**
//      * Toggles the pivot between kOut and kIn each press.
//      * Emergency use — push stuck game pieces through.
//      * Bind with onTrue().
//      */
//     public Command getTogglePivotCommand() {
//         return new TogglePivotIntake(m_intake);
//     }

//     /**
//      * Main shoot command. Respects the pose-shooting toggle:
//      *   - Pose ON  → flywheel voltage continuously updated from live robot pose
//      *   - Pose OFF → fixed voltage
//      */
//     public Command getShootCommand() {
//         if (m_poseShootingEnabled) {
//             return new ShootPoseVoltage(
//                 m_shooterFeeder,
//                 m_swerve,
//                 kTransitionVoltage
//             ).withTimeout(kShootCycleTimeout);
//         } else {
//             return new ShootFixedVoltage(
//                 m_shooterFeeder,
//                 kFixedShooterVoltage,
//                 kTransitionVoltage
//             ).withTimeout(kShootCycleTimeout);
//         }
//     }

//     /**
//      * Shoot at a specific fixed voltage regardless of the pose toggle.
//      * Handy for autonomous routines that already know the desired voltage.
//      */
//     public Command getShootAtVoltageCommand(double shooterVoltage) {
//         return new ShootFixedVoltage(
//             m_shooterFeeder,
//             shooterVoltage,
//             kTransitionVoltage
//         ).withTimeout(kShootCycleTimeout);
//     }

//     /**
//      * Runs transition rollers in reverse to push the game piece back out.
//      * Bind with whileTrue().
//      */
//     public Command getEjectShooterCommand() {
//         return new EjectFuel(m_shooterFeeder, kShooterEjectVoltage);
//     }

//     /**
//      * Aligns to target using vision then shoots.
//      * Respects pose-shooting toggle for voltage.
//      * Bind with onTrue().
//      */
//     public Command getVisionShootCommand() {
//         return new VisionShoot(
//             m_vision,
//             m_swerve,
//             m_shooterFeeder,
//             m_poseShootingEnabled,
//             kFixedShooterVoltage,
//             kTransitionVoltage,
//             kShootCycleTimeout
//         );
//     }

//     public Command getClimbCommand(ClimbStates targetState) {
//         return new SetClimbState(m_climb, targetState);
//     }

//     /** Stops everything and retracts intake. */
//     public Command getIdleCommand() {
//         return new IdleState(m_intake, m_shooterFeeder);
//     }

//     // ── Periodic ──────────────────────────────────────────────────────────────

//     @Override
//     public void periodic() {
//         SmartDashboard.putBoolean("Shooter/PoseMode", m_poseShootingEnabled);

//         double dist = m_swerve.getPose()
//             .getTranslation()
//             .getDistance(Landmarks.hubPosition());

//         SmartDashboard.putNumber("Shooter/DistanceToHub", dist);
//         SmartDashboard.putNumber("Shooter/PlannedVoltage",
//             m_poseShootingEnabled
//                 ? m_shooterFeeder.getVoltageForDistance(dist)
//                 : kFixedShooterVoltage
//         );
//     }
// }