package by.dav.elevator;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseInput implements MouseListener {

	private Main main;

	public MouseInput(Main main) {
		this.main = main;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (Main.State == Main.STATE.SIMULATOR)
			return;
		int mX = e.getX();
		int mY = e.getY();

		// Start button
		if (Menu.startButton.contains(new Point(mX, mY))) {
			System.out.println("startButton");
			// Pressed Start Button
			Main.State = Main.STATE.SIMULATOR;
			main.closeMenu();
		}

		// Quit button
		if (Menu.quitButton.contains(new Point(mX, mY))) {
			System.exit(1);
		}

		// Increment count floor button
		if (Menu.incCountFloor.contains(new Point(mX, mY))) {
			Main.countFloor++;
			if (Main.countFloor > 100)
				Main.countFloor = 100;
		}
		// Decrement count floor button
		if (Menu.decCountFloor.contains(new Point(mX, mY))) {
			Main.countFloor--;
			if (Main.countFloor < 2)
				Main.countFloor = 2;
		}
		// x2 count floor button
		if (Menu.x2CountFloor.contains(new Point(mX, mY))) {
			Main.countFloor *= 2;
			if (Main.countFloor > 100)
				Main.countFloor = 100;
		}

		// Increment count human button
		if (Menu.incCountHuman.contains(new Point(mX, mY))) {
			Main.countHuman++;
		}
		// Decrement count human button
		if (Menu.decCountHuman.contains(new Point(mX, mY))) {
			Main.countHuman--;
			if (Main.countHuman < 1)
				Main.countHuman = 1;
		}
		// x2 count human button
		if (Menu.x2CountHuman.contains(new Point(mX, mY))) {
			Main.countHuman *= 2;
		}

		// Increment capacity button
		if (Menu.incCapasity.contains(new Point(mX, mY))) {
			Main.capacity++;
		}
		// Decrement capacity button
		if (Menu.decCapasity.contains(new Point(mX, mY))) {
			Main.capacity--;
			if (Main.capacity < 1)
				Main.capacity = 1;
		}
		// x2 capacity button
		if (Menu.x2Capasity.contains(new Point(mX, mY))) {
			Main.capacity *= 2;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
