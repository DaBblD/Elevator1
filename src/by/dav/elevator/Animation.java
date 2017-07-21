package by.dav.elevator;

import java.awt.image.BufferedImage;

public class Animation {
	private int speed;
	private int frames;
	private int index = 0;
	private int count = 0;

	private BufferedImage[] img;

	private BufferedImage currentImg;

	public Animation(int speed, BufferedImage[] img) {
		this.speed = speed;
		this.img = img;
		frames = img.length;
	}

	public void runAnimation() {
		index++;
		if (index > speed) {
			index = 0;
			nextFrame();

		}
	}

	public void nextFrame() {

		if (count < frames) {
			currentImg = img[count];
			count++;
		} else {
			count = 0;
		}
	}

	public BufferedImage getImage() {
		return currentImg;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
