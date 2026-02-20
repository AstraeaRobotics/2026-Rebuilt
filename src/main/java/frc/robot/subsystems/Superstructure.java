// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.intake.IntakeFuel;
import frc.robot.commands.shooter.ShootFuel;
import frc.robot.Constants.ClimbConstants.ClimbStates;
import frc.robot.commands.IdleState;
import frc.robot.commands.climb.SetClimbState;
import frc.robot.commands.intake.EjectFuel;
import frc.robot.commands.shooter.FeedToShooter;

public class Superstructure extends SubsystemBase {
  /** Creates a new Superstructure. */

  private final IntakeSubsystem  m_intake;
  private final FeederSubsystem  m_feeder;
  private final ShooterSubsystem m_shooter;
  private final ClimbSubsystem   m_climb;

  public static final double kFeederIntakeVoltage = 2.0;   // TODO: tune

  public static final double kFeederShootVoltage  = 10.0;   // TODO: tune

  public static final double kIntakeRollerVoltage = 8.0;   // TODO: tune

  public static final double kEjectRollerVoltage  = -6.0;  // TODO: tune

  public static final double kFeederEjectVoltage  = -6.0; // TODO: tune

  public static final double kShootCycleTimeout   = 3.5; // TODO: tune

  public Superstructure(IntakeSubsystem intake, FeederSubsystem feeder, ShooterSubsystem shooter, ClimbSubsystem climb) {
    m_intake  = intake;
    m_feeder  = feeder;
    m_shooter = shooter;
    m_climb   = climb;
  }

  public Command getIntakeCommand() {
    return new IntakeFuel(m_intake, m_feeder, kIntakeRollerVoltage, kFeederIntakeVoltage);
  }

  public Command getEjectCommand() {
    return new EjectFuel(m_intake, m_feeder, kEjectRollerVoltage, kFeederEjectVoltage);
  }

  public Command getShootCommand() {
    return new ShootFuel(m_shooter)
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
    return new IdleState(m_intake, m_feeder, m_shooter);
  }



  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
