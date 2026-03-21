// Copyright (c) FIRST and other WPILib contributors.
// Andy was here :D
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.Constants.DrivebaseConstants;
import frc.robot.commands.intake.PivotIn;
import frc.robot.commands.intake.PivotOut;
import frc.robot.commands.intake.ReverseIntake;
import frc.robot.commands.intake.RunIntake;
import frc.robot.commands.shooterfeeder.EjectTransition;
import frc.robot.commands.shooterfeeder.FeederMode;
import frc.robot.commands.shooterfeeder.LaunchSequence;
import frc.robot.commands.swerve.DriveRobotCentric;
import frc.robot.commands.swerve.ResetGyro;
import frc.robot.commands.swerve.TeleopSwerve;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterFeederSubsystem;
import frc.robot.subsystems.SwerveSubsystem;

public class RobotContainer {

  // ── Subsystems ─────────────────────────────────────────────────────────────
  private final SwerveSubsystem        m_swerve        = new SwerveSubsystem();
  private final IntakeSubsystem        m_intake        = new IntakeSubsystem();
  private final ShooterFeederSubsystem m_shooterFeeder = new ShooterFeederSubsystem();

  // ── Controllers ────────────────────────────────────────────────────────────
  private final PS4Controller    m_controller    = new PS4Controller(0);
  public static final GenericHID operatorGamepad = new GenericHID(1);

  // Driver face buttons
  private final JoystickButton kCross    = new JoystickButton(m_controller, PS4Controller.Button.kCross.value);
  private final JoystickButton kSquare   = new JoystickButton(m_controller, PS4Controller.Button.kSquare.value);
  // private final JoystickButton kCircle   = new JoystickButton(m_controller, PS4Controller.Button.kCircle.value);
  // private final JoystickButton kTriangle = new JoystickButton(m_controller, PS4Controller.Button.kTriangle.value);

  // Driver shoulder / trigger buttons
  private final JoystickButton kR1 = new JoystickButton(m_controller, PS4Controller.Button.kR1.value);
  private final JoystickButton kL1 = new JoystickButton(m_controller, PS4Controller.Button.kL1.value);
  private final JoystickButton kR2 = new JoystickButton(m_controller, PS4Controller.Button.kR2.value);
  private final JoystickButton kL2 = new JoystickButton(m_controller, PS4Controller.Button.kL2.value);

  // D-pad
  private final POVButton pov0   = new POVButton(m_controller, 0);
  private final POVButton pov90  = new POVButton(m_controller, 90);
  private final POVButton pov180 = new POVButton(m_controller, 180);
  private final POVButton pov270 = new POVButton(m_controller, 270);

  // Operator macropad
  public static final JoystickButton kOperator1  = new JoystickButton(operatorGamepad, 1);
  public static final JoystickButton kOperator2  = new JoystickButton(operatorGamepad, 2);
  public static final JoystickButton kOperator3  = new JoystickButton(operatorGamepad, 3);
  public static final JoystickButton kOperator4  = new JoystickButton(operatorGamepad, 4);
  public static final JoystickButton kOperator5  = new JoystickButton(operatorGamepad, 5);
  public static final JoystickButton kOperator6  = new JoystickButton(operatorGamepad, 6);
  public static final JoystickButton kOperator7  = new JoystickButton(operatorGamepad, 7);
  public static final JoystickButton kOperator8  = new JoystickButton(operatorGamepad, 8);
  public static final JoystickButton kOperator9  = new JoystickButton(operatorGamepad, 9);
  public static final JoystickButton kOperator10 = new JoystickButton(operatorGamepad, 10);
  public static final JoystickButton kOperator11 = new JoystickButton(operatorGamepad, 11);
  public static final JoystickButton kOperator12 = new JoystickButton(operatorGamepad, 12);

  SendableChooser<Command> chooser = new SendableChooser<>();

  public RobotContainer() {
    NamedCommands.registerCommand("Shoot", new LaunchSequence(m_shooterFeeder));

    chooser.setDefaultOption("AutoCenterV1", AutoBuilder.buildAuto("AutoCenterV1"));
    chooser.addOption("AutoRightV1", AutoBuilder.buildAuto("AutoRight"));

    SmartDashboard.putData("Auto choices", chooser);

    m_swerve.setDefaultCommand(
      new TeleopSwerve(
        m_swerve,
        m_controller::getLeftX,
        m_controller::getLeftY,
        m_controller::getRightX,
        kSquare::getAsBoolean
      )
    );

    configureBindings();
  }

  private void configureBindings() {

    // ── Swerve ───────────────────────────────────────────────────────────────
    kCross.onTrue(new ResetGyro(m_swerve));

    pov0.whileTrue(new DriveRobotCentric(m_swerve, -DrivebaseConstants.kRobotCentricVel, 0));
    pov180.whileTrue(new DriveRobotCentric(m_swerve,  DrivebaseConstants.kRobotCentricVel, 0));
    pov270.whileTrue(new DriveRobotCentric(m_swerve, 0, -DrivebaseConstants.kRobotCentricVel));
    pov90.whileTrue(new DriveRobotCentric(m_swerve, 0,  DrivebaseConstants.kRobotCentricVel));

    // ── Shooter / Feeder ─────────────────────────────────────────────────────
    kR1.whileTrue(new LaunchSequence(m_shooterFeeder));
    kL1.whileTrue(new EjectTransition(m_shooterFeeder));

    // ── Intake pivot (open-loop, hold to move) ───────────────────────────────
    kOperator1.whileTrue(new PivotIn(m_intake));
    kOperator2.whileTrue(new PivotOut(m_intake));

    // ── Feeder mode — press once to start, press again to stop ───────────────
    kOperator4.toggleOnTrue(new FeederMode(m_shooterFeeder));

    // ── Intake roller ────────────────────────────────────────────────────────
    kR2.whileTrue(new RunIntake(m_intake));
    kL2.whileTrue(new ReverseIntake(m_intake));
  }

  public Command getAutonomousCommand() {
    return chooser.getSelected();
  }
}