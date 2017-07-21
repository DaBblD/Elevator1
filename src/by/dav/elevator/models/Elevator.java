package by.dav.elevator.models;

import java.awt.image.BufferedImage;
import java.io.IOException;

import by.dav.elevator.BufferedImageLoader;
import by.dav.elevator.Cage;
import by.dav.elevator.Logger;
import by.dav.elevator.Main;

public class Elevator implements Runnable {
	public static final int WIDTH = (int) (Main.WIDTH * 0.2);
	public static final int HEIGHT = (int) Main.HEIGHT / Main.countFloor;

	private int x = Main.GROUND_WIDTH;
	private int y = Main.HEIGHT - ((int) Main.HEIGHT / Main.countFloor);

	private boolean work = false;
	private boolean down = false;

	private BufferedImage image;
	private Cage cage;

	private long pauseTime = 0;

	public Elevator(Cage cage) {
		init();
		work = true;
		this.cage = cage;
	}

	private void init() {
		BufferedImageLoader loader = new BufferedImageLoader();
		try {
			image = loader.loadElevator("/elevator.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void stop() {
		if (!work)
			return;
		work = false;
	}

	@Override
	public void run() {

		while (work) {
			try {
				Thread.sleep(Main.SPEED);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tick();
		}
	}

	private void tick() {
		synchronized (cage) {
			if (cage.isWaitPassenger())
				return;

			// Stop on the floor
			if (System.currentTimeMillis() - pauseTime < 10 + (120 * Main.SPEED)) {
				return;
			}
			cage.setWait(false);
			for (int i = 0; i < Main.countFloor; i++) {
				if (y == (int) (Main.HEIGHT / Main.countFloor) * i) {
					pauseTime = System.currentTimeMillis();
					cage.setWait(true);
					if (down)
						cage.nextFloor(-1);
					else
						cage.nextFloor(1);
					Logger.addLog("The elevator is in " + cage.getCurrentFloor() + " floor.");
				}
			}

			// Moving passengers in the elevator
			if (down && y < Main.HEIGHT - ((int) Main.HEIGHT / Main.countFloor)) {
				y += 1;
				for (Human h : cage.getPassengers()) {
					h.doStep(1);
				}
			}
			if (!down && y > 0) {
				y -= 1;
				for (Human h : cage.getPassengers()) {
					h.doStep(-1);
				}
			}

			// Changing the direction of movement of the elevator
			if (y >= Main.HEIGHT - ((int) Main.HEIGHT / Main.countFloor)) {
				down = false;
				cage.nextFloor(-2);
			}
			if (y <= 0) {
				down = true;
				cage.nextFloor(2);
			}
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public BufferedImage getImage() {
		return image;
	}

}
