
package ControlSystem;

import SubSystems.DistanceController;
import SubSystems.DriveTrain;
import SubSystems.Elevator;
import SubSystems.Intake;
import SubSystems.Navigation;
import SubSystems.Shooter;
import SubSystems.TestTalon;
import SubSystems.TurnController;
import SubSystems.Turret;
import SubSystems.Vision;
import Utilities.Constants;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Timer;


public class RoboSystem{
	public DriveTrain dt;	
	public CANTalon hangerDrive;
	public Shooter shooter;
	public Intake intake;
	public Turret turret;
	public Elevator elevator;
	public TestTalon testTalon;
	public Navigation nav;
	public Vision vision;
	private  distanceThread distTh;
    public TurnController turn; 
    public DistanceController dist;
    private static RoboSystem instance = null;
    
    
    public static RoboSystem getInstance()
    {
        if( instance == null )
            instance = new RoboSystem();
        return instance;
    }
    
    public RoboSystem(){
    	dt = DriveTrain.getInstance();
    	intake = Intake.getInstance();
    	shooter = Shooter.getInstance();
    	turret = Turret.getInstance();
    	elevator = Elevator.getInstance();
    	nav = Navigation.getInstance();
    	vision = Vision.getInstance();
    }
    public void Init(){
    	intake.setAngle(Constants.INTAKE_OFFSET-intake.getAngle());
    	turret.set(turret.getAngle());
    	shooter.stop();
    }
    
    public double driveDistanceHoldingHeading(double distance, double heading,double maxSpeed,double timeout,double tol, boolean holdSpeed,double breakTime){                
        dist.resetDistance();
        double startDistance = 0;
        double distanceChange = distance + startDistance;
        System.out.println("DD: " + startDistance + " " + distanceChange + " " + distance);
        dist.reset();
        dist.setGoal(distanceChange, maxSpeed,heading,timeout,tol,holdSpeed);
        distTh.run();
        return nav.getDistance() - startDistance;
    }
    private class distanceThread extends Thread{
        private boolean keeprunning = false;
        public void run(){
            if(!keeprunning){
                while(!dist.onTarget()){
                    dist.run();
                    Timer.delay(0.01);
                }
            }else{
               
            }
            System.out.println("done");
            dt.directDrive(0, 0);                       
        }
    }
}
