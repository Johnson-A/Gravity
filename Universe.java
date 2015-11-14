package gravity;

public interface Universe {
	double G = 6.67384E-11;
	double AU = 1.4960E11;
	double solarMass = 1.9891E30;
	double earthMass = 5.97219E24;
	double jupiterMass = 1.89813E27;
	double astBeltMass = 3.0E21;

	double dt = 1.0E8 * 10;
	double bound = 2 * AU;
	double accConst = 0.3; // accConst = 4.0 is beautiful
	double softening = Math.pow(bound, 2) / 1000000;
	int numBodies = 100_000;
	
	int screenSize = 800;
	int largestSize = 10;
	int smallestSize = 1;

	double defMass = astBeltMass / numBodies;
}