package SubSystems;

import java.util.Timer;
import java.util.TimerTask;

import Sensors.GyroInterface;
import Sensors.SuperEncoder;
import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends SynchronousPID implements Controller
{
    private Victor motor;
    public PIDController pidc;
    private SuperEncoder enc;
    private Solenoid hood;
    private final Timer mTimer = new Timer();
    private static final int K_READING_RATE = 200;
    private static final double PID_PROP = 0.09; //0.008   0.09
    private static final double PID_INT  = 0.0;
    private static final double PID_DIFF = 0.07 ; //0.011  0.07
    private static final double MINRPM = 0;
    private static final double MAXRPM = 6800; 
    private static final double MAXFORWARDPOWER = 1;
    private static final double MAXREVERSEPOWER = 0;
    private static final double OK_ERROR = 50; //TODO Calibrate
    private static final double FENDER_SHOT_RPM = 2750; //
//    private static final double FENDER_SHOT_RPM = 2000;
    private double bumpPower = 0;
    private static final double KEY_SHOT_RPM = 4300;
    private double buffer = 0;
    public double goal = 0;
    private static Shooter instance = null;
    public boolean fenderShot = false;
    public static Shooter getInstance()
    {
        if( instance == null )
            instance = new Shooter();
        return instance;
    }
    public void start() {
        synchronized (mTimer) {
            mTimer.schedule(new InitTask(), 0);
        }
    }
    public Shooter(){
    	this.setInputRange(MINRPM, MAXRPM);
        this.setOutputRange(-Math.abs(MAXFORWARDPOWER), Math.abs(MAXFORWARDPOWER));
    }
    
    private class InitTask extends TimerTask {
        @Override
        public void run() {
            while (true) {
                try {
                	enc = new SuperEncoder(Ports.SHOOTER_ENC,Ports.SHOOTER_ENC + 1,false,SuperEncoder.HIGH_RESOLUTION);
                    enc.start();
                    motor = new Victor(Ports.SHOOTER_MOTOR);                    
                    goal = 0;
                    break;
                } catch (Exception e) {
                    System.out.println("Gyro failed to initialize: " + e.getMessage());
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
    private double calculatePID(double value){
    	return this.calculate(value);
    }
    private void setPower(double value){
    	this.set(value);
    }
    private class UpdateTask extends TimerTask {
    	public void run()
        {
        	double current = enc.getRPM();       
            double power = -calculatePID(current);
        	double rpms = enc.rpm;
            
            if(goal == 0){
            	setPower(0);
                buffer = 0;
                bumpPower = 0;
            }else{
                
                if(goal > 6500){
                	setPower(1);
                }else{
                	setPower(power);
                }
            } 
            motor.set(-power);
            SmartDashboard.putString("shooterPID", "P " + (int)(pidc.getP()*1000) + " D " + (int)(pidc.getD()*1000) + " " + goal );
            SmartDashboard.putNumber("ShootSpeed", rpms);
            SmartDashboard.putNumber("ShooterGoal", goal);
            SmartDashboard.putNumber("ShooterPower", power);
        }
    }
    /** Used to interface with a PID controller
     *
     * @param output value for the PID controller to set the motors to
     */
    private double speed = 0;
    
    public synchronized void set(double speed){ motor.set(speed); }
    public synchronized void goTo(double _goal){ goal = Util.limit(_goal, MINRPM, MAXRPM);  } 
    public synchronized void stop(){ goal = 0; }
    public void adjust(double accel){ goTo(goal  + (accel * 200)); } 
    public boolean onGoal() { 
        if(goal > 6500){
            return true;
        }else{
            return Util.onTarget(goal, enc.rpm,OK_ERROR); 
        }
    }
    public void hoodExtend(){ hood.set(false); } 
    public void hoodRetract(){ hood.set(true); }
	
	@Override
	public boolean onTarget() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void loadProperties() {
		// TODO Auto-generated method stub
		
	}
	public void lowerP(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
        
        p -= 0.01;
        this.setPID(p, i, d);
    }
    public void upP(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
        
        p += 0.01;
        this.setPID(p, i, d);
    }
    public void lowerD(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
        
        d -= 0.01;
        this.setPID(p, i, d);
    }
    public void upD(){
        double p = this.getP();
        double d = this.getD();
        double i = this.getI();
        
        d += 0.01;
        this.setPID(p, i, d);
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}