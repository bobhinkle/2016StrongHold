package ControlSystem;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FSM {

	public enum State{
    	DEFAULT, INIT, PRE_TOTE,
    	
    }
	private RoboSystem robot;
	private static FSM instance = null;
	public partsUpdate pu;
	private State currentState = State.INIT;
    private State goalState = State.DEFAULT;
    private State prevState = State.DEFAULT;
	public static FSM getInstance()
    {
        if( instance == null )
            instance = new FSM();
        return instance;
    }
        
    public FSM() {
    	SmartDashboard.putString("FSM", "STARTED");
        robot = RoboSystem.getInstance();
        pu = new partsUpdate();
    	pu.start();
    }
    
    public void setGoalState(State goal) {
        if(currentState == goal){
            currentState = State.DEFAULT;
            goalState = goal;
        }else{
            goalState = goal;
        }
    }
    
    public State getCurrentState() {
        return currentState;
    }
    private void stateComplete(State state){
        prevState = state;
    }
    public State previousState(){
    	return prevState;
    }
    public class partsUpdate extends Thread{
        private boolean keepRunning = true;
    	public void run(){
    		SmartDashboard.putString("FSM", "THREAD STARTED");
    		while(keepRunning){
				update();
				robot.shooter.run();
				Timer.delay(0.01); 
    		}
        }
        public void kill(){
        	keepRunning = false;
        }
    }
    
    public void nextState(){
    	switch(previousState()){
    	
		
		default:
			setGoalState(FSM.State.PRE_TOTE);
			break;
		}
    }
    public void update(){ 
        switch(goalState){
            case INIT:
                SmartDashboard.putString("FSM_STATE", "INIT");
                stateComplete(State.INIT);
                break;
            
            case DEFAULT:
            	SmartDashboard.putString("FSM_STATE", "WAITING");
            	break;
		case PRE_TOTE:
			break;
		default:
			break;
        }
    }
}