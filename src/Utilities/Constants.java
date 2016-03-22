package Utilities;

/**
 *
 * @author Rohi Zacharia
 */
public class Constants {
    
    public static final boolean LOW_GEAR  = true; // Drivetrain low gear
    public static final double MIN_DT_POWER = 0.2;
    public static final double STARTING_ANGLE_OFFSET = 0;
    public static final int autonSelect = 2;
    
    public static final double INPUT_DELAY = 0.25;
    
    public static final double TURN_KP = 0.01; //0.020
    public static final double TURN_KI = 0.0;
    public static final double TURN_KD = 0.00;//0.02
    public static final double TURN_KFV = 0.00;
    public static final double TURN_KFA = 0.00;
    public static final double TURN_ON_TARGET_DEG = 1;
    public static final double TURN_MAX_ACCEL = 90.0;
    public static final double TURN_MAX_VEL = 90.0;
    
    public static final double DIST_KP = 0.025;
    public static final double DIST_KI = 0.00001; 
    public static final double DIST_KD = 0.05; 
    
    public static final double DIST_KP_BIG = 0.018;
    public static final double DIST_KI_BIG = 0.00001; 
    public static final double DIST_KD_BIG = 0.5; 
    
    public static final double DIST_SMALL = 10;
    public static final double STRAIGHT_KP = 0.009;//.012
    public static final double STRAIGHT_KI = 0.0;
    public static final double STRAIGHT_KD = 0.0;
    public static final double DISTANCE_TOLERANCE = 1.0;
    ////////////////////////////////////////////////////////////////////////////////////////////
    public static final int INTAKE_OFFSET          = 158; //200 pbot . 210    288
    public static final int INTAKE_ARM_MAX_ANGLE   = INTAKE_OFFSET + 147; //280 298
    public static final int INTAKE_ARM_MIN_ANGLE   = INTAKE_OFFSET + 17; ///168
    public static final int INTAKE_GRAB_BALL_ANGLE = INTAKE_OFFSET + 45; //231
    public static final int INTAKE_WAIT_FOR_GRAB   = INTAKE_OFFSET + 62;  //253
    
    public static final int INTAKE_SHOOTING_ANGLE  = INTAKE_OFFSET + 20; //188
    public static final int INTAKE_LOW_BAR_ANGLE   = INTAKE_OFFSET  + 5; //178
    public static final int INTAKE_STOW_ANGLE      = INTAKE_OFFSET   + 128; //288
      
/////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int TURRET_MAX_ANGLE = 75;
    public static final int TURRET_MIN_ANGLE = -90;
    public static final double TURRET_HOOD_MAX_ANGLE = 20.0;
    public static final double TURRET_HOOD_MIN_ANGLE = -10.0;
    public static final double TURRET_CLOSE_SHOT_MAX_ANGLE = 65;
    public static final double TURRET_CLOSE_SHOT_MIN_ANGLE = -90; //145
    public static final int TURRET_MIN_ZERO_CHECKS = 20;
    public static final int TURRET_TRACKING_ANGLE = 75;
/////////////////////////////////////////////////////////////////////////////////////////////////
    public static final double SHOOTER_FAR_SHOT  = 7000;
    public static final double SHOOTER_CLOSE_SHOT = 3600; //3500
    public static final double SHOOTER_AUTON_SIDE_SHOT = 7000;
    public static final double SHOOTER_ERROR      = 200;
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public static final double HANGER_MAX_POWER = 1.0;
/////////////////////////////////////////////////////////////////////////////////////////////////
    public static final double DRIVE_DISTANCE_PER_PULSE = 0.0127506973037588*4;      //0.03420833;
    public static final double VOLTS_TO_PSI = 53.18;
    
    public static final int GYRO_INIT = 0;
    public static final int GYRO_READY  = 1;
    
    public static final double PRESSURE_V2P = 10.0;
    
    public static final double WHEELBASE_LENGTH = 33.1625;
    public static final double WHEELBASE_WIDTH  = 21.5;
    
    public static final double POWER_STALL = 10.0;
    
    public static final double CAMERA_PIXEL_WIDTH = 640.0;
	public static final double CAMERA_FOV = 45.3;
	public static final double GRIP_X_OFFSET = -0.0;
	public static final double CAM_CALIBRATION = 1.7;
}