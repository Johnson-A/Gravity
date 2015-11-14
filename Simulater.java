package gravity;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Simulater implements Runnable {
	ArrayList<Body> bodies = new ArrayList<Body>(Universe.numBodies);
	ArrayList<Polygon> rects = new ArrayList<Polygon>();
	Node parent;
	int updates = 0;
	long start;
	Runnable redraw = () -> {};

	public static void main(String[] args) {
		new Simulater(null).run();
	}

	public Simulater(Display view) {
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
			"" + Runtime.getRuntime().availableProcessors());

		bodies = BodyGenerator.addBodies();
		if (view != null) {
			redraw = () -> {
				view.paint(view.getGraphics());
				//view.capture(view.offScreen);
			};
		}
	}

	@Override
	public void run() {
		while (true) {
			parent = getParent();
			bodies.parallelStream().forEach(parent::attract);
			bodies.parallelStream().forEach(Body::acc);
			analyze();
		}
	}

	private void analyze() {
		if (updates % 1 == 0) {
			redraw.run();
			System.out.format("%.2f\n", 1 / ((System.nanoTime() - start) / Math.pow(10, 9)));
			//System.out.println("" + updates * Universe.dt + " " + getTotalEnergy());
			start = System.nanoTime();
		}
		++updates;
	}

	public Node getParent() {
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

		for (Body b : bodies) {
			minX = Math.min(minX, b.xCOM);
			minY = Math.min(minY, b.yCOM);
			maxX = Math.max(maxX, b.xCOM);
			maxY = Math.max(maxY, b.yCOM);
		}

		double size = Math.max(maxX - minX, maxY - minY) / 2;
		Node parent = new Node(size, minX + size, minY + size);

		parent.addAll(bodies);

		return parent;
	}

	public double getTotalEnergy() {
		return bodies.parallelStream().collect(
			Collectors.summingDouble(b -> {
				double KE = b.mass / 2 * (Math.pow(b.xVel, 2) + Math.pow(b.yVel, 2));

				double PE = 0.0;
				for (Body n : bodies) {
					if (b != n) {
						PE += -Universe.G * n.mass * b.mass / 2
							/ (Point.distance(b.xCOM, b.yCOM, n.xCOM, n.yCOM)
							+ Math.pow(Universe.softening, 0.5));
					}
				}
				return KE + PE;
			}));
	}
}
