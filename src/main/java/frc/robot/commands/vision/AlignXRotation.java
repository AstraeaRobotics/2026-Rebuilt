package frc.robot.commands.vision;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class AlignXRotation extends Command {
  private final VisionSubsystem m_VisionSubsystem;
  private final SwerveSubsystem m_SwerveSubsystem;
  private final boolean m_slowMode;
  
  private int m_finishCounter = 0;
  private static final int FINISH_THRESHOLD = 15; // 0.3 seconds at 50Hz

  public AlignXRotation(VisionSubsystem visionSubsystem, SwerveSubsystem swerveSubsystem, boolean slowMode) {
    this.m_VisionSubsystem = visionSubsystem;
    this.m_SwerveSubsystem = swerveSubsystem;
    this.m_slowMode = slowMode;

    addRequirements(m_SwerveSubsystem, m_VisionSubsystem);
  }

  @Override
  public void initialize() {
    m_finishCounter = 0;
    SmartDashboard.putString("Vision/Command", "AlignXRotation Started");
  }

  @Override
  public void execute() {
    if (!m_VisionSubsystem.hasValidTarget()) {
      m_SwerveSubsystem.drive(new ChassisSpeeds(0, 0, 0), m_slowMode);
      return;
    }
    
    double vx = m_VisionSubsystem.calculateXSpeed();
    double rotSpeed = m_VisionSubsystem.calculateRotationSpeed(m_SwerveSubsystem.getHeading());
    
    SmartDashboard.putNumber("Vision/CMD_XSpeed", vx);
    SmartDashboard.putNumber("Vision/CMD_RotSpeed", rotSpeed);
    
    ChassisSpeeds speeds = new ChassisSpeeds(0, -vx, -rotSpeed);
    m_SwerveSubsystem.drive(speeds, m_slowMode);
  }

  @Override
  public void end(boolean interrupted) {
    m_SwerveSubsystem.drive(new ChassisSpeeds(0, 0, 0), m_slowMode);
    SmartDashboard.putString("Vision/Command", interrupted ? "Interrupted" : "Complete");
  }

  @Override
  public boolean isFinished() {
    if (!m_VisionSubsystem.hasValidTarget()) {
      return false;
    }
    
    boolean aligned = m_VisionSubsystem.isAlignedX() && 
                      m_VisionSubsystem.isAlignedRotation(m_SwerveSubsystem.getHeading());
    
    if (aligned) {
      m_finishCounter++;
    } else {
      m_finishCounter = 0;
    }
    
    SmartDashboard.putNumber("Vision/FinishCounter", m_finishCounter);
    
    return m_finishCounter >= FINISH_THRESHOLD;
  }
}