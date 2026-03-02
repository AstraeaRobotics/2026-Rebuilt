package frc.robot.subsystems;

import java.util.ArrayList;
import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
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
import frc.robot.Constants.ShooterConstants.ShooterStates;
import frc.robot.utils.UnitsUtil.InterpolatingMeasureMap;

public class ShooterSubsystem extends SubsystemBase {

    private final SparkMax m_shooterMotor;
    private final SparkMax m_transitionMotor;

    private ShooterStates m_state = ShooterStates.kIdle;

    public static InterpolatingMeasureMap<Distance, DistanceUnit, Voltage, VoltageUnit> shotDistanceVoltageMap;

    public ShooterSubsystem() {
        m_shooterMotor    = new SparkMax(ShooterConstants.kShooter_CANID, MotorType.kBrushless);
        m_transitionMotor = new SparkMax(ShooterConstants.kTransition_CANID, MotorType.kBrushless);

        configureMotors();
        initializeInterpolationMap();
    }

    private void configureMotors() {
        SparkMaxConfig shooterConfig = new SparkMaxConfig();
        shooterConfig
            .idleMode(IdleMode.kCoast)
            .smartCurrentLimit(60)
            .inverted(false);

        SparkMaxConfig transitionConfig = new SparkMaxConfig();
        transitionConfig
            .idleMode(IdleMode.kBrake)
            .smartCurrentLimit(60)
            .inverted(false);

        m_shooterMotor.configure(shooterConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        m_transitionMotor.configure(transitionConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    private void initializeInterpolationMap() {
        ArrayList<Pair<Distance, Voltage>> data = new ArrayList<>();
        // TODO: Replace with real tested values
        data.add(Pair.of(Units.Meters.of(2.0), Units.Volts.of(6.0)));
        data.add(Pair.of(Units.Meters.of(3.0), Units.Volts.of(7.5)));
        data.add(Pair.of(Units.Meters.of(4.0), Units.Volts.of(9.0)));
        data.add(Pair.of(Units.Meters.of(5.0), Units.Volts.of(11.0)));
        shotDistanceVoltageMap = new InterpolatingMeasureMap<>(data);
    }

    /**
     * Looks up shooter voltage for a given distance.
     * Clamps to nearest edge value if outside the 2-5m range.
     */
    public double getVoltageForDistance(double distanceMeters) {
        return shotDistanceVoltageMap
            .get(Units.Meters.of(distanceMeters))
            .in(Units.Volts);
    }

    public void setState(ShooterStates state) {
        m_state = state;
    }

    public ShooterStates getState() {
        return m_state;
    }

    public double getShooterVoltage() {
        return m_shooterMotor.getAppliedOutput() * m_shooterMotor.getBusVoltage();
    }

    public boolean atTargetVoltage() {
        return getShooterVoltage() >= (m_state.getShooterVoltage() - ShooterConstants.kVoltageTolerance);
    }

    public void setShooterVoltage(double voltage) {
        m_shooterMotor.setVoltage(voltage);
    }

    public void setTransitionVoltage(double voltage) {
        m_transitionMotor.setVoltage(voltage);
    }

    public void stopAll() {
        m_shooterMotor.setVoltage(0.0);
        m_transitionMotor.setVoltage(0.0);
    }

    @Override
    public void periodic() {}
}