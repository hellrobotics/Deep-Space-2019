/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotMap;

/**
 * Add your docs here.
 */
public class HatchGrabber extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  Encoder stage1Encoder = new Encoder(RobotMap.ARM1ENCODER1, RobotMap.ARM1ENCODER2);
  DigitalInput leftEndStop = new DigitalInput(RobotMap.LEFTGRABSTOP);
  DigitalInput rightEndStop = new DigitalInput(RobotMap.RIGHTGRABSTOP);

  Solenoid pneuExtend = new Solenoid(RobotMap.EXTENDERVALVE);
  Solenoid pneuGrab = new Solenoid(RobotMap.GRABVALVE);
  Spark translateMotor = new Spark(RobotMap.HATCHMOTOR);

  private static HatchGrabber m_instance;
	public static synchronized HatchGrabber getInstance() {
		if (m_instance == null){
			m_instance = new HatchGrabber();
		}
		return m_instance;	
  }
  
  public void SetGrab (boolean grab) {  
    pneuGrab.set(grab);
  }
  public void SetExtend (boolean extend) {
    pneuExtend.set(extend);
  }
  public void MoveGrabber (double power) {
    //System.out.println(rightEndStop.get() + " " + leftEndStop.get());
    SmartDashboard.putBoolean("rightEndstop", rightEndStop.get());
    SmartDashboard.putBoolean("leftEndstop", leftEndStop.get());
    if (!rightEndStop.get() && power > 0) {
      translateMotor.set(0);
    } else if (!leftEndStop.get() && power < 0) {
      translateMotor.set(0);
    } else {
      translateMotor.set(power);
    }
  }

  public void AutoMoveGrabber (double pos, double tolerance, boolean encoded) {
    double error = 0;
    if (encoded) {
      error = (pos - getEncoderPos())*-1;
    } else {
      error = pos;
    }
    double pk = 0.5/tolerance;
    double moveValue = error*pk;
    System.out.println("AUTO TRACK ON! " + moveValue);
    MoveGrabber(moveValue);
  }

  public void AutoMoveGrabber (double pos, double tolerance) {
    AutoMoveGrabber(pos, tolerance, false);
  }


   
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
 
  public int getEncoderPos() {
    return stage1Encoder.get();
  }

  public boolean getRightEndstop() {
    return rightEndStop.get();
  }

  public void Calibrate() {
    MoveGrabber(1);
    if (!rightEndStop.get()) {
      stage1Encoder.reset();
      MoveGrabber(0);
    }
  }
}
