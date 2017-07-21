package by.dav.elevator;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import by.dav.elevator.models.Elevator;
import by.dav.elevator.models.Human;

public class BufferedImageLoader {
	private BufferedImage loadImage(String path) throws IOException {
		BufferedImage image = ImageIO.read(getClass().getResource(path));
		return image;
	}

	public BufferedImage loadBackground(String path) throws IOException {
		BufferedImage image = loadImage(path);
		image = resizeImage(image, Main.WIDTH, Main.HEIGHT);
		return image;
	}

	public BufferedImage loadGround(String path) throws IOException {
		BufferedImage image = loadImage(path);
		image = resizeImage(image, Main.GROUND_WIDTH, Main.GROUND_HEIGHT);
		return image;
	}

	public BufferedImage loadElevator(String path) throws IOException {
		BufferedImage image = loadImage(path);
		image = resizeImage(image, Elevator.WIDTH, Elevator.HEIGHT);
		return image;
	}

	public BufferedImage[] loadHuman(String path) throws IOException {
		BufferedImage image = loadImage(path);
		BufferedImage[] images = new BufferedImage[12];
		for (int i = 0; i < images.length; i++) {
			images[i] = image.getSubimage((image.getWidth() / images.length * i) - i, 24,
					image.getWidth() / images.length, image.getHeight() - 24);
			images[i] = resizeImage(images[i], Human.WIDTH, Human.HEIGHT);
		}
		return images;
	}

	public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		try {
			g.drawImage(image, 0, 0, width, height, null);
			g.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resizedImage;
	}
}
