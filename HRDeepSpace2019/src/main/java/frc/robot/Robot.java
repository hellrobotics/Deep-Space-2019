/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.vision.VisionThread;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.VisionTracking;
import frc.robot.commands.GrabController;
import frc.robot.commands.MotorRun;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();


  VictorSP testMotor = new VictorSP(1);
  VictorSP tes2Motor = new VictorSP(2);
  private final Joystick m_stick = new Joystick(0);

  private static final int IMG_WIDTH = 320;
	private static final int IMG_HEIGHT = 240;
	
	private VisionThread visionThread;
  private double centerX = 0.0;
  private final Object imgLock = new Object();

  Command gControl = new GrabController();
  Command motorRun = new MotorRun();
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);


    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
    camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
    
    visionThread = new VisionThread(camera, new VisionTracking(), pipeline -> {
        if (!pipeline.convexHullsOutput().isEmpty()) {

          MatOfPoint biggestContour = pipeline.convexHullsOutput().get(0);
				
					for(int i = 0; i < pipeline.convexHullsOutput().size(); i++) {
						final MatOfPoint contour = pipeline.convexHullsOutput().get(i);
						double area = Imgproc.contourArea(contour);
						double biggestArea = Imgproc.contourArea(biggestContour);
						if (area > biggestArea) {
							biggestContour = contour;
						}
          }

          if (Imgproc.contourArea(biggestContour) > 20.0) {
						final Rect bb = Imgproc.boundingRect(biggestContour);
            centerX = bb.x + (bb.width/2);
          } else {
						centerX = -1;
					}
        } else {
          centerX = -1;
        }
    });
    visionThread.start();

  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */

  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();

    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        double centerX;
        synchronized (imgLock) {
          centerX = this.centerX;
        }
        if (centerX != -1) {
          double turn = centerX - (IMG_WIDTH / 2*0.25);
          System.out.println(turn/(IMG_WIDTH / 2*0.25) + " " + centerX);
          testMotor.set((turn*-0.3)/(IMG_WIDTH / 2*0.25));
          break;
        }
    }
  }

  @Override
  public void teleopInit() {
    gControl.start();
    motorRun.start();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    testMotor.set((m_stick.getThrottle()*-1+1.0)/2.0);
    tes2Motor.set((m_stick.getThrottle()*-1+1.0)*-1/2.0);
    //System.out.println("ASS: " + (m_stick.getThrottle()*-1+1.0)*-1/2.0);

    Scheduler.getInstance().run();

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
