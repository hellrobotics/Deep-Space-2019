/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;

/**
 * Add your docs here.
 */
public class HatchGrabber extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

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
    translateMotor.set(power);
  }
  

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
