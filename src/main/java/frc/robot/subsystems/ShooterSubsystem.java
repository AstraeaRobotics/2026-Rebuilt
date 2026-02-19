package frc.robot.subsystems;

import java.util.ArrayList;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.Pair;
import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.VoltageUnit;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.ShooterConstants;
import frc.robot.utils.UnitsUtil.InterpolatingMeasureMap;

public class ShooterSubsystem extends SubsystemBase {

  private final SparkMax m_shooter;
  private final SparkMax m_transition;

  public static InterpolatingMeasureMap<Distance, DistanceUnit, Voltage, VoltageUnit> shotDistanceVoltageMap;

  public ShooterSubsystem() {
    m_shooter = new SparkMax(ShooterConstants.kShooter_CANID, MotorType.kBrushless);
    m_transition = new SparkMax(ShooterConstants.kTransition_CANID, MotorType.kBrushless);

    configureMotors();
    initializeInterpolationMap();
  }

  private void configureMotors() {
    SparkMaxConfig shooterConfig = new SparkMaxConfig();
    shooterConfig.smartCurrentLimit(60).idleMode(IdleMode.kCoast).inverted(false);

    SparkMaxConfig transitionConfig = new SparkMaxConfig();
    transitionConfig.smartCurrentLimit(60).idleMode(IdleMode.kCoast).inverted(false);

    m_shooter.configure(shooterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_transition.configure(transitionConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  private void initializeInterpolationMap() {
    ArrayList<Pair<Distance, Voltage>> data = new ArrayList<>();

    // Replace with tested values
    data.add(Pair.of(Units.Meters.of(2.0), Units.Volts.of(6.0)));
    data.add(Pair.of(Units.Meters.of(3.0), Units.Volts.of(7.5)));
    data.add(Pair.of(Units.Meters.of(4.0), Units.Volts.of(9.0)));
    data.add(Pair.of(Units.Meters.of(5.0), Units.Volts.of(11.0)));

    shotDistanceVoltageMap = new InterpolatingMeasureMap<>(data);
  }

  public void runShooter(double voltage) {
    m_shooter.setVoltage(voltage);
  }

  public void runTransition(double voltage) {
    m_transition.setVoltage(voltage);
  }

  public void runShootersAtDistance(Distance distance) {
    Voltage voltage = shotDistanceVoltageMap.get(distance);
    m_shooter.setVoltage(voltage.in(Units.Volts));
  }

  public void stopShooters() {
    m_shooter.setVoltage(0);
    m_transition.setVoltage(0);
  }

  @Override
  public void periodic() {}
}
