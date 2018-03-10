package org.usfirst.frc.team4999.robot.controllers;

import org.usfirst.frc.team4999.robot.RobotMap;
import org.usfirst.frc.team4999.utils.Utils;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class FlightstickXboxWrapper extends DriveController {
	
	private Joystick flightStick = RobotMap.flightStick;
	private XboxController xbox = RobotMap.xbox;
	
	private static final double MOVE_CURVE = 2;
	private static final double TURN_CURVE = 1;
	
	private static final double DEADZONE = 0.1;
	
	private static final double MAX_CLAW_SPEED = 0.4;
	
	private int currentPos = 0;
	private boolean povHeld = false;

	@Override
	public double getMoveRequest() {
		double moveRequest = -flightStick.getY();
    	moveRequest = deadzone(moveRequest, DEADZONE);
    	moveRequest = curve(moveRequest, MOVE_CURVE);
    	return moveRequest;
	}

	@Override
	public double getTurnRequest() {
		double turnRequest = flightStick.getTwist();
    	turnRequest = deadzone(turnRequest, DEADZONE);
    	turnRequest = curve(turnRequest, TURN_CURVE);
    	return turnRequest;
	}

	@Override
	public double getSpeedLimiter() {
		return Utils.map(-flightStick.getThrottle(), -1, 1, 0, 1);
	}

	@Override
	public double getLiftPosition() {
		double[] values = LiftPosition.values();
		int pov = flightStick.getPOV();
		if(pov == -1) {
			povHeld = false;
		} else if(!povHeld) {
			povHeld = true;
			if(pov == 0 && currentPos < values.length-1) {
				currentPos++;
			} else if(pov == 180 && currentPos > 0) {
				currentPos--;
			}
		}
		return values[currentPos];
	}


	@Override
	public boolean getReverseDirection() {
		return flightStick.getRawButtonPressed(2);
	}

	@Override
	public boolean getFailsafeDrive() {
		return flightStick.getRawButton(7);
	}

	@Override
	public boolean getFailsafeElbow() {
		return xbox.getBackButton();
	}

	@Override
	public boolean getHunt() {
		return xbox.getBumper(Hand.kRight);
	}

	@Override
	public boolean getShoot() {
		return xbox.getBumper(Hand.kLeft);
	}

	@Override
	public double getElbowSpeed() {
		double val = -deadzone(xbox.getTriggerAxis(Hand.kRight), DEADZONE) + deadzone(xbox.getTriggerAxis(Hand.kLeft), DEADZONE);
		return Utils.map(val, -1, 1, -MAX_CLAW_SPEED, MAX_CLAW_SPEED);
	}

}