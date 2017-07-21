package by.dav.elevator;

import java.util.concurrent.CopyOnWriteArrayList;

import by.dav.elevator.models.Human;

public class Cage {

	private int capacity;

	private boolean wait = false;
	private int waitPassenger = 0;

	private int currentFloor = 0;
	private CopyOnWriteArrayList<Human> passengers = new CopyOnWriteArrayList<Human>();

	public Cage(int capacity) {
		this.capacity = capacity;
	}

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void nextFloor(int value) {
		this.currentFloor += value;
	}

	public boolean isFreePlace() {
		if (passengers.size() < capacity)
			return true;
		return false;
	}

	public void addPassenger(Human human) {
		if (wait && isFreePlace()) {
			passengers.add(human);
			Logger.addLog("The elevator have " + passengers.size() + " people.");
		}
	}

	public void waitPasseger() {
		waitPassenger++;
	}

	public void goElevator() {
		waitPassenger--;
	}

	public void removePassenger(Human human) {
		passengers.remove(human);
		Logger.addLog("The elevator have " + passengers.size() + " people.");
	}

	public CopyOnWriteArrayList<Human> getPassengers() {
		return passengers;
	}

	public void setWait(boolean wait) {
		this.wait = wait;
	}

	public boolean isWait() {
		return wait;
	}

	public boolean isWaitPassenger() {
		if (waitPassenger == 0)
			return false;
		return true;
	}

	public int getWaitPassenger() {
		return waitPassenger;
	}

}
