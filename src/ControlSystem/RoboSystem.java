
package ControlSystem;

import IO.Controller;
import IO.Logger;
import IO.TeleController;
import SubSystems.DistanceController;
import SubSystems.DriveTrain;
import SubSystems.Elevator;
import SubSystems.Intake;
import SubSystems.Lights;
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
    public DistanceController dist;
    private static RoboSystem instance = null;
    public Logger logFile;
    public turnThread turnTh;
    public TurnController turn; 
    public boolean turnRunning = false;
    private TeleController controller;
    public Lights lights;
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
    	logFile = Logger.getInstance();
    	lights = Lights.getInstance();
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
    public void turnToHeading(double heading, double timeout,boolean hold){
        nav.resetRobotPosition(0, 0, 0,false);
        if(turn == null){
            turn = TurnController.getInstance();
        }
        turn.reset();
        turn.setGoal(heading,timeout,hold);
        turnTh = new turnThread();
        turnTh.start();
        System.out.println("TurnHold");
    }
    public class turnThread extends Thread{
        private boolean keepRunning = true;
        public void run(){
            try {
            	turnRunning = true;
                while(!turn.onTarget() && keepRunning){
                    turn.run();
                    Timer.delay(0.01);
                }
                System.out.println("done");
                dt.directDrive(0, 0);
                turnRunning = false;
            }catch(Exception e){
                System.out.println("crash" + e.toString());
            }            
        }
        public void kill(){
        	keepRunning = false;
        	turnRunning = false;
        }
    }
    
    public void visionFiring(){
    	double visionError = 0.0;
    	if(shooter.shot() == Shooter.Shot.CLOSE){
    		visionError = 1.25;
    	}else{
    		visionError = 0.8;
    	}        	
    	if(elevator.status() == Elevator.Direction.UP){    		
    		if(vision.isTargetSeen()){ 
    			if(Math.abs(Vision.getAngle()) <= visionError){
    				turret.setState(Turret.State.OFF);
    				if(shooter.onTarget() && turret.onTarget() && !shooter.firing()){            			
//            			robot.logFile.writeToLog(System.currentTimeMillis() + " LT HELD FIRE1- TARGET SEEN:" + robot.vision.isTargetSeen() + " VISION ANGLE:" + Vision.getAngle() + " SHOOTER SPEED :" + robot.shooter.onTarget() + " T_ANGLE:" + robot.turret.getAngle());
            			logFile.writeToLog("AUTO FIRE");
            			turret.stop();
            			shooter.fire();
            			if(turnTh != null)
                    		turnTh.kill();
            			if(controller == null){
            				controller = TeleController.getInstance();
            			}
            			controller.wheel.rumble(Controller.Vibration.DOUBLE);
        			}
    			}else{
        			turret.setState(Turret.State.SINGLE);
        			System.out.println("RESCAN");
        		}    			
    		}else{
    			System.out.println("NOTHING");
    			turret.setState(Turret.State.TRACKING);
//    			robot.logFile.writeToLog(System.currentTimeMillis() + " LT HELD NO FIRE- TARGET SEEN:" + robot.vision.isTargetSeen() + " VISION ANGLE:" + Vision.getAngle() + " SHOOTER SPEED :" + robot.shooter.onTarget() + " T_ANGLE:" + robot.turret.getAngle());
    		}
    		
    	}
    }
}
