package SubSystems;

import ControlSystem.RoboSystem;
import Utilities.Constants;
import Utilities.Util;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Jared Russell
 */
public class DistanceController extends SynchronousPID implements Controller
{
    private RoboSystem robot;
    private SynchronousPID straightController;
    private double goalPosition;
    private double maxVelocity;
    private boolean isOnTarget = false;
    private static final int onTargetThresh = 30;
    private int onTargetCounter = onTargetThresh;
    public static double kOnTargetToleranceInches = Constants.DISTANCE_TOLERANCE;
    public static final double kLoopRate = 200.0;
    private double timeout = 0;
    private double startTime = 0;
    private double heading = 0;
    private double tempTol = 4;
    private static DistanceController instance = null;
    private boolean holdSpeed = false;
    private double twoball_p,twoball_i,twoball_d;
    private double straight_p,straight_i,straight_d;
    public static DistanceController getInstance()
    {
        if( instance == null )
            instance = new DistanceController();
        return instance;
    }
    public void loadParts(){
        robot = RoboSystem.getInstance();
    }
    private DistanceController()
    {
        loadProperties();
    }
    public void setDistPIDValues(double p, double i, double d){
        twoball_p = p;
        twoball_i = i;
        twoball_d = d;
    }
    public void updatePIDValues(){
        this.setPID(twoball_p, twoball_i, twoball_d);
    }
    public synchronized void setGoal(double goalDistance, double maxPower, double angle, double time, double tol,boolean force)
    {
        this.setSetpoint(goalDistance);
        this.maxVelocity = maxPower;
        this.setOutputRange(-Math.abs(maxPower), Math.abs(maxPower));
        goalPosition = goalDistance;
        startTime = System.currentTimeMillis();
        timeout = (1000 * time) + startTime;
        heading = angle;
        tempTol = tol;
        setTime();
        holdSpeed = force;
    }
    double lastDistance = 0;
    double lastCheck = 0;
    public void setTime(){
        lastCheck = System.currentTimeMillis();
    }
    public boolean checkStall(){
        double distanceTraveled = Math.abs(robot.nav.getDistance()) - Math.abs(lastDistance);
        double timeSinceCheck = System.currentTimeMillis() - lastCheck;
        if((distanceTraveled < 0.2) && (timeSinceCheck > 1)){
            return true;
        }
        lastDistance = robot.nav.getDistance();
        lastCheck = System.currentTimeMillis();
        return false;
    }

    public synchronized void reset()
    {
        super.reset();
        straightController.reset();
        isOnTarget = false;
        onTargetCounter = onTargetThresh;
        heading = 0;
    }
    public synchronized void resetZero(){
        isOnTarget = false;
        onTargetCounter = onTargetThresh;
    }
    public synchronized void resetDistance(){
        if(robot == null){
            robot = RoboSystem.getInstance();
        }
        robot.nav.resetRobotPosition(0.0, 0.0, 0.0,false);
    }
    public synchronized void run()
    {
        if(robot == null){
            robot = RoboSystem.getInstance();
        }
        if(robot.nav == null){
            robot.nav = Navigation.getInstance();
        }
        double current = robot.nav.getDistance();
        double power = this.calculate(current);
        double turn = straightController.calculate(Util.getDifferenceInAngleDegrees(heading, robot.nav.getHeadingInDegrees()));
        if(holdSpeed){
            if(inRange(current)){ //outOfTime()
                isOnTarget = true;
                robot.dt.directDrive(0, 0);
                onTargetCounter = -1;
                System.out.println("DIS 1");
            }else{
                if(this.getSetpoint() < 0){
                	 System.out.println("DIS 2");
                    robot.dt.cheesyDrive(turn, -this.maxVelocity, false);
                }else{
                	 System.out.println("DIS 3");
                    robot.dt.cheesyDrive(turn, this.maxVelocity, false);
                }
            }
        }else{
            if(inRange(current))
            {
                if(onTarget() ||  outOfTime()){ //checkStall
                	 System.out.println("DIS 4");
                    isOnTarget = true;
                }
                System.out.println("DIS 5");
                onTargetCounter--;
                if(power > this.maxVelocity){
                	power = this.maxVelocity;
                }else if( power < -this.maxVelocity){
                	power = -this.maxVelocity;
                }
                robot.dt.cheesyDrive(turn, power, false);
            }
            else
            { 
            	
            	 System.out.println("DIS 6 " + current);
                onTargetCounter = onTargetThresh;
                isOnTarget = false;
                robot.dt.cheesyDrive(turn, power, false);
            }
        }
        
        SmartDashboard.putNumber("distError", getError(current, this.getSetpoint()) );
        SmartDashboard.putNumber("DriveController_target", this.getSetpoint());
        SmartDashboard.putNumber("DriveController_power", power);
        
    }

    public boolean inRange(double current){ return Util.onTarget(goalPosition,current, tempTol);}
    public synchronized boolean onTarget()
    {
        return (onTargetCounter <= 0) || (outOfTime());
    }
    public double getError(double current, double goal){
        return goal - current;
    }

    public boolean outOfTime(){
        if(timeout < System.currentTimeMillis()){
            return true;
        }else{
            return false;
        }
    }
    public final void loadProperties()
    {
        twoball_p = Constants.DIST_KP;
        twoball_i = Constants.DIST_KI;
        twoball_d = Constants.DIST_KD;
        this.setPID(twoball_p, twoball_i, twoball_d);
        this.setOutputRange(-1, 1);

        straight_p = Constants.STRAIGHT_KP;
        straight_i = Constants.STRAIGHT_KI;
        straight_d = Constants.STRAIGHT_KD;
        straightController = new SynchronousPID(straight_p,straight_i,straight_d);

        kOnTargetToleranceInches = Constants.DISTANCE_TOLERANCE;
    }
}
