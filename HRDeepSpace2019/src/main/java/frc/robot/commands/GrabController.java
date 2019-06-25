/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.subsystems.HatchGrabber;

public class GrabController extends Command {

  private HatchGrabber ssGrab;
	private OI oi;

  private boolean isTracking = false;
  private boolean isCalibrated = false;

  double integral = 0;
  double previous_error = 0;
  int centerPos = 392;


  public GrabController() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    ssGrab = HatchGrabber.getInstance();
    requires(ssGrab);
    oi = OI.getInstance();
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

    int pos = ssGrab.getEncoderPos();
    SmartDashboard.putNumber("grabPos", pos*-1);
    SmartDashboard.putBoolean("tracking", isTracking); 
    //System.out.println(stage1Pos);

    if (isCalibrated == false) {
      ssGrab.Calibrate();
      System.out.println("CALIBRATING");
      if (!ssGrab.getRightEndstop()) {
        isCalibrated = true;
      }
    } else {

    if (oi.stick.getRawButton(1) == true || oi.stick2.getRawButton(6)) {
      ssGrab.SetGrab(true);
    } else {
      ssGrab.SetGrab(false);
    }
    if (oi.stick.getRawButton(2)||oi.stick2.getRawButton(5)) {
      ssGrab.SetExtend(true);
    } else {
      ssGrab.SetExtend(false);
    }

    if (oi.stick.getPOV() == 90) {
      ssGrab.MoveGrabber(1.0);
    } else if (oi.stick.getPOV() == 270) {
      ssGrab.MoveGrabber(-1.0);
    } else {
      ssGrab.MoveGrabber(oi.stick2.getX());
    }
/*
    if (oi.stick.getRawButtonPressed(11) || oi.stick2.getRawButtonPressed(2)){
        isTracking = !isTracking;
    }
*/
    if (isTracking) {
      if (Robot.visionError != 0) {
        ssGrab.AutoMoveGrabber(Robot.visionError, 20.0);
      } else {
        ssGrab.MoveGrabber(0);
      }
    }

    if (oi.stick.getRawButton(12) || oi.stick2.getRawButton(1)) {
      ssGrab.AutoMoveGrabber(centerPos, 50, true);
      //System.out.println("ENCODER: " + ssGrab.getEncoderPos());
    }
  }

  }

  


  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}