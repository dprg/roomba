package com.hackingroomba.roombacomm;

/*	
 * This class implements the following PID algorithm
 * previous_error = 0
 * start:
 * error = setpoint - actual_position
 * P = Kp * error
 * I = Ki * sum(error)
 * D = Kd * (error - previous_error)
 * output = P + I + D
 * previous_error = error
 * wait(dt)
 * goto start
 */
public class Pid {
	double k_p, k_i, k_d, i_state_max;               // the PID constants
	double d_state, i_state;   // the PID states
	boolean disableD = true;
	int lastError;

	Pid(double p, double i, double d)
	{
		k_p = p;
		k_i = i;
		k_d = d;
		d_state = 0.0;
		i_state = 0.0;
		i_state_max = 200.0;
	}

	public double computePid( double target, double value )
	{
		double error;
		double p, i, d;
		double ret;

	    error = target - value;
	    p = k_p * error;
	    d = k_d * (error - d_state );
	    if (disableD) {	// prevent an initial kick on the first iteration, before d_state is set.
	    	disableD = false;
	    	d = 0;
	    }
	    d_state = error;
	    i_state += error;

	    // cap I term windup
	    if( i_state > i_state_max )
	            i_state = i_state_max;
	    if( i_state < -i_state_max )
	            i_state = -i_state_max;
	    // clear I term & diable D if we overshoot
	    if (((error > 0) && (lastError < 0)) || ((error < 0) && (lastError > 0))) {
	    	i_state = 0;
	    	disableD = true;
	    }

	    i = k_i * i_state;

	    ret = p + i + d;
	    System.out.printf("error %4.1f p: %4.1f i: %4.1f d: %4.1f", error, p, i, d);
	    
	    return ret;
	}

}
