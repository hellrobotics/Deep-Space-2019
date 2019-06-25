/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Add your docs here.
 */
public class OI {

	public Joystick stick = new Joystick(0);
	public Joystick stick2 = new Joystick(1);

    private static OI m_instance;
	public static synchronized OI getInstance() {
		if (m_instance == null){
			m_instance = new OI();
		}
		return m_instance;
		
	}

}
