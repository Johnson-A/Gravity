package gravity;

import java.util.ArrayList;
import java.util.Random;

public class BodyGenerator implements Universe {
	static Random r = new Random(0);

	public static ArrayList<Body> collision() {
		ArrayList<Body> bodies = new ArrayList<Body>();
		for (int i = 0; i <= numBodies / 2; i++) {
			bodies.add(new Body((0.4 + r.nextDouble() * 0.6) * AU,
				(0.5 + r.nextDouble() * 0.6) * AU, defMass, 0, 0));
			bodies.add(new Body((1.4 + r.nextDouble() * 0.6) * AU,
				(0.7 + r.nextDouble() * 0.6) * AU, defMass, -4, 0));
		}
		return bodies;
	}

	public static ArrayList<Body> asteroids() {
		ArrayList<Body> bodies = new ArrayList<Body>();
		double centerMass = solarMass;
		double rMin = 0.3 * AU;
		double rMax = 0.6 * AU;
		double angle = 0;
		bodies.add(new Body(AU, AU, centerMass, 0, 0));
		bodies.add(new Body(AU * 2 / 3, AU, centerMass / 100,
			0, -Math.sqrt(G * centerMass / (AU / 3))));
		for (int i = 2; i < numBodies; i++) {
			double radius = rMin + r.nextDouble() * (rMax - rMin);
			angle += Math.PI * 2 / numBodies;
			double vel = Math.sqrt(G * centerMass / radius) * (0.8 + 0.4 * r.nextDouble()) * 4 / 5;

			bodies.add(new Body(Math.cos(angle) * radius + AU, Math.sin(angle) * radius + AU,
				defMass, -Math.sin(angle) * vel, Math.cos(angle) * vel));
		}
		return bodies;
	}

	public static ArrayList<Body> addBodies() {
		ArrayList<Body> bodies = new ArrayList<Body>();
		for (int i = 0; i < numBodies; i++) {
			bodies.add(new Body(r.nextDouble() * bound, r.nextDouble() * bound, defMass, 0.0, 0.0));
		}
		return bodies;
	}
}
