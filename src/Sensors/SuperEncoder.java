/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Sensors;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
/**
 *
 * @author xpsl05x
 */
public class SuperEncoder implements PIDSource {

    private Encoder enc;
    private double lastTime = 0.0;
    private double lastRead = 0.0;
    public double rpm = 0.0;      
    private int pidReturn = 0;
    private double checkDiff = 0;
    public final static int PID_RPM = 0;
    public final static int PID_DISTANCE = 1;
    public static enum RESOLUTION{
    	LOW_RESOLUTION,HIGH_RESOLUTION
    }
    public SuperEncoder(int aChannel, int bChannel,boolean reverseDirection,RESOLUTION type) {
            // TODO Auto-generated constructor stub
        switch(type){
            case LOW_RESOLUTION:
                pidReturn = 1;
                enc = new Encoder(aChannel,bChannel,reverseDirection,EncodingType.k2X);
                enc.setSamplesToAverage((byte)100);
                break;
            case HIGH_RESOLUTION:
                pidReturn = 0;
                enc = new Encoder(aChannel,bChannel,reverseDirection,EncodingType.k4X);
                break;
        }
            
    }
//Initialize the encoder
	//Return the distance as pidGet
    public double pidGet() {
        update();
        switch(pidReturn){
            case PID_RPM:
                return getRPM();
            case PID_DISTANCE:
                return getDistance();
            default:
                return getDistance();
         
        
        } 
    }
    public void setPIDReturn(int pidSet){
        pidReturn = pidSet;
    }
    
    public void reset(){
        enc.reset();
    }
    public double getRate(){
        return enc.getRate();
    }
    public double getRaw(){
        return enc.getRaw();
    }
    public double getDistance(){
        return enc.getDistance();
    }
    
    public void start(){
    	
    }
    public void setDistancePerPulse(double dpp){
        enc.setDistancePerPulse(dpp);
    }
    public void update(){
        rpm = getRate()/4.340278;
    }
    public double getRPM()
    {        
       return rpm;
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