/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.RobotMap;

/**
 * Add your docs here.
 */
public class ArcadeDrive extends Subsystem {

  private static ArcadeDrive m_instance;
	public static synchronized ArcadeDrive getInstance() {
		if (m_instance == null){
			m_instance = new ArcadeDrive();
		}
		return m_instance;	
  }
  VictorSP frontRightSp = new VictorSP(RobotMap.FRONTRIGHTDT);
  VictorSP backRightSp = new VictorSP(RobotMap.BACKRIGHTDT);
  SpeedControllerGroup rightMotors = new SpeedControllerGroup(frontRightSp, backRightSp);

  VictorSP frontLeftSP = new VictorSP(RobotMap.FRONTLEFTDT);  
  VictorSP backLeftSP = new VictorSP(RobotMap.BACKLEFTDT);
  SpeedControllerGroup leftMotors = new SpeedControllerGroup(frontLeftSP, backLeftSP);

  DifferentialDrive allDrive = new DifferentialDrive(leftMotors, rightMotors);
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
  

  public void Arcade (double moveValue, double rotateValue) {
    allDrive.arcadeDrive(moveValue, rotateValue);
  }
  



}
