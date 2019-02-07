/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.OI;
import frc.robot.subsystems.BallIntake;

public class IntakeController extends Command {

  private BallIntake ssIntake;
  private OI oi;
  
  public IntakeController() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    ssIntake = BallIntake.getInstance();
    requires(ssIntake);
    oi = OI.getInstance();
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

    if (oi.stick.getPOV() == 0) {
      ssIntake.RunTransport(0.5);
    } else if (oi.stick.getPOV() == 180) {
      ssIntake.RunTransport(-0.5);
    } else {
      ssIntake.RunTransport(0);
    }

    if (oi.stick.getRawButton(3)) {
      ssIntake.RunIntake(1);
    } else if (oi.stick.getRawButton(5)) {
      ssIntake.RunIntake(-1);
    } else {
      ssIntake.RunIntake(0);
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
