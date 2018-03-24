package org.usfirst.frc.team4999.robot.controllers;

import org.usfirst.frc.team4999.robot.RobotMap;
import org.usfirst.frc.team4999.utils.Utils;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class XboxF310Wrapper extends DriveController {

	private XboxController xbox = RobotMap.xbox;
	private LogitechF310 logitech = RobotMap.f310;
	
	private static final double MOVE_CURVE = 2.5;
	private static final double TURN_CURVE = 2.5;
	
	private static final double DEADZONE = 0.1;
	private static final double MAX_CLAW_SPEED = 0.4;
	
	private static final double[] SPEEDS = {0.2, 0.4, 0.6, 0.8, 1};
	private int currentSpeed = SPEEDS.length - 1;
	
	private static final double LIFT_SPEED = 0.8;
	
	private static final double CLIMB_HOLD_TIME = 2;
	
	private int currentPos = 0;
	private boolean povHeld = false;
	
	private Timer climbTimer = new Timer();
	
	public XboxF310Wrapper() {
		climbTimer.start();
	}
	
	@Override
	public double getMoveRequest() {
		double moveRequest = xbox.getY(XboxController.Hand.kLeft);
    	moveRequest = Utils.deadzone(moveRequest, DEADZONE);
    	moveRequest = Utils.curve(moveRequest, MOVE_CURVE);
    	return moveRequest;
	}

	@Override
	public double getTurnRequest() {
		double turnRequest = RobotMap.xbox.getX(XboxController.Hand.kRight);
    	turnRequest = Utils.deadzone(turnRequest, DEADZONE);
    	turnRequest = Utils.curve(turnRequest, TURN_CURVE);
    	return turnRequest;
	}

	@Override
	public double getSpeedLimiter() {
		if(xbox.getYButtonPressed() && currentSpeed < SPEEDS.length - 1) {
			currentSpeed++;
		} else if(xbox.getXButtonPressed() && currentSpeed > 0) {
			currentSpeed--;
		}
		
		return SPEEDS[currentSpeed];
	}

	@Override
	public boolean getReverseDirection() {
		return xbox.getBButtonPressed();
	}

	@Override
	public boolean getFailsafeDrive() {
		return xbox.getStartButton();
	}

	@Override
	public boolean getFailsafeCubes() {
		return logitech.getBackButton();
	}

	@Override
	public boolean getIntake() {
		return logitech.getBumper(Hand.kLeft);
	}

	@Override
	public boolean getShoot() {
		return logitech.getBumper(Hand.kRight);
	}

	@Override
	public double getElbowSpeed() {
		double val = Utils.clip(logitech.getY(Hand.kLeft) + logitech.getY(Hand.kRight), -1, 1);
		return Utils.map(val, -1, 1, -MAX_CLAW_SPEED, MAX_CLAW_SPEED);
	}

	@Override
	public double getLiftPosition() {
		double[] values = LiftPosition.values();
		int pov = xbox.getPOV();
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
	public double getLiftSpeed() {
		int pov = xbox.getPOV();
		if(pov == 315 || pov == 0 || pov == 45)
			return LIFT_SPEED;
		else if(pov == 135 || pov == 180 || pov == 225)
			return -LIFT_SPEED;
		else
			return 0;
	}

	@Override
	public int getCubeManagerButton() {
		return 0;
	}

	@Override
	public boolean climb() {
		if(xbox.getPOV() == 90) {
			return climbTimer.hasPeriodPassed(CLIMB_HOLD_TIME);
		} else {
			climbTimer.reset();
			return false;
		}
	}
	
	@Override
	public void vibrate(double intensity) {
		xbox.setRumble(RumbleType.kLeftRumble, intensity);
	}
	
	@Override
	public boolean useCubeManager() {
		return false;
	}
	
	@Override
	public boolean shiftLift() {
		return xbox.getBumperPressed(Hand.kLeft) || xbox.getBumperPressed(Hand.kRight);
	}

}