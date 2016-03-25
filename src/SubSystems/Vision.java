package SubSystems;

import java.util.Timer;
import java.util.TimerTask;

import Utilities.Constants;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision {

	private static Vision instance = null;
	    // From server
		private static double cameraAngle = 5000.0;
		public static volatile double gripX = 0.0;
		public static volatile double width = 0.0;
		public static double[] centerXArray;
		public static double[] centerYArray;
		private static double[] gripAreaArray;
		private static double[] widthArray;
		private final Timer mTimer = new Timer();
		// Grip network
		private final NetworkTable grip = NetworkTable.getTable("GRIP");
		public Process gripProcess;
		private static final int K_READING_RATE = 200;
		private final double[]  DUMMY = {5000};
		private boolean targetSeen = false;
		private static volatile double gripCenterY = 0.0;
	
	public Vision(){
		SmartDashboard.putString("VISION","INIT2");
        updateGripNetwork();
	}
	public void start() {
        synchronized (mTimer) {
            mTimer.schedule(new InitTask(), 0);
        }
    }
	public static Vision getInstance(){
		if(instance == null)
			instance = new Vision();
		return instance;
	}
	
	private class InitTask extends TimerTask {
        @Override
        public void run() {
            while (true) {
                try {
            		SmartDashboard.putString("VISION","INIT2");
                    updateGripNetwork();
                    break;
                } catch (Exception e) {
                	break;
                }
            }
            synchronized (mTimer) {
            	SmartDashboard.putString("VISION","STARTED");
                mTimer.schedule(new UpdateTask(), 0, (int) (K_READING_RATE));
            }
        }
    }
	public static double getCameraAngleFromBeaglebone() {
    	return cameraAngle;
    }
    public synchronized double getX(){
    	return gripX;
    }
    public void updateGripNetwork() {
    	centerXArray = grip.getSubTable("vision").getNumberArray("centerX", DUMMY);
        gripAreaArray = grip.getSubTable("vision").getNumberArray("area", DUMMY);
        centerYArray = grip.getSubTable("vision").getNumberArray("centerY", DUMMY);
        widthArray   = grip.getSubTable("vision").getNumberArray("width", DUMMY);
        if(centerXArray.length != 0 && centerYArray.length != 0 && widthArray.length != 0) {
        	targetSeen = true;
        	double maxArea = 0;
        	int maxIndex = 0;
        	for(int i = 0; i < gripAreaArray.length; i++){
        		if(gripAreaArray[i]>maxArea){
        			maxArea = gripAreaArray[i];
        			maxIndex = i;
        		}
        	}
        	gripCenterY = centerXArray[maxIndex];
        	gripX = centerXArray[maxIndex];
        	width = widthArray[maxIndex];
        }else {
        	targetSeen = false;
        	gripX = 0.0;
        	gripCenterY = 0.0;     
        	width = 0.0;
        }
    }
    
    public static double getAngle(double x){
        double slope = Constants.CAMERA_FOV/Constants.CAMERA_PIXEL_WIDTH;
        double intercept = -Constants.CAMERA_FOV/2.0;
        return (((x+Constants.GRIP_X_OFFSET)*slope)+intercept)*Constants.CAM_CALIBRATION; //gripX
    }
    public static double getAngle(){
    	return -getAngle(gripX);
    }
    public void update(){
    	updateGripNetwork();
    	SmartDashboard.putNumber("AngeToTurnAim", getAngle());
    	SmartDashboard.putBoolean("TARGET_SEEN", isTargetSeen()); 
    	SmartDashboard.putNumber("XCoorX", gripX);
    	SmartDashboard.putNumber("X_MIN",gripCenterY);
    	SmartDashboard.putString("VISION","FINISHED");
//    	System.out.println(gripX + " "  + getAngle(gripX) + " " + " " + gripCenterY + " " + width + " " + isTargetSeen());
    	
    }
    public boolean isTargetSeen() {
//    	return Math.abs(getAngle()) != 27.0;
    	return targetSeen;
    }
    private class UpdateTask extends TimerTask {
	    public void run(){ 	    	
    		SmartDashboard.putString("VISION","UPDATING");
    		updateGripNetwork();
        	SmartDashboard.putNumber("AngeToTurnAim", getAngle());
        	SmartDashboard.putBoolean("TARGET_SEEN", isTargetSeen()); 
        	SmartDashboard.putNumber("XCoorX", gripX);
        	SmartDashboard.putString("VISION","FINISHED");	
	    }
    }
}