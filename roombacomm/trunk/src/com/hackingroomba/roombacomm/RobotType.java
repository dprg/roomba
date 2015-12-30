package com.hackingroomba.roombacomm;

public class RobotType {
	// encoder constants
	private final double ROOMBA_COUNTS_PER_INCH = 57;
	private final double MOBOT_COUNTS_PER_INCH = 250/80;		// 205 counts in 80 inches
	private final double TANKBOT_COUNTS_PER_INCH = 1800;
	public double countsPerInch = ROOMBA_COUNTS_PER_INCH;
	
	// spin tolerance constants
	private final int ROOMBA_TOLERANCE = 2;
	private final int MOBOT_TOLERANCE = 3;
	private final int DEFAULT_TOLERANCE = 10;
	public int tolerance = DEFAULT_TOLERANCE;
	private final int FAST_ROOMBA_SPINSPEED = 100;
	private final int SLOW_ROOMBA_SPINSPEED = 20;
	private final int FAST_MOBOT_SPINSPEED = 120;
	private final int SLOW_MOBOT_SPINSPEED = 80;
	public int fastSpinSpeed;
	public int slowSpinSpeed;
	
	// PID constants
	double ROOMBA_KP = 2.0;
	double ROOMBA_KI = 1.0;
	double ROOMBA_KD = 0.0;
	double MOBOT_KP = 0.5;
	double MOBOT_KI = 0.02;
	double MOBOT_KD = 0.8;
	double KP;
	double KI;
	double KD;


	// robot types
	public enum robotTypes {roomba, frankenRoomba, tankbot, mobot};
	robotTypes robotType;

	RobotType (robotTypes rt) 
	{
		robotType = rt;
		if ((rt == robotTypes.roomba) || (rt == robotTypes.frankenRoomba)) {
			countsPerInch = ROOMBA_COUNTS_PER_INCH;
			tolerance = ROOMBA_TOLERANCE;		// tolerance of angle for spinToHeading
			KP = ROOMBA_KP;						// roomba PID constants
			KI = ROOMBA_KI;
			KD = ROOMBA_KD;
			fastSpinSpeed = FAST_ROOMBA_SPINSPEED;
			slowSpinSpeed = SLOW_ROOMBA_SPINSPEED;
		} else if (rt == robotTypes.tankbot) {
			countsPerInch = TANKBOT_COUNTS_PER_INCH;
			tolerance = DEFAULT_TOLERANCE;		// tolerance of angle for spinToHeading
			KP = ROOMBA_KP;						// tankbot PID constants
			KI = ROOMBA_KI;
			KD = ROOMBA_KD;
			fastSpinSpeed = FAST_ROOMBA_SPINSPEED;
			slowSpinSpeed = SLOW_ROOMBA_SPINSPEED;
		} else if (rt == robotTypes.mobot) {
			countsPerInch = MOBOT_COUNTS_PER_INCH;
			tolerance = MOBOT_TOLERANCE;		// tolerance of angle for spinToHeading
			KP = MOBOT_KP;						// Mo'bot PID constants
			KI = MOBOT_KI;
			KD = MOBOT_KD;
			fastSpinSpeed = FAST_MOBOT_SPINSPEED;
			slowSpinSpeed = SLOW_MOBOT_SPINSPEED;
		}
	}
	public robotTypes getRobotType() {
		return robotType;
	}

	public void setRobotType(robotTypes robotType) {
		this.robotType = robotType;
	}
}

