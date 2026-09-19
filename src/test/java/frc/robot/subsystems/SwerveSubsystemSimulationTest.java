// package frc.robot.subsystems;

// import static org.junit.jupiter.api.Assertions.assertTrue;

// import edu.wpi.first.hal.HAL;
// import edu.wpi.first.math.kinematics.ChassisSpeeds;
// import edu.wpi.first.wpilibj.simulation.DriverStationSim;
// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;

// class SwerveSubsystemSimulationTest {
//   @BeforeAll
//   static void initializeHal() {
//     assertTrue(HAL.initialize(500, 0));
//     DriverStationSim.setEnabled(true);
//     DriverStationSim.setAutonomous(false);
//     DriverStationSim.notifyNewData();
//   }

//   @AfterAll
//   static void shutdownHal() {
//     DriverStationSim.resetData();
//     HAL.shutdown();
//   }

//   @Test
//   void commandedVelocityAdvancesSimulatedPose() {
//     SwerveSubsystem swerve = new SwerveSubsystem();

//     for (int i = 0; i < 100; i++) {
//       swerve.drive(new ChassisSpeeds(1.0, 0.0, 0.0), false, false);
//       swerve.simulationPeriodic();
//       swerve.periodic();
//     }

//     assertTrue(
//       swerve.getPose().getX() > 1.0,
//       "The simulated drivetrain should move forward on Field2d"
//     );

//     double headingBeforeTurn = swerve.getPose().getRotation().getDegrees();
//     for (int i = 0; i < 100; i++) {
//       swerve.drive(new ChassisSpeeds(0.0, 0.0, 1.0), false, false);
//       swerve.simulationPeriodic();
//       swerve.periodic();
//     }

//     assertTrue(
//       Math.abs(swerve.getPose().getRotation().getDegrees() - headingBeforeTurn) > 45.0,
//       "The simulated gyro should rotate the robot pose on Field2d"
//     );
//   }
// }
