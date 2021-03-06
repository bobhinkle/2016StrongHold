package SubSystems;

import Utilities.Constants;
import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Navigation implements PIDSource{
	
    Gyro gyro2;
    // Navigational state
    private double x = 0.0; // positive from driver facing center of the field
    private double y = 0.0; // positive from driver looking left
    private static Navigation instance;
    private double basicDistance = 0;
    private boolean twoGyro = false;
    private double angle = 0;
    private double STARTING_ANGLE_OFFSET = 0.0;
    private Navigation()
    {
        
    }
    public static Navigation getInstance()
    {
        if( instance == null )
        {
            instance = new Navigation();
        }
        return instance;
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
    	return 0;
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

    public synchronized void run()
    {
/*        updatePosition();
        SmartDashboard.putNumber("X",getX());
        SmartDashboard.putNumber("Y",getY());
        SmartDashboard.putNumber("RawDistanceX",followerWheelX.getRaw());
        SmartDashboard.putNumber("RawDistanceY",followerWheelY.getRaw());
        SmartDashboard.putNumber("Heading",getHeadingInDegrees());
        SmartDashboard.putNumber("RawHeading",getRawHeading());   */     
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