// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.xrp.XRPServo;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Lifter extends SubsystemBase {
  /** Servo angle (degrees) for the lifter's raised position. Tune this for your mechanism. */
  public static final double kUpAngleDeg = 90.0;

  /** Servo angle (degrees) for the lifter's lowered position. Tune this for your mechanism. */
  public static final double kDownAngleDeg = 0.0;

  private final XRPServo m_lifterServo;

  /** Creates a new Lifter. */
  public Lifter() {
    // Device number 5 maps to the physical Servo 2 port on the XRP
    m_lifterServo = new XRPServo(5);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  /**
   * Set the current angle of the lifter (0 - 180 degrees).
   *
   * @param angleDeg Desired lifter angle in degrees
   */
  public void setAngle(double angleDeg) {
    m_lifterServo.setAngle(angleDeg);
  }
}
