// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.signals.InvertedValue;

import edu.wpi.first.math.util.Units;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }

  public static class IntakeIndexConstants {
    // TODO change these IDs
    public static final int kExtendHopper_CANID = 0; 
    public static final int kIntakeMotor_CANID = 1;
    public static final int kHopperMotor_CANID = 2;  

    public static final double kIntake_ks = 0;
    public static final double kIntake_kv = 0;

    public static final double kHopper_ks = 0;
    public static final double kHopper_kv = 0;

    public enum HopperStates {
      kIn(0),
      kOut(12);

      private double m_hopperSetpoint;

      private HopperStates(double m_hopperSetpoint) {
        this.m_hopperSetpoint = m_hopperSetpoint;
      }

      public double getHopperSetpoint() {
        return m_hopperSetpoint;
      }
    }
  }

  public static final class ClimbConstants {
      public static final int kClimbMotor_CANID = 0; 
      public static final InvertedValue kClimbMotorInverted = InvertedValue.CounterClockwise_Positive; 
    
      public static final double kClimb_kP = 0.0; 
      public static final double kClimb_kI = 0.0; 
      public static final double kClimb_kD = 0.0; 
      
      public static final double kClimb_ks = 0.0;
      public static final double kClimb_kg = 0.0; 
      public static final double kClimb_kv = 0.0;
      public static final double kClimb_ka = 0.0; 
   
      public static final double kMaxHeight = 100.0; 
      public static final double kMinHeight = 0.0;
      public static final double kPositionTolerance = 2.0; 
      
      public enum ClimbStates {
          kRetracted(0.0),        
          kExtended(100.0),    
          kPartialExtend(50.0);  
          
          private final double climbSetpoint;
          
          ClimbStates(double setpoint) {
              this.climbSetpoint = setpoint;
          }
          
          public double getClimbSetpoint() {
              return climbSetpoint;
          }
      }
  }

  public static class VisionConstants {
    public static final double kXP = 0.0;
    public static final double kXI = 0.0;
    public static final double kXD = 0.0; 
    
    public static final double kYP = 0.0;
    public static final double kYI = 0.0;
    public static final double kYD = 0.0;

    public static final double kRotP = 0.0;
    public static final double kRotI = 0.0;
    public static final double kRotD = 0.0;
    
    public static final double kXTolerance = 0.5;         // degrees 
    public static final double kYTolerance = 0.2;         // degrees 
    public static final double kRotationTolerance = 1.0;  // degrees

    public static final double kTargetTY = 5.5;

    public enum AlignmentPosition {
      CENTER(0.0);  

      private final double offsetMeters;

      AlignmentPosition(double offsetMeters) {
        this.offsetMeters = offsetMeters;
      }

      public double getOffsetMeters() {
        return offsetMeters;
      }
    }
  }

  public static class DrivebaseConstants {
    public static final double kWheelBase = Units.inchesToMeters(26.125);
    public static final double kTrackWidth = Units.inchesToMeters(23.75);

    public static final double kAutoSpeedMultiplier = 0.8;

    public static final double kRobotCentricVel = 0.1;
  }

  public static class DrivebaseModuleConstants {
    public static final double kDriveGearRatio = 3.56;
    public static final double kWheelDiameter = Units.inchesToMeters(3);
    public static final double kMaxDriveVoltage = 6.0;

    public static final int kTurnEncoderPositionFactor = 360;
    public static final int kTurnEncoderVelocityFactor = 60;

    public static final double kDriveEncoderPositionFactor = (1 / kDriveGearRatio) * 2 * Math.PI * (kWheelDiameter / 2);
    public static final double kDriveEncoderVelocityFactor = 1/(60 * kDriveGearRatio);

    public static final double turnKP = 0.004;
    public static final double turnKI = 0;
    public static final double turnKD = 0;

    public static final double driveKV = 6.5; 
    public static final double driveKS = 0.25;
  }
}
