/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;

/**
 * Add your docs here.
 */
public class BallIntake extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  public VictorSP intakeMotor = new VictorSP(RobotMap.INTAKEMOTOR);
  public VictorSP intakeRaiseMotor = new VictorSP(RobotMap.INTAKERAISER);
  public Spark transportMotor = new Spark(RobotMap.BALLTRANSPORT);
  DigitalInput topEndStop = new DigitalInput(RobotMap.TOPGRABSTOP);
  DigitalInput BottomEndStop = new DigitalInput(RobotMap.BOTTOMGRABSTOP);

  private static BallIntake m_instance;
	public static synchronized BallIntake getInstance() {
		if (m_instance == null){
			m_instance = new BallIntake();
		}
		return m_instance;	
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  public void RunIntake (double speed) {
    intakeMotor.set(speed);
  }

  public void RaiseIntake (double speed) {
    if (!topEndStop.get() && speed > 0) {
      intakeRaiseMotor.set(0);
    } else if (!BottomEndStop.get() && speed < 0) {
      intakeRaiseMotor.set(0);
    } else {
      intakeRaiseMotor.set(speed);
    }
  }

  public void RunTransport (double speed) {
    transportMotor.set(speed);
  }

}
