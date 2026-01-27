// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Superstructure extends SubsystemBase {
  /** Creates a new Superstructure. */

  private final ClimbSubsystem m_climb;
  private final HopperSubsystem m_hopper;
  private final IntakeSubsystem m_intake;
  private final ShooterSubsystem m_shooter;
  private final SwerveSubsystem m_swerve;

  private SuperstructureState m_currentState = SuperstructureState.IDLE;
  private SuperstructureState m_desiredState = SuperstructureState.IDLE;

  public enum SuperstructureState {
    IDLE,
    INTAKING,
    FEEDING,
    SHOOTING,
    CLIMBING_PREP,
    CLIMBING
  }

  public Superstructure() {
    m_climb = new ClimbSubsystem();
    m_hopper = new HopperSubsystem();
    m_intake = new IntakeSubsystem();
    m_shooter = new ShooterSubsystem();
    m_swerve = new SwerveSubsystem();
  }

  public SuperstructureState setState() {
    return m_currentState;
  }

  public boolean atDesiredState() {
    return m_currentState == m_desiredState;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
