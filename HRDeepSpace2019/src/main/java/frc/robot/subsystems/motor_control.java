/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;

/**
 * Add your docs here.
 */
public class motor_control extends Subsystem {
  
VictorSP exampleVictorSP = new VictorSP(8);
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  private static motor_control m_instance;
	public static synchronized motor_control getInstance() {
		if (m_instance == null){
			m_instance = new motor_control();
		}
		return m_instance;	
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
  public void run_motor(double speed) {
    exampleVictorSP.set(speed);

  }
}
