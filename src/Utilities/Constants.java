package Utilities;

public class Constants {
    
    public static final boolean LOW_GEAR  = true; // Drivetrain low gear
    public static final double MIN_DT_POWER = 0.2;
    public static final double STARTING_ANGLE_OFFSET = 0;
    public static final int autonSelect = 2;
    
    public static final double INPUT_DELAY = 0.25;
    
    public static final double TURN_KP = 0.05; //0.020
    public static final double TURN_KI = 0.0;
    public static final double TURN_KD = 0.00;//0.02
    public static final double TURN_KFV = 1.0;
    public static final double TURN_KFA = 1.0;
    public static final double TURN_ON_TARGET_DEG = 1;
    public static final double TURN_MAX_ACCEL = 40.0;
    public static final double TURN_MAX_VEL = 40.0;
    
    public static final double DIST_KP = 0.08;  
    public static final double DIST_KI = 0.0001; 
    public static final double DIST_KD = 0.2; 
    
    public static final double DIST_KP_BIG = 0.018;
    public static final double DIST_KI_BIG = 0.00003; 
    public static final double DIST_KD_BIG = 0.5; 
    
    public static final double DIST_SMALL = 10;
    
    public static final double STRAIGHT_KP = 0.05;//.012 .05
    public static final double STRAIGHT_KI = 0.0001;
    public static final double STRAIGHT_KD = 0.0;
    public static final double DISTANCE_TOLERANCE = 1.0;
    ////////////////////////////////////////////////////////////////////////////////////////////
    public static final int INTAKE_OFFSET          = 105  ; //158 pbot
    public static final int INTAKE_ARM_MAX_ANGLE   = INTAKE_OFFSET + 147; //280 298
    public static final int INTAKE_ARM_MIN_ANGLE   = INTAKE_OFFSET + 5; ///168
    public static final int INTAKE_GRAB_BALL_ANGLE = INTAKE_OFFSET + 49; //231
    public static final int INTAKE_WAIT_FOR_GRAB   = INTAKE_OFFSET + 76;  //253
    
    public static final int INTAKE_SHOOTING_ANGLE  = INTAKE_OFFSET + 20; //188
    public static final int INTAKE_LOW_BAR_ANGLE   = INTAKE_OFFSET  + 5; //178
    public static final int INTAKE_STOW_ANGLE      = INTAKE_OFFSET   + 138; //288
      
/////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int TURRET_MAX_ANGLE = 90;
    public static final int TURRET_MIN_ANGLE = -90;
    public static final double TURRET_HOOD_MAX_ANGLE = 20.0;
    public static final double TURRET_HOOD_MIN_ANGLE = -10.0;
    public static final double TURRET_CLOSE_SHOT_MAX_ANGLE = 90;
    public static final double TURRET_CLOSE_SHOT_MIN_ANGLE = -90; //145
    public static final int TURRET_MIN_ZERO_CHECKS = 20;  //20
    public static final int TURRET_TRACKING_ANGLE = 75;
    public static final int TURRET_TRACKING_AUTO_ANGLE = 45; 
/////////////////////////////////////////////////////////////////////////////////////////////////
    public static final double SHOOTER_FAR_SHOT  = 3750; // 3800
    public static final double SHOOTER_CLOSE_SHOT = 3200; //3150
    public static final double SHOOTER_AUTON_SIDE_SHOT = 4000;
    public static final double SHOOTER_LOAD_UP  = 2000;
    public static final double SHOOTER_ERROR      = 225;
    public static final double SHOOTER_LOADER_LBS = 20;
    public static final double SHOOTER_FIRED_SPEED = 500;
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public static final double HANGER_MAX_POWER = 1.0;
/////////////////////////////////////////////////////////////////////////////////////////////////
    public static final double DRIVE_DISTANCE_PER_PULSE = 0.0138664201525306*4;      //0.03420833;
    public static final double VOLTS_TO_PSI = 53.18;
    
    public static final int GYRO_INIT = 0;
    public static final int GYRO_READY  = 1;
    
    public static final double PRESSURE_V2P = 10.0;
    public static final double BALL_DRAW = 11.0;
    public static final double WHEELBASE_LENGTH = 33.1625;
    public static final double WHEELBASE_WIDTH  = 21.5;
    
    public static final double POWER_STALL = 10.0;
    
    public static final double CAMERA_PIXEL_WIDTH = 640.0;
	public static final double CAMERA_FOV = 66; //45.3
	public static final double GRIP_X_OFFSET = 0.5; // negative to go left, positive to go right. 1.75
	public static final double CAM_CALIBRATION = 1.0;
}