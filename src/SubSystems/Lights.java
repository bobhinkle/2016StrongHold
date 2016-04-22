package SubSystems;

import Utilities.Ports;
import edu.wpi.first.wpilibj.DigitalOutput;

public class Lights {

	private static Lights instance = null;
	private DigitalOutput STATE0;
	private DigitalOutput STATE1;
	private DigitalOutput STATE2;
	
	public static enum MODE{
		ON_TARGET,BALL_INTAKE,WAITING,OFF_TARGET,GYRO_INIT,GYRO_READY
	}
	public Lights(){
		STATE0 = new DigitalOutput(Ports.LIGHTS_0);
		STATE1 = new DigitalOutput(Ports.LIGHTS_1);
		STATE2 = new DigitalOutput(Ports.LIGHTS_2);
		setState(MODE.WAITING);
	}
	public static Lights getInstance(){
		if(instance == null)
			instance = new Lights();
		return instance;
	}
	
	public void setState(MODE mode){
		switch(mode){
		case OFF_TARGET:
			STATE0.set(false);
			STATE1.set(false);
			STATE2.set(false);
			break;
		case ON_TARGET:
			STATE0.set(true);
			STATE1.set(false);
			STATE2.set(false);
			break;
		case WAITING:
			STATE0.set(false);
			STATE1.set(true);
			STATE2.set(false);
			break;
		case BALL_INTAKE:
			STATE0.set(true);
			STATE1.set(true);
			STATE2.set(false);
			break;
		case GYRO_INIT:
			STATE0.set(false);
			STATE1.set(false);
			STATE2.set(true);
			break;
		case GYRO_READY:
			STATE0.set(true);
			STATE1.set(false);
			STATE2.set(true);
			break;
		}
	}
}
