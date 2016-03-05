package SubSystems;

import java.util.Timer;
import java.util.TimerTask;

import Sensors.SuperEncoder;
import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Navigation implements PIDSource{
	
    // Navigational state
    private double x = 0.0; // positive from driver facing center of the field
    private double y = 0.0; // positive from driver looking left
    private static Navigation instance;
    private double basicDistance = 0;
    private boolean twoGyro = false;
    private double angle = 0;
    private double STARTING_ANGLE_OFFSET = 0.0;
    private final Timer mTimer = new Timer();
    private static final int K_READING_RATE = 200;
    private SuperEncoder followerWheelX;
    private ADXRS450_Gyro gyro;
    private Navigation()
    {
        start();
    }
    public static Navigation getInstance()
    {
        if( instance == null )
            instance = new Navigation();
        return instance;
    }
    public void start() {
        synchronized (mTimer) {
            mTimer.schedule(new InitTask(), 0);
        }
    }
    private class InitTask extends TimerTask {
        @Override
        public void run() {
            while (true) {
                try {
                	gyro = new ADXRS450_Gyro();
                    gyro.calibrate();
                	followerWheelX = new SuperEncoder(Ports.FOLLOWER_X,Ports.FOLLOWER_X+1,false,SuperEncoder.RESOLUTION.HIGH_RESOLUTION);
                    break;
                } catch (Exception e) {
                    System.out.println("nave" + e.getMessage());
                    synchronized (mTimer) {
                        mTimer.schedule(new InitTask(), 500);
                    }
                }
            }
            synchronized (mTimer) {
                mTimer.schedule(new UpdateTask(), 0, (int) (1000.0 / K_READING_RATE));
            }
        }
    }
    private class UpdateTask extends TimerTask {
	    public void run(){ 
	    	try{
		    	updatePosition();
		        SmartDashboard.putNumber("X",getX());
		        SmartDashboard.putNumber("Y",getY());
		        SmartDashboard.putNumber("RawDistanceX",followerWheelX.getRaw());
		        SmartDashboard.putNumber("Heading",getHeadingInDegrees());
		        SmartDashboard.putNumber("RawHeading",getRawHeading()); 
	    	}catch(Exception e){
	    		System.out.println(e);
	    	}
	        
	    }
    }
    public void update(){
    	updatePosition();
        SmartDashboard.putNumber("X",getX());
        SmartDashboard.putNumber("Y",getY());
//        SmartDashboard.putNumber("RawDistanceX",followerWheelX.getRaw());
        SmartDashboard.putNumber("Heading",getHeadingInDegrees());
    }
    public void initGyro(){
        System.out.println("init");
        SmartDashboard.putString("GYRO_STATUS", "INITIALIZING");
        System.out.println("init done");
        SmartDashboard.putString("GYRO_STATUS", "READY");
    }
    public synchronized void resetRobotPosition(double x, double y, double theta,boolean gyroReset)
    {
        this.x = x;
        this.y = y;
    }
    
    public synchronized double getX()
    {
        return x;
    }

    public synchronized double getY()
    {
        return y;
    }

    public double getHeadingInDegrees()
    {
        //return Util.boundAngle0to360Degrees(gyro.getAngleInDegrees());
    	return gyro.getAngle();
    }
    public double getRawHeading(){
//        return gyro.getAngle();
        return angle;
    }
    public double getRawHeadingInDegrees(){
    	return Util.radsToDegrees(angle);
    }

    public double getPitchInDegrees()
    {
        //return gyro.getAngle();
        return 0;
    }

    public void resetPitch()
    {
//        gyro.rezero();
//    	gyro.reset();
    }

    

    public double getFollowerWheelDistance()
    {
//        return followerWheelX.getDistance();
        return 0;
    }

    public double getDistance(){
        return basicDistance;
    }
    public void updatePosition()
    {
    	
    }
    public double pidGet() {
        return getY();
    }
    
    public class Distance implements PIDSource {
        public double pidGet(){
        	return 0;
//            return basicDistance = followerWheelY.getDistance();
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