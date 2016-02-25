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
    
    public static final double STEERING_P = 0.01; //0.0
    public static final double STEERING_I = 0.0;  //
    public static final double STEERING_D = 0.0;  //0.0
    public static final double INPUT_DELAY = 0.25;
    
    public static final double DIST_KP = 0.005;
    public static final double DIST_KI = 0.00; 
    public static final double DIST_KD = 0.000;
    public static final double DIST_KFV = 0.0001;
    public static final double DIST_KFA = 0.0001;
    public static final double DIST_MAX_ACCEL = 320.0;
    public static final double DIST_MAX_VEL = 4200.0;
    public static final double X_MOVE = 4800.0;		
    public static final double DIST_SMALL = 10;
    
    public static final double DISTANCE_TOLERANCE = 2.0; //auton distance pid
    
    public static final double TURN_KP = 0.02; //0.020
    public static final double TURN_KI = 0.00;
    public static final double TURN_KD = 0.02;//0.02
    public static final double TURN_KFV = 0.0000; //0.0001
    public static final double TURN_KFA = 0.0000; //0.0001
    public static final double TURN_ON_TARGET_DEG = 1;
    public static final double TURN_MAX_ACCEL = 15.0; //25
    public static final double TURN_MAX_VEL = 600.0; //900
    public static final double MAX_ROTATION_ANGLE_PER_SEC = 2;
    public static final double TURN_KP_TURN = 0.08;
    ////////////////////////////////////////////////////////////////////////////////////////////
    public static final int INTAKE_ARM_MAX_ANGLE = 290;
    public static final int INTAKE_ARM_MIN_ANGLE = 100;
    public static final int INTAKE_GRAB_BALL_ANGLE = 186;
    public static final int INTAKE_SHOOTING_ANGLE   = 180;
    public static final int INTAKE_LOW_BAR_ANGLE   = 153;
    public static final int INTAKE_STOW_ANGLE      = 280;
/////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int TURRET_MAX_ANGLE = 90;
    public static final int TURRET_MIN_ANGLE = -90;
    public static final double TURRET_HOOD_MAX_ANGLE = 20.0;
    public static final double TURRET_HOOD_MIN_ANGLE = -10.0;
/////////////////////////////////////////////////////////////////////////////////////////////////
    public static final double SHOOTER_FAR_SHOT  = 6000;
    public static final double SHOOTER_CLOSE_SHOT = 4000;
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public static final double DRIVE_DISTANCE_PER_PULSE = 0.008607439 * 2;      //0.03420833;
    public static final double VOLTS_TO_PSI = 53.18;
    
    public static final int GYRO_INIT = 0;
    public static final int GYRO_READY  = 1;
    
    public static final double WHEELBASE_LENGTH = 33.1625;
    public static final double WHEELBASE_WIDTH  = 21.5;
    
    public static final double POWER_STALL = 10.0;
}