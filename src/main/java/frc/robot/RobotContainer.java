// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.PS4Controller.Button;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.ClimbConstants.ClimbStates;
import frc.robot.Constants.DrivebaseConstants;
import frc.robot.commands.swerve.DriveRobotCentric;
import frc.robot.commands.swerve.ResetGyro;
import frc.robot.commands.swerve.TeleopSwerveNEW;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterFeederSubsystem;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.SwerveSubsystem;
import frc.robot.subsystems.VisionSubsystem;
/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {

  private final SwerveSubsystem  m_swerve   = new SwerveSubsystem();
  private final IntakeSubsystem  m_intake   = new IntakeSubsystem();
  private final ShooterFeederSubsystem m_shooterFeeder  = new ShooterFeederSubsystem();
  private final ClimbSubsystem   m_climb    = new ClimbSubsystem();
  private final VisionSubsystem  m_vision   = new VisionSubsystem();

  private final Superstructure m_superstructure = new Superstructure(
    m_intake, m_shooterFeeder, m_climb, m_swerve, m_vision
  );

  private final PS4Controller m_controller = new PS4Controller(0);
  public static final GenericHID operatorGamepad = new GenericHID(1);

  private final JoystickButton kCross    = new JoystickButton(m_controller, PS4Controller.Button.kCross.value);
  private final JoystickButton kSquare   = new JoystickButton(m_controller, PS4Controller.Button.kSquare.value);
  private final JoystickButton kCircle   = new JoystickButton(m_controller, PS4Controller.Button.kCircle.value);
  private final JoystickButton kTriangle = new JoystickButton(m_controller, PS4Controller.Button.kTriangle.value);
  private final JoystickButton kR1       = new JoystickButton(m_controller, PS4Controller.Button.kR1.value);
  private final JoystickButton kL1       = new JoystickButton(m_controller, PS4Controller.Button.kL1.value);
  private final JoystickButton kR2       = new JoystickButton(m_controller, PS4Controller.Button.kR2.value);
  private final JoystickButton kL2       = new JoystickButton(m_controller, PS4Controller.Button.kL2.value);
  private final JoystickButton kOptions  = new JoystickButton(m_controller, PS4Controller.Button.kOptions.value);
  private final JoystickButton kTouchpad = new JoystickButton(m_controller, PS4Controller.Button.kTouchpad.value);

  private final POVButton pov0   = new POVButton(m_controller, 0);
  private final POVButton pov90  = new POVButton(m_controller, 90);
  private final POVButton pov180 = new POVButton(m_controller, 180);
  private final POVButton pov270 = new POVButton(m_controller, 270);

  public static final JoystickButton kOperator1 = new JoystickButton(operatorGamepad, 1); // Climb to L1
  public static final JoystickButton kOperator2 = new JoystickButton(operatorGamepad, 2); // Retract to ground

  SendableChooser<Command> chooser = new SendableChooser<>();

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    SmartDashboard.putData("Auto choices", chooser);

    m_swerve.setDefaultCommand(
      new TeleopSwerveNEW(
        m_swerve, m_controller::getLeftX, 
        m_controller::getLeftY, 
        m_controller::getRightX, 
        kSquare::getAsBoolean)
    );

    configureBindings();
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // ── Swerve ────────────────────────────────────────────────────────────
    // TODO see if theses even work
        kOptions.onTrue(new ResetGyro(m_swerve));
        kTouchpad.onTrue(new InstantCommand(m_swerve::lockWheels, m_swerve));
        kTouchpad.onFalse(new InstantCommand(m_swerve::unlockWheels, m_swerve));

        pov0.whileTrue(new DriveRobotCentric(m_swerve, -DrivebaseConstants.kRobotCentricVel, 0));
        pov180.whileTrue(new DriveRobotCentric(m_swerve, DrivebaseConstants.kRobotCentricVel, 0));
        pov270.whileTrue(new DriveRobotCentric(m_swerve, 0, -DrivebaseConstants.kRobotCentricVel));
        pov90.whileTrue(new DriveRobotCentric(m_swerve, 0, DrivebaseConstants.kRobotCentricVel));

        // ── Intake ────────────────────────────────────────────────────────────
        kR2.whileTrue(m_superstructure.getIntakeCommand());           // hold to intake
        kTriangle.onTrue(m_superstructure.getTogglePivotCommand());   // emergency pivot toggle

        // ── Shooter ───────────────────────────────────────────────────────────
        kL2.onTrue(m_superstructure.getShootCommand());               // shoot
        kR1.onTrue(m_superstructure.getVisionShootCommand());         // vision align + shoot
        kCross.whileTrue(m_superstructure.getEjectShooterCommand()); // hold to eject shooter
        kCircle.onTrue(new InstantCommand(m_superstructure::togglePoseShooting)); // toggle pose mode

        // ── Macropad (Climb) ──────────────────────────────────────────────────
        kOperator1.onTrue(m_superstructure.getClimbCommand(ClimbStates.kL1));     // climb to L1
        kOperator2.onTrue(m_superstructure.getClimbCommand(ClimbStates.kGround)); // retract to ground
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return chooser.getSelected();
  }
}
