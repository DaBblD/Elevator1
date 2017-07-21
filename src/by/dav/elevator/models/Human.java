package by.dav.elevator.models;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import by.dav.elevator.Animation;
import by.dav.elevator.BufferedImageLoader;
import by.dav.elevator.Cage;
import by.dav.elevator.Main;

public class Human implements Runnable {

	public static final int WIDTH = (int) ((Main.HEIGHT / Main.countFloor + 5) * 0.2);
	public static final int HEIGHT = (int) ((Main.HEIGHT / Main.countFloor + 5) * 0.6);

	private int x;
	private int y;

	private int endPointX;

	private int startFloor;
	private int finishFloor;

	private boolean work = false;

	private boolean waitElevator = false;
	private boolean waitFloor = false;
	private boolean toTheElevator = false;
	private boolean inTheElevator = false;
	private boolean toTheExit = false;

	private BufferedImage image;
	private Cage cage;
	private Animation animIn;
	private Animation animOut;

	public Human(int startFloor, int finishFloor, Cage cage) {
		this.startFloor = startFloor;
		this.finishFloor = finishFloor;
		this.cage = cage;

		image = new BufferedImage(1, 1, 1);

		BufferedImageLoader loader = new BufferedImageLoader();

		try {
			animIn = new Animation(10, loader.loadHuman("/humangoin.png"));
			animOut = new Animation(10, loader.loadHuman("/humangoout.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		x = 0;
		y = Main.HEIGHT - ((int) (Main.HEIGHT / Main.countFloor) * (startFloor - 1)) - Human.HEIGHT;

		endPointX = Main.GROUND_WIDTH - Human.WIDTH;

		toTheElevator = true;

		work = true;

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
		animIn.setSpeed(Main.SPEED / 2);
		animIn.runAnimation();
		animOut.setSpeed(Main.SPEED / 2);
		animOut.runAnimation();
		if (x != endPointX) {
			x += Math.signum(endPointX - x);
		}
		if (toTheElevator && x == endPointX) {
			toTheElevator = false;
			waitElevator = true;
		}
		synchronized (cage) {
			if (waitElevator && startFloor == cage.getCurrentFloor() && cage.isFreePlace() && cage.isWait()) {
				Random r = new Random();
				endPointX = r.nextInt(Elevator.WIDTH - Human.WIDTH) + Main.GROUND_WIDTH;
				waitElevator = false;
				inTheElevator = true;
				cage.waitPasseger();
				cage.addPassenger(this);
			}

			if (inTheElevator && x == endPointX && !waitFloor && !toTheExit) {
				waitFloor = true;
				cage.goElevator();
			}

			if (waitFloor && finishFloor == cage.getCurrentFloor()) {
				cage.removePassenger(this);
				waitFloor = false;
				toTheExit = true;
				endPointX = 0;
			}
		}
		if (toTheExit && x == endPointX)
			work = false;

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getStartFloor() {
		return startFloor;
	}

	public int getfinishFloor() {
		return finishFloor;
	}

	public void doStep(int step) {
		y += step;
	}

	public BufferedImage getImage() {
		if (!waitElevator && !waitFloor) {
			if (toTheElevator || inTheElevator)
				image = animIn.getImage();
			if (toTheExit)
				image = animOut.getImage();
		}
		try {
			Graphics2D g = image.createGraphics();
			g.setColor(Color.white);
			g.drawString("" + finishFloor, WIDTH / 2, HEIGHT / 2);
			g.dispose();
		} catch (NullPointerException ex) {
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return image;
	}

	public int getEndPointX() {
		return endPointX;
	}

	public void setEndPointX(int endPointX) {
		this.endPointX = endPointX;
	}

	public boolean isWork() {
		return work;
	}

	public boolean isWaitElevator() {
		return waitElevator;
	}
}
