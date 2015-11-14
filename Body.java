package gravity;

import java.awt.Polygon;
import java.util.Collection;

public class Body extends Region {
	double xVel, yVel;
	double accX, accY;

	public Body(double xCOM, double yCOM, double mass, double xV, double yV) {
		super(xCOM, yCOM, mass, 0);
		xVel = xV;
		yVel = yV;
	}

	public void acc() {
		xCOM += (xVel + accX * Universe.dt / 2) * Universe.dt;
		yCOM += (yVel + accY * Universe.dt / 2) * Universe.dt;
		xVel += accX * Universe.dt;
		yVel += accY * Universe.dt;
		accX = 0;
		accY = 0;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}
	
	@Override
	public String toString() {
		return "Mass = " + mass + " x = " + xCOM + " y = " + yCOM;
	}

	@Override
	public void add(Region r) {}

	@Override
	public void aggregate() {}

	@Override
	public Collection<Polygon> getRects() {
		return null;
	}

	@Override
	public void attract(Body b) {}
}
