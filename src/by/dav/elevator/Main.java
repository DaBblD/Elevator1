package by.dav.elevator;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import by.dav.elevator.models.Elevator;
import by.dav.elevator.models.Human;

public class Main extends Canvas implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5347643354173170687L;

	public static final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height - 300;
	public static final int WIDTH = (int) (HEIGHT * 0.7);
	public static final int GROUND_WIDTH = (int) (Main.WIDTH * 0.8);
	public static final int GROUND_HEIGHT = 5;
	public static final String TITLE = "Elevator Simulator";
	public static int SPEED = 10;

	public static int countFloor = 10;
	public static int countHuman = 10;
	public static int capacity = 5;

	public static Graphics g;

	private static JSlider slider;
	private static JTextArea textArea;
	private static JScrollPane scrollPane;
	private static JFrame frame;

	private static String Fps = "";

	private Thread thread;
	private boolean running;
	private boolean runMenu;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private BufferedImage background;
	private BufferedImage ground;

	private Elevator elevator;
	private List<Human> humans;
	private Cage cage;

	public static enum STATE {
		MENU, SIMULATOR
	};

	public static STATE State = STATE.MENU;

	public static void main(String[] args) {
		Main main = new Main();

		main.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		main.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		main.setMinimumSize(new Dimension(WIDTH, HEIGHT));

		slider = new JSlider(JSlider.HORIZONTAL, 0, 20, 10);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				SPEED = source.getValue();
			}
		});

		textArea = new JTextArea(5, 15);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		scrollPane = new JScrollPane(textArea);

		frame = new JFrame(Main.TITLE);
		frame.add(main, BorderLayout.CENTER);
		frame.add(slider, BorderLayout.NORTH);
		frame.add(scrollPane, BorderLayout.SOUTH);

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		main.start();
	}

	private void init() {
		requestFocus();

		BufferedImageLoader loader = new BufferedImageLoader();
		try {
			background = loader.loadBackground("/background.png");
			ground = loader.loadGround("/ground.png");
		} catch (IOException e) {
			e.printStackTrace();
		}

		humans = new ArrayList<>();
		cage = new Cage(capacity);
		elevator = new Elevator(cage);

		Random r = new Random();
		for (int i = 0; i < countHuman; i++) {
			int startFloor = r.nextInt(countFloor) + 1;
			int finishFloor = r.nextInt(countFloor) + 1;
			if (startFloor == finishFloor) {
				i--;
				continue;
			}
			humans.add(new Human(startFloor, finishFloor, cage));
			Logger.addLog(i + " Create human, start floor " + startFloor + ", finish floor " + finishFloor);
		}

		Collections.sort(humans, new Comparator<Human>() {

			@Override
			public int compare(Human h1, Human h2) {
				return h1.getStartFloor() - h2.getStartFloor();
			}
		});

		for (int i = 1; i < humans.size(); i++) {
			int shift = Human.WIDTH / 2;
			while (humans.get(i - 1).getStartFloor() == humans.get(i).getStartFloor() && i < humans.size() - 1) {
				humans.get(i).setEndPointX(humans.get(i).getEndPointX() - shift);
				shift += Human.WIDTH / 2;
				i++;
			}
		}

		new Thread(elevator).start();
		for (int i = 0; i < countHuman; i++) {
			new Thread(humans.get(i)).start();
		}
	}

	private void start() {
		if (runMenu)
			return;
		runMenu = true;

		this.addMouseListener(new MouseInput(this));

		thread = new Thread(this);
		thread.start();
	}

	private synchronized void stop() {
		if (!running)
			return;
		running = false;

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}

	@Override
	public void run() {
		if (State == STATE.MENU) {

			while (runMenu) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				render();
			}
		}
		init();
		running = true;
		long lastTime = System.nanoTime();
		final double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int updates = 0;
		int frames = 0;
		long timer = System.currentTimeMillis();

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				tick();
				updates++;
				delta--;
			}
			render();
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				Fps = updates + " Ticks, Fps " + frames;
				updates = 0;
				frames = 0;
			}
		}
	}

	public void tick() {
		if (State == STATE.MENU)
			return;
		for (int i = 0; i < humans.size(); i++) {
			if (!humans.get(i).isWork()) {
				humans.remove(i);
				i--;
			}
		}
		if (humans.size() == 0) {
			render();
			elevator.stop();
			stop();
		}
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}

		g = bs.getDrawGraphics();
		if (State == STATE.MENU && runMenu) {
			g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

			Graphics2D g2d = (Graphics2D) g;

			Font fnt0 = new Font("arial", Font.BOLD, 30);
			Font fnt1 = new Font("arial", Font.BOLD, 20);
			Font fnt2 = new Font("arial", Font.BOLD, 80);

			g.setFont(fnt0);
			g.setColor(Color.white);
			g.drawString("Elevator Simulator", 25, 25);

			g.setFont(fnt1);
			g.drawString("Start", Menu.startButton.x + 19, Menu.startButton.y + 35);
			g2d.draw(Menu.startButton);

			g.drawString("Quit", Menu.quitButton.x + 19, Menu.quitButton.y + 35);
			g2d.draw(Menu.quitButton);

			g.drawString("Count floor", Menu.incCountFloor.x + 35, Menu.incCountFloor.y - 10);
			g2d.draw(Menu.incCountFloor);
			g.setFont(fnt2);
			g.drawString("+", Menu.incCountFloor.x + 3, Menu.incCountFloor.y + 53);
			g.drawString("-", Menu.decCountFloor.x + 11, Menu.decCountFloor.y + 45);
			g.setFont(fnt1);
			g.drawString(countFloor + "", Menu.incCountFloor.x + 75, Menu.incCountFloor.y + 35);
			g2d.draw(Menu.decCountFloor);
			g.drawString("x2", Menu.x2CountFloor.x + 15, Menu.x2CountFloor.y + 35);
			g2d.draw(Menu.x2CountFloor);

			g.drawString("Count human", Menu.incCountHuman.x + 35, Menu.incCountHuman.y - 10);
			g2d.draw(Menu.incCountHuman);
			g.setFont(fnt2);
			g.drawString("+", Menu.incCountHuman.x + 3, Menu.incCountHuman.y + 53);
			g.drawString("-", Menu.decCountHuman.x + 11, Menu.decCountHuman.y + 45);
			g.setFont(fnt1);
			g.drawString(countHuman + "", Menu.incCountHuman.x + 75, Menu.incCountHuman.y + 35);
			g2d.draw(Menu.decCountHuman);
			g.drawString("x2", Menu.x2CountHuman.x + 15, Menu.x2CountHuman.y + 35);
			g2d.draw(Menu.x2CountHuman);

			g.drawString("Capasity", Menu.incCapasity.x + 35, Menu.incCapasity.y - 10);
			g2d.draw(Menu.incCapasity);
			g.setFont(fnt2);
			g.drawString("+", Menu.incCapasity.x + 3, Menu.incCapasity.y + 53);
			g.drawString("-", Menu.decCapasity.x + 11, Menu.decCapasity.y + 45);
			g.setFont(fnt1);
			g.drawString(capacity + "", Menu.incCapasity.x + 75, Menu.incCapasity.y + 35);
			g2d.draw(Menu.decCapasity);
			g.drawString("x2", Menu.x2Capasity.x + 15, Menu.x2Capasity.y + 35);
			g2d.draw(Menu.x2Capasity);

		}

		if (State == STATE.SIMULATOR && running) {
			g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
			g.drawImage(background, 0, 0, null);

			for (int i = 0; i < countFloor + 1; i++) {
				g.drawImage(ground, 0, HEIGHT / countFloor * i, null);
			}

			g.drawImage(elevator.getImage(), elevator.getX(), elevator.getY(), null);

			for (int i = 0; i < humans.size(); i++) {
				g.drawImage(humans.get(i).getImage(), humans.get(i).getX(), humans.get(i).getY(), null);
			}
		}

		Font fnt0 = new Font("arial", Font.BOLD, 10);
		g.setColor(Color.white);
		g.setFont(fnt0);
		g.drawString(Fps, 10, 10);

		g.dispose();
		bs.show();
	}

	public void closeMenu() {
		runMenu = false;
	}

	public static JTextArea getTextArea() {
		return textArea;
	}

}
