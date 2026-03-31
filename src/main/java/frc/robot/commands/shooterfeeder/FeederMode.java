// package frc.robot.commands.shooterfeeder;

// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj2.command.Command;
// import frc.robot.Constants.ShooterFeederConstants.ShooterFeederStates;
// import frc.robot.subsystems.ShooterFeederSubsystem;

// /**
//  * Runs the shooter and transition at feeder-mode voltages (-12 / -9.5).
//  * Bind to operator button 4 with whileTrue — motors stop when released.
//  */
// public class FeederMode extends Command {

//     private final ShooterFeederSubsystem m_shooterFeeder;

//     public FeederMode(ShooterFeederSubsystem shooterFeeder) {
//         m_shooterFeeder = shooterFeeder;
//         addRequirements(m_shooterFeeder);
//     }

//     @Override
//     public void initialize() {
//         m_shooterFeeder.setState(ShooterFeederStates.kFeederMode);
//         SmartDashboard.putString("Drive Mode", "SNIPER MODE ON");
//     }

//     @Override
//     public void end(boolean interrupted) {
//         m_shooterFeeder.setState(ShooterFeederStates.kIdle);
//         SmartDashboard.putString("Drive Mode", "NORMAL");
//     }

//     @Override
//     public boolean isFinished() {
//         return false;
//     }
// }