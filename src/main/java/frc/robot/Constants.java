// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.signals.InvertedValue;
import edu.wpi.first.math.util.Units;

public final class Constants {

  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }

  public static class IntakeConstants {
    public static final int kIntakeMotor_CANID = 1;  // TODO: verify CAN ID
    public static final int kPivotMotor_CANID  = 20; // TODO: verify CAN ID

    public static final double kIntakeVoltage        =  9.0;
    public static final double kIntakeReverseVoltage = -9.0;

    // Pivot is driven open-loop; output is clamped to [0, kPivotMaxPosition]
    public static final double kPivotMinPosition = 0.0;
    public static final double kPivotMaxPosition = 0.35;

    // Open-loop voltage sent to the pivot motor to hold / move to a position
    public static final double kPivotHoldVoltage = 1.0; // TODO: tune

    public enum IntakeStates {
      kIn  (0.0),   // fully retracted
      kPush(0.175), // mid position  – TODO: tune
      kOut (0.35);  // fully deployed – TODO: tune (max ~0.35)

      private final double m_pivotSetpoint;

      IntakeStates(double pivotSetpoint) {
        this.m_pivotSetpoint = pivotSetpoint;
      }

      public double getPivotSetpoint() {
        return m_pivotSetpoint;
      }
    }
  }

  public static class ShooterFeederConstants {
    public static final int kShooter_CANID          = 3; // TODO: verify
    public static final int kTransitionFeeder_CANID = 4; // TODO: verify

    public static final double kVoltageTolerance   = 0.5;
    public static final double kShooterVoltage     = -9;
    public static final double kTransitionVoltage  = -7;

    public enum ShooterFeederStates {
      kIdle      (0,  0),
      kSpinningUp(-9, 0),
      kLaunching (-9, -6);

      private final double shooterVoltage;
      private final double transitionVoltage;

      ShooterFeederStates(double shooterVoltage, double transitionVoltage) {
        this.shooterVoltage    = shooterVoltage;
        this.transitionVoltage = transitionVoltage;
      }

      public double getShooterVoltage()    { return shooterVoltage; }
      public double getTransitionVoltage() { return transitionVoltage; }
    }
  }

  public static class ClimbConstants {
    public static final int kClimbMotor_CANID = 0; // TODO: set CAN ID
    public static final int kCANcoder_CANID   = 0; // TODO: set CAN ID

    public static final InvertedValue kClimbMotorInverted =
        InvertedValue.CounterClockwise_Positive; // TODO: check inversion

    public static final double kMaxHeight = 0.0; // TODO: set (rotations)
    public static final double kMinHeight = 0.0;

    public static final double kClimb_kP = 0.0;
    public static final double kClimb_kI = 0.0;
    public static final double kClimb_kD = 0.0;

    public static final double kClimb_ks = 0.0;
    public static final double kClimb_kg = 0.0;
    public static final double kClimb_kv = 0.0;
    public static final double kClimb_ka = 0.0;

    public enum ClimbStates {
      kGround(0.0),
      kL1    (0.0); // TODO: set setpoints

      private final double climbSetpoint;

      ClimbStates(double climbSetpoint) {
        this.climbSetpoint = climbSetpoint;
      }

      public double getClimbSetpoint() { return climbSetpoint; }
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

    public static final double kXTolerance        = 0.5;
    public static final double kYTolerance        = 0.2;
    public static final double kRotationTolerance = 1.0;
    public static final double kTargetTY          = 5.5;

    public enum AlignmentPosition {
      CENTER(0.0);

      private final double offsetMeters;

      AlignmentPosition(double offsetMeters) {
        this.offsetMeters = offsetMeters;
      }

      public double getOffsetMeters() { return offsetMeters; }
    }
  }

  public static class DrivebaseConstants {
    public static final double kWheelBase  = Units.inchesToMeters(26.125);
    public static final double kTrackWidth = Units.inchesToMeters(23.75);

    public static final double kAutoSpeedMultiplier = 0.8;
    public static final double kRobotCentricVel     = 0.1;
  }

  public static class DrivebaseModuleConstants {
    public static final double kDriveGearRatio  = 3.56;
    public static final double kWheelDiameter   = Units.inchesToMeters(3);
    public static final double kMaxDriveVoltage = 6.0;

    public static final int kTurnEncoderPositionFactor = 360;
    public static final int kTurnEncoderVelocityFactor = 60;

    public static final double kDriveEncoderPositionFactor =
        (1.0 / kDriveGearRatio) * 2.0 * Math.PI * (kWheelDiameter / 2.0);
    public static final double kDriveEncoderVelocityFactor =
        1.0 / (60.0 * kDriveGearRatio);

    public static final double turnKP = 0.004;
    public static final double turnKI = 0.0;
    public static final double turnKD = 0.0;

    public static final double driveKV = 6.5;
    public static final double driveKS = 0.25;
  }
}