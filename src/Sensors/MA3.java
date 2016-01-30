/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Sensors;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 *
 * @author xpsl05x
 */
public class MA3 implements PIDSource{
    private AnalogInput absEncoder;
    private double voltsToDegrees = 5.0/360.0;
    private double offset = 0;
    private double lastTime;
    private double lastAngle;
    
    public MA3(int port){
        absEncoder = new AnalogInput(port);
    }
    public double getAngle(){
        return Math.floor((-absEncoder.getVoltage()/voltsToDegrees)+offset);
    }
    public double pidGet() { return getAngle(); }
    public double getRaw(){
        return absEncoder.getVoltage();
    }
    public double angleToRaw(double angle){
        return (angle-offset);
    }
    public double getSpeed() {
        /* Note: speed is in arbitrary units.
         * Not terribly precise, but good enough for our application
        Also, not a particularly robust implementation. Needs to be called
        back to back */
        double speed = Math.abs(this.getAngle() - lastAngle)/(System.currentTimeMillis() - lastTime) * 100;
        lastAngle = this.getAngle();
        lastTime = System.currentTimeMillis();
        return speed;
    }
	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public PIDSourceType getPIDSourceType() {
		// TODO Auto-generated method stub
		return null;
	}
}
