package gravity;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.DoubleStream;

import javax.imageio.ImageIO;

public class Display extends Applet implements Universe {
	private static final long serialVersionUID = 3102647806958221808L;
	BufferedImage offScreen;
	Graphics2D bufferGraphics;
	Simulater sim;

	@Override
	public void init() {
		super.init();

		sim = new Simulater(this);

		offScreen = new BufferedImage(screenSize, screenSize, BufferedImage.TYPE_INT_RGB);
		offScreen.setAccelerationPriority(1.0f);
		bufferGraphics = offScreen.createGraphics();

		setSize(screenSize + 1, screenSize + 1);
		start();

		new Thread(sim).start();
	}

	@Override
	public void paint(Graphics g) {
		bufferGraphics.clearRect(0, 0, screenSize, screenSize);
		bufferGraphics.setColor(Color.WHITE);
		double scale = screenSize / bound;

//		for (Body b : sim.bodies) {
//			int drawSize = (int) (smallestSize + Math.min(largestSize, b.mass
//				/ (defMass * numBodies) * largestSize));
//
//			bufferGraphics.drawOval((int) (b.xCOM * scale),
//				(int) (b.yCOM * scale), drawSize, drawSize);
//		}

		List<Node> reg = sim.parent.getColors(0);
		DoubleStream max = reg.parallelStream().mapToDouble((r) -> r.mass / (r.size * r.size)).sorted();
		double[] vals = max.toArray();
		double maxVal = 0;
		for (int i = 1; i <= vals.length / 10000; i++) {
			maxVal += vals[vals.length - i];
		}
		maxVal /= vals.length / 10000 * 3;
		for (Node r : reg) {
			int greenVal = Math.min(255, (int) (255.0 * (r.mass / (r.size * r.size) / maxVal)));
			bufferGraphics.setColor(new Color(0, greenVal, 0));
			bufferGraphics.fillRect((int) ((r.xCenter - r.size) * scale), (int) ((r.yCenter - r.size) * scale),
					Math.max(1, (int) (r.size * 2 * scale)), Math.max(1, (int) (r.size * 2 * scale)));
		}

		bufferGraphics.setColor(Color.BLUE);
		for (Polygon r : sim.rects) {
			bufferGraphics.drawPolygon(r);
		}
		g.drawImage(offScreen, 0, 0, this);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public void capture(BufferedImage copiedImage) {
		try {
			File outputfile = new File("C:/Users/alex/Desktop/test/frame"
				+ String.format("%05d", sim.updates) + ".jpg");
			ImageIO.write(copiedImage, "jpg", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
