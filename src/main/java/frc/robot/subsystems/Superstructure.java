package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Landmarks;
import frc.robot.commands.climb.SetClimbState;
import frc.robot.commands.intake.EjectFuel;
import frc.robot.commands.intake.IntakeFuel;
import frc.robot.commands.shooter.FeedToShooter;
import frc.robot.commands.shooter.ShootFuel;
import frc.robot.Constants.ClimbConstants.ClimbStates;

public class Superstructure extends SubsystemBase {

    private final IntakeSubsystem  m_intake;
    private final FeederSubsystem  m_feeder;
    private final ShooterSubsystem m_shooter;
    private final ClimbSubsystem   m_climb;
    private final SwerveSubsystem  m_swerve;

    // Toggle state — false = fixed 9.5V, true = pose-based
    private boolean m_poseShootingEnabled = false;

    public static final double kFixedShooterVoltage  = 9.5;
    public static final double kTransitionVoltage    = 5.0;
    public static final double kFeederIntakeVoltage  = 2.0;  // TODO: tune
    public static final double kFeederShootVoltage   = 10.0; // TODO: tune
    public static final double kIntakeRollerVoltage  = 8.0;  // TODO: tune
    public static final double kEjectRollerVoltage   = -6.0; // TODO: tune
    public static final double kFeederEjectVoltage   = -6.0; // TODO: tune
    public static final double kShootCycleTimeout    = 3.5;  // TODO: tune

    public Superstructure(IntakeSubsystem intake, FeederSubsystem feeder,
                          ShooterSubsystem shooter, ClimbSubsystem climb,
                          SwerveSubsystem swerve) {
        m_intake  = intake;
        m_feeder  = feeder;
        m_shooter = shooter;
        m_climb   = climb;
        m_swerve  = swerve;
    }

    /** Called by the circle button binding to flip the toggle. */
    public void togglePoseShooting() {
        m_poseShootingEnabled = !m_poseShootingEnabled;
    }

    public boolean isPoseShootingEnabled() {
        return m_poseShootingEnabled;
    }

    /**
     * Computes the shooter voltage to use right now.
     * If pose shooting is on, looks up distance from pose.
     * If off, returns the fixed 9.5V constant.
     */
    private double computeShooterVoltage() {
        if (!m_poseShootingEnabled) {
            return kFixedShooterVoltage;
        }

        double distanceMeters = m_swerve.getPose()
            .getTranslation()
            .getDistance(Landmarks.hubPosition());

        return m_shooter.getVoltageForDistance(distanceMeters);
    }

    // ── Commands ──────────────────────────────────────────────────────────────

    public Command getIntakeCommand() {
        return new IntakeFuel(m_intake, m_feeder, kIntakeRollerVoltage, kFeederIntakeVoltage);
    }

    public Command getEjectCommand() {
        return new EjectFuel(m_intake, m_feeder, kEjectRollerVoltage, kFeederEjectVoltage);
    }

    /**
     * Shoot command — voltage is snapshotted at the moment the command is scheduled,
     * so it won't jump around mid-shot if the robot drifts slightly.
     */
    public Command getShootCommand() {
        double voltage = computeShooterVoltage();
        return new ShootFuel(m_shooter, voltage, kTransitionVoltage)
            .deadlineFor(new FeedToShooter(m_shooter, m_feeder, kFeederShootVoltage))
            .withTimeout(kShootCycleTimeout);
    }

    public Command getVisionShootCommand(Command visionAlignCommand) {
        return visionAlignCommand.alongWith(getShootCommand());
    }

    public Command getClimbCommand(ClimbStates targetState) {
        return new SetClimbState(m_climb, targetState);
    }

    public Command getIdleCommand() {
        return new frc.robot.commands.IdleState(m_intake, m_feeder, m_shooter);
    }

    @Override
    public void periodic() {
        SmartDashboard.putBoolean("Shooter/PoseMode", m_poseShootingEnabled);
        SmartDashboard.putNumber("Shooter/PlannedVoltage", computeShooterVoltage());
    }
}