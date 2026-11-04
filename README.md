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

## Adding the lifter to your own project

Already have your own XRP robot project, like one from another workshop? You can add the lifter without starting over. You'll copy one file, create the lifter, and connect two buttons.

### Before you start

Check that the lifter won't clash with anything your robot already uses:

- **Servo 2 port:** the lifter uses servo number `5`. Search your code for `new XRPServo(5)`. If something else already uses it, plug the lifter into **Servo 1** instead and change the `5` to `4` in `Lifter.java` (see the [servo port numbers table](#servo-port-numbers)). Check first that nothing in your code already uses `new XRPServo(4)`, like an arm.
- **D-pad up and down:** check `configureButtonBindings()` in your `RobotContainer.java` for `POVButton`, `povUp`, or `povDown`. If D-pad up or down already does something, pick different buttons in step 3.

Then plug the lifter servo into the **Servo 2** port on the XRP.

### Step 1: Copy the subsystem

Copy [`src/main/java/frc/robot/subsystems/Lifter.java`](src/main/java/frc/robot/subsystems/Lifter.java) into the `subsystems` folder of your project.

Open it and check the first line of code:

```java
package frc.robot.subsystems;
```

This must match the folder the file is in. If your project uses the standard WPILib layout, it already does. If you get errors about the package, compare it with the first line of your own `Drivetrain.java` and make them match.

The lifter's settings are at the top of `Lifter.java`, so you don't need to change `Constants.java`:

```java
public static final double kUpAngleDeg = 90.0;
public static final double kDownAngleDeg = 0.0;
```

### Step 2: Create the lifter

Open your `RobotContainer.java`. Near the top, with the other `import` lines, add:

```java
import frc.robot.subsystems.Lifter;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.POVButton;
```

(If an import is already there, you don't need to add it twice.)

Then, next to where your other subsystems are created (look for lines like `new Drivetrain()`), add:

```java
private final Lifter m_lifter = new Lifter();
```

### Step 3: Connect the buttons

Still in `RobotContainer.java`, inside `configureButtonBindings()`, add:

```java
// Lifter: press D-pad up to raise it, press D-pad down to lower it.
// The D-pad reports an angle: 0 = up, 90 = right, 180 = down, 270 = left.
new POVButton(m_controller, 0)
    .onTrue(new InstantCommand(() -> m_lifter.setAngle(Lifter.kUpAngleDeg), m_lifter));
new POVButton(m_controller, 180)
    .onTrue(new InstantCommand(() -> m_lifter.setAngle(Lifter.kDownAngleDeg), m_lifter));
```

`m_controller` is the name of the controller in this project. If yours has a different name, like `m_joystick`, use that instead.

To use regular buttons instead of the D-pad, swap `POVButton` for `JoystickButton` and use a button number. On an Xbox controller, A is 1, B is 2, X is 3, Y is 4, left bumper is 5, and right bumper is 6. You'll also need `import edu.wpi.first.wpilibj2.command.button.JoystickButton;`.

> **Using `CommandXboxController`?** Some projects create the controller with `new CommandXboxController(0)` instead of `new Joystick(0)`. If yours does, skip the `POVButton` import and write the bindings like this instead:
> ```java
> m_controller.povUp().onTrue(new InstantCommand(() -> m_lifter.setAngle(Lifter.kUpAngleDeg), m_lifter));
> m_controller.povDown().onTrue(new InstantCommand(() -> m_lifter.setAngle(Lifter.kDownAngleDeg), m_lifter));
> ```

### Step 4: Test it

1. Build your code (see [Building from a terminal](#building-from-a-terminal)) and fix any red underlines.
2. Run the code and enable **Teleoperated**.
3. Press **D-pad up**. The lifter should rise and stay up after you let go.
4. Press **D-pad down**. The lifter should lower and stay down.

A servo moves to its new angle quickly and at full strength. Letting go of the button won't stop it, because the servo keeps holding its angle. If the lifter hits something before it gets there, click **Disabled** in the simulator window right away, then adjust the angles. See [Change how far the lifter moves](#change-how-far-the-lifter-moves). If up and down are swapped, swap the two numbers.

---

## Learn more

- [WPILib documentation](https://docs.wpilib.org/en/stable/): the main reference
- [Getting started with the XRP](https://docs.wpilib.org/en/stable/docs/xrp-robot/index.html)
- [Command-based programming](https://docs.wpilib.org/en/stable/docs/software/commandbased/index.html)
- [Java basics for FRC](https://docs.wpilib.org/en/stable/docs/software/basic-programming/java-gc.html)
