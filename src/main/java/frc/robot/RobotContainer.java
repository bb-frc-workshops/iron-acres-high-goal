// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.ArcadeDrive;
import frc.robot.commands.AutonomousDistance;
import frc.robot.commands.AutonomousTime;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Lifter;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.xrp.XRPOnBoardIO;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final Drivetrain m_drivetrain = new Drivetrain();
  private final XRPOnBoardIO m_onboardIO = new XRPOnBoardIO();
  private final Arm m_arm = new Arm();
  private final Lifter m_lifter = new Lifter();

  // Assumes a gamepad plugged into channel 0
  private final Joystick m_controller = new Joystick(0);

  // Create SmartDashboard chooser for autonomous routines
  private final SendableChooser<Command> m_chooser = new SendableChooser<>();

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    // ---------------------------------------------------------------------
    // HOW BUTTON BINDINGS WORK
    //
    // A "Trigger" watches some true/false condition (like "is the A button
    // held down?"). You attach commands to it that run when that condition
    // changes:
    //
    //   .onTrue(cmd)  -> runs cmd once, at the moment the condition becomes
    //                    true (the button is pressed).
    //   .onFalse(cmd) -> runs cmd once, at the moment the condition becomes
    //                    false (the button is released).
    //
    // JoystickButton and POVButton are ready-made Triggers for a numbered
    // button or a D-pad direction on a controller.
    //
    // An InstantCommand runs a single line of code and finishes right away.
    // The lambda "() -> m_arm.setAngle(45.0)" is that line of code: "when
    // this command runs, call setAngle(45.0) on the arm".
    //
    // The last argument (e.g. m_arm) tells the scheduler which subsystem the
    // command uses. Only one command can use a subsystem at a time, so a new
    // command for the arm automatically interrupts an old one.
    // ---------------------------------------------------------------------

    // DRIVING: The "default command" runs on a subsystem whenever no other
    // command is using it. Here, the drivetrain always listens to the
    // joysticks unless something else (like an autonomous routine) takes over.
    m_drivetrain.setDefaultCommand(getArcadeDriveCommand());

    // XRP USER BUTTON: The small button on the XRP board itself. Pressing and
    // releasing it just prints a message to the console. This is useful for
    // checking that your code is running.
    Trigger userButton = new Trigger(m_onboardIO::getUserButtonPressed);
    userButton
        .onTrue(new PrintCommand("USER Button Pressed"))
        .onFalse(new PrintCommand("USER Button Released"));

    // ARM, A BUTTON (button 1): While A is held, the arm moves to 45 degrees.
    // When A is released, the arm goes back to 0 degrees.
    JoystickButton joystickAButton = new JoystickButton(m_controller, 1);
    joystickAButton
        .onTrue(new InstantCommand(() -> m_arm.setAngle(45.0), m_arm))
        .onFalse(new InstantCommand(() -> m_arm.setAngle(0.0), m_arm));

    // ARM, B BUTTON (button 2): Same idea as A, but the arm moves to
    // 90 degrees while B is held.
    JoystickButton joystickBButton = new JoystickButton(m_controller, 2);
    joystickBButton
        .onTrue(new InstantCommand(() -> m_arm.setAngle(90.0), m_arm))
        .onFalse(new InstantCommand(() -> m_arm.setAngle(0.0), m_arm));

    // LIFTER, D-PAD: The D-pad (the "POV hat") reports a direction as an
    // angle in degrees: 0 = up, 90 = right, 180 = down, 270 = left. It
    // reports -1 when nothing is pressed.
    //
    // Unlike the arm buttons, the lifter has no onFalse(). Letting go of the
    // D-pad leaves the lifter where it is: press up once to raise it, press
    // down once to lower it.
    //
    // To change how high or low the lifter goes, edit kUpAngleDeg and
    // kDownAngleDeg in Lifter.java.
    POVButton dpadUp = new POVButton(m_controller, 0);
    dpadUp.onTrue(new InstantCommand(() -> m_lifter.setAngle(Lifter.kUpAngleDeg), m_lifter));

    POVButton dpadDown = new POVButton(m_controller, 180);
    dpadDown.onTrue(new InstantCommand(() -> m_lifter.setAngle(Lifter.kDownAngleDeg), m_lifter));

    // AUTONOMOUS CHOOSER: This adds a drop-down menu to the dashboard so you
    // can choose which autonomous routine runs when Autonomous is enabled.
    m_chooser.setDefaultOption("Auto Routine Distance", new AutonomousDistance(m_drivetrain));
    m_chooser.addOption("Auto Routine Time", new AutonomousTime(m_drivetrain));
    SmartDashboard.putData(m_chooser);
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return m_chooser.getSelected();
  }

  /**
   * Use this to pass the teleop command to the main {@link Robot} class.
   *
   * @return the command to run in teleop
   */
  public Command getArcadeDriveCommand() {
    return new ArcadeDrive(
        m_drivetrain, () -> -m_controller.getRawAxis(1), () -> -m_controller.getRawAxis(2));
  }
}
