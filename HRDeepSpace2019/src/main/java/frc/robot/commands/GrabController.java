/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.subsystems.HatchGrabber;

public class GrabController extends Command {

  private HatchGrabber ssGrab;
	private OI oi;

  private boolean isTracking = false;

  double integral = 0;
  double previous_error = 0;


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

    int stage1Pos = ssGrab.getStage1EncoderPos();
    //System.out.println(stage1Pos);

    if (oi.stick.getRawButton(1) == true) {
      ssGrab.SetGrab(true);
    } else {
      ssGrab.SetGrab(false);
    }
    if (oi.stick.getRawButton(2) == true) {
      ssGrab.SetExtend(true);
    } else {
      ssGrab.SetExtend(false);
    }

    if (oi.stick.getPOV() == 90) {
      ssGrab.MoveGrabber(1.0);
    } else if (oi.stick.getPOV() == 270) {
      ssGrab.MoveGrabber(-1.0);
    } else {
      ssGrab.MoveGrabber(0);
    }

    if (oi.stick.getRawButtonPressed(11)){
        isTracking = !isTracking;
    }

    if (isTracking) {
      if (Robot.visionError != 0) {
        AutoMove(Robot.visionError, 20.0);
      } else {
        ssGrab.MoveGrabber(0);
        //System.out.println("PUL MÆ!!!");
      }
    }

  }

  void AutoMove (double pos, double tolerance) {
    double error = pos;
    double pk = 0.5/tolerance;
    integral += (error*.02);
    double derivative = (error - this.previous_error) / .02;
    double moveValue = error*pk+0*integral+0*derivative;
    System.out.println("AUTO TRACK ON! " + moveValue);
    ssGrab.MoveGrabber(moveValue);
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
