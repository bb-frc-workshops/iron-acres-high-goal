# Iron Acres High Goal

Robot code for an [XRP](https://docs.wpilib.org/en/stable/docs/xrp-robot/index.html) robot, written in Java with [WPILib](https://docs.wpilib.org/), the library used in the FIRST Robotics Competition (FRC).

If you're new to robot programming, start here. This page covers what the robot does, how the code is organized, and how to run it and change it.

**Build the robot:** The 3D-printable parts for this robot are on Printables: [XBS High Goal Robot](https://www.printables.com/model/1720246-xbs-high-goal-robot).

---

## What the robot can do

| Mechanism | Hardware | What it does |
|---|---|---|
| **Drivetrain** | Two drive motors and wheel encoders | Drives forward and backward, and turns |
| **Arm** | Servo on XRP **Servo port 1** | Swings to set angles while a button is held |
| **Lifter** | Servo on XRP **Servo port 2** | Raises and lowers, staying where you put it |

A **servo** is a motor that turns to an angle you choose (here 0–180°) and holds it there. You tell it *where* to go, not how fast to spin.

---

## Controller layout

Plug a gamepad (such as an Xbox controller) into your computer. The code expects it in **port 0** of the Driver Station.

| Control | Action |
|---|---|
| Left stick, up/down | Drive forward and backward |
| Right stick, left/right (axis 2) | Turn |
| **A** (hold) | Arm moves to 45°, returns to 0° when released |
| **B** (hold) | Arm moves to 90°, returns to 0° when released |
| **D-pad up** (press) | Lifter goes up |
| **D-pad down** (press) | Lifter goes down |
| USER button on the XRP board | Prints a message to the console (handy for checking your code is running) |

> Controllers don't all number their axes the same way. If turning is on the wrong stick, check the joystick view in the simulator window to see which axis moves, and change the axis number in `getArcadeDriveCommand()` in `RobotContainer.java`.

---

## How the code is organized

This project uses WPILib's **command-based** style. The code is split into two kinds of pieces:

- **Subsystems** are the robot's physical parts. Each one owns its hardware and has simple methods like "set the angle".
- **Commands** are actions that use subsystems, such as "drive 10 inches" or "turn 180 degrees".

```
src/main/java/frc/robot/
├── Main.java              Starts the program. You won't need to edit this.
├── Robot.java             Runs the robot loop and switches between modes (teleop, auto, ...).
├── RobotContainer.java    ★ Connects everything: creates subsystems and maps buttons to commands.
├── Constants.java         A place for shared numbers, such as the controller port.
├── subsystems/
│   ├── Drivetrain.java    Drive motors, encoders, and gyro.
│   ├── Arm.java           Arm servo (Servo port 1).
│   └── Lifter.java        Lifter servo (Servo port 2), plus its up and down angles.
└── commands/
    ├── ArcadeDrive.java         Drive with the joysticks.
    ├── DriveDistance.java       Drive a set number of inches.
    ├── DriveTime.java           Drive for a set number of seconds.
    ├── TurnDegrees.java         Turn a set number of degrees.
    ├── TurnTime.java            Turn for a set number of seconds.
    ├── AutonomousDistance.java  Auto routine built from distance and degree commands.
    └── AutonomousTime.java      Auto routine built from timed commands.
```

**`RobotContainer.java` is usually where you start.** Its `configureButtonBindings()` method has comments that explain each binding.

### Servo port numbers

The labels on the XRP board don't match the numbers you use in code:

| Port on the XRP board | Number in code |
|---|---|
| Servo 1 | `4` |
| Servo 2 | `5` |

So `Arm` uses `new XRPServo(4)` and `Lifter` uses `new XRPServo(5)`.

---

## Running the code

The XRP doesn't run Java itself. The code runs **on your computer** in simulation mode and sends commands to the XRP over Wi‑Fi.

1. **Install WPILib 2026** from the [WPILib installation guide](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html). It includes VS Code, Java, and all the tools you need.
2. **Open this folder** in **WPILib VS Code**. Use the copy of VS Code that came with WPILib, not a regular VS Code install.
3. **Turn on the XRP** and connect your computer to its Wi‑Fi network. The code expects the XRP at `192.168.42.1`, which is set in `build.gradle`.
4. Press **Ctrl+Shift+P**, choose **WPILib: Simulate Robot Code**, and select **Sim GUI** if asked.
5. In the simulator window:
   - Under **System Joysticks**, drag your controller onto **Joystick[0]**.
   - Under **Robot State**, click **Teleoperated** to drive, or **Autonomous** to run the selected auto routine.

To choose an autonomous routine, use the **SendableChooser** drop-down on the dashboard (in the Sim GUI, look under **NetworkTables → SmartDashboard**).

### Building from a terminal

To check that the code compiles without running it:

```
./gradlew build
```

If you see an odd error like `Type T not present`, Gradle is using the wrong version of Java. Point it at WPILib's Java first:

```
# PowerShell
$env:JAVA_HOME = "C:\Users\Public\wpilib\2026\jdk"
./gradlew build
```

---

## Common changes

### Change how far the lifter moves

In `subsystems/Lifter.java`, edit these two numbers (0–180):

```java
public static final double kUpAngleDeg = 90.0;
public static final double kDownAngleDeg = 0.0;
```

### Bind a new button

In `RobotContainer.java`, inside `configureButtonBindings()`:

```java
// X button is button 3 on an Xbox controller.
JoystickButton xButton = new JoystickButton(m_controller, 3);
xButton.onTrue(new InstantCommand(() -> m_arm.setAngle(135.0), m_arm));
```

- `onTrue` runs when the button is **pressed**.
- `onFalse` runs when the button is **released**.
- For the D-pad, use `new POVButton(m_controller, angle)`, where angle is 0 for up, 90 for right, 180 for down, and 270 for left.

### Add a new servo mechanism

1. Copy `subsystems/Lifter.java` to a new file, rename the class, and change the servo port number.
2. Create it in `RobotContainer.java`: `private final MyThing m_myThing = new MyThing();`
3. Bind buttons to it in `configureButtonBindings()`.

---

## Learn more

- [WPILib documentation](https://docs.wpilib.org/en/stable/): the main reference
- [Getting started with the XRP](https://docs.wpilib.org/en/stable/docs/xrp-robot/index.html)
- [Command-based programming](https://docs.wpilib.org/en/stable/docs/software/commandbased/index.html)
- [Java basics for FRC](https://docs.wpilib.org/en/stable/docs/software/basic-programming/java-gc.html)
