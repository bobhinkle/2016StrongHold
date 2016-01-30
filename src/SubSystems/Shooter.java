package SubSystems;

import java.util.Timer;
import java.util.TimerTask;

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

    private static final double MINRPM = 0;
    private static final double MAXRPM = 6000; 
    private static final int K_READING_RATE = 200;
    private static final double PID_PROP = 0.002; //0.008   0.09
    private static final double PID_INT  = 0.0;
    private static final double PID_DIFF = 0.001; //0.011  0.07
    private static final double PID_F    = 1.0/MAXRPM;
    private static final double MAXFORWARDPOWER = 1;
    private static final double MAXREVERSEPOWER = 0;
    private static final double OK_ERROR = 50; //TODO Calibrate
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
    public void shooterUpSpeed(){
    	double current = this.getSetpoint();
    	this.setSetpoint(current + 10.00);
    	goal = this.getSetpoint();
    }
    public void shooterDownSpeed(){
    	double current = this.getSetpoint();
    	goal = this.getSetpoint();
    	this.setSetpoint(current - 10.00);
    }
    public Shooter(){
    	this.setInputRange(MINRPM, MAXRPM);
        this.setOutputRange(MAXREVERSEPOWER, MAXFORWARDPOWER);
        loadProperties();
    }
    
    private class InitTask extends TimerTask {
        @Override
        public void run() {
            while (true) {
                try {
                	SmartDashboard.putString("Status", "Shooter Started");
                	enc = new SuperEncoder(Ports.SHOOTER_ENC,Ports.SHOOTER_ENC + 1,false,SuperEncoder.HIGH_RESOLUTION);
                    enc.start();
                    motor = new Victor(Ports.SHOOTER_MOTOR);                    
                    goal = 0;
                    SmartDashboard.putString("Status", "Shooter Done");
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
                SmartDashboard.putString("Status", "Task Started");
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
    		enc.update();
        	double current = enc.getRPM();            	
            double power = calculatePID(current);
            buffer = Util.buffer(power, buffer, 5);
            if(goal == 0){
            	power = 0;
                buffer = 0;
            }else{
                
                if(goal >= 6500){
                	power = 1.0;
                }
            } 
            SmartDashboard.putNumber("ShootSpeed", current);
            SmartDashboard.putNumber("ShooterGoal", goal);
            SmartDashboard.putNumber("ShooterPower", buffer);
            setPower(power);
            
        }
    }
    /** Used to interface with a PID controller
     *
     * @param output value for the PID controller to set the motors to
     */
    private double speed = 0;
    
    public synchronized void set(double speed){ motor.set(speed); }
    public synchronized void goTo(double _goal){ goal = Util.limit(_goal, MINRPM, MAXRPM); this.setSetpoint(goal); } 
    public synchronized void stop(){ goal = 0; setPower(0);}
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
	private void postPID(){
	//	SmartDashboard.putString("shooterPID", "P " + pidc.getP() + " D " + pidc.getD() + " " + goal );
	}
	@Override
	public boolean onTarget() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void loadProperties() {
		// TODO Auto-generated method stub
		this.setPID(PID_PROP, PID_INT, PID_DIFF,PID_F);
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