package me.Aubli;

public class LiftFloor {

	private Lift lift;
	private int floor;
	private int Y;
	
	public LiftFloor(Lift lift, int Y, int floor){
		this.floor = floor;
		this.lift = lift;
		this.Y = Y;
	}
	
	public LiftFloor(int Y, int floor){
		this.floor = floor;		
		this.Y = Y;
	}
	
	public int getFloor(){
		return floor;
	}
	
	public int getY(){
		return Y;
	}
	
	public Lift getLift(){
		return lift;
	}
	
	public void setLift(final Lift lift){
		this.lift = lift;
	}
}
