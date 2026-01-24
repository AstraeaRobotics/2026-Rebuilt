// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LEDSubsystem extends SubsystemBase {

  AddressableLED m_ledStrip; 
  AddressableLEDBuffer m_ledBuffer;
  private final int LED_LENGTH = 10;

  public LEDSubsystem() {
    m_ledStrip = new AddressableLED(0);
    m_ledStrip.setLength(LED_LENGTH);

    m_ledBuffer = new AddressableLEDBuffer(LED_LENGTH);

    m_ledStrip.setData(m_ledBuffer);
    m_ledStrip.start();
  }

  public void setColorRed(){
    LEDPattern red = LEDPattern.solid(Color.kRed);

    // Apply the LED pattern to the data buffer
    red.applyTo(m_ledBuffer);

    // Write the data to the LED strip
    m_ledStrip.setData(m_ledBuffer);
  }


  public InstantCommand testRed(){
    return new InstantCommand(() -> setColorRed()); 
  }


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
