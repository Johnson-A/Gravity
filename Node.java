package gravity;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Node extends Region {
	double xCenter, yCenter;
	Region[] subRegions = new Region[4];

	public Node(double xCOM, double yCOM, double mass, double size, double xCen, double yCen) {
		super(xCOM, yCOM, mass, size);
		xCenter = xCen;
		yCenter = yCen;
	}

	public Node(double size, double xCen, double yCen) {
		super(0, 0, 0, size);
		xCenter = xCen;
		yCenter = yCen;
	}

	public Node(Node region, int pos) {
		super(0, 0, 0, region.size / 2);

		switch (pos) {
		case 0:
			xCenter = region.xCenter - size;
			yCenter = region.yCenter - size;
			break;
		case 1:
			xCenter = region.xCenter + size;
			yCenter = region.yCenter - size;
			break;
		case 2:
			xCenter = region.xCenter - size;
			yCenter = region.yCenter + size;
			break;
		case 3:
			xCenter = region.xCenter + size;
			yCenter = region.yCenter + size;
			break;
		}
	}

	public void add(Region body) {
		int pos = (body.xCOM > xCenter ? 1 : 0) + (body.yCOM > yCenter ? 2 : 0);
		Region branch = subRegions[pos];

		if (branch == null) {
			subRegions[pos] = body;
			return;
		}

		if (branch.isLeaf()) { // branch is a body
			Node newRegion = new Node(this, pos);
			newRegion.add(branch);
			subRegions[pos] = newRegion;
		}
		subRegions[pos].add(body);
	}

	public void addAll(ArrayList<Body> bodies) {
		IntStream.range(0, 4).parallel().forEach((int pos) -> {
			subRegions[pos] = new Node(this, pos);

			bodies.stream().forEach((b) -> {
				if ((b.xCOM > xCenter ? 1 : 0) + (b.yCOM > yCenter ? 2 : 0) == pos) {
					subRegions[pos].add(b);
				}
			});

			// Aggregate all of the children of the parent in
			// different threads.
			// No body should gravitationally interact with the
			// parent node
			// if Universe.accConst < 2
			subRegions[pos].aggregate();
		});
	}

	public void aggregate() {
		for (int i = 0; i < 4; i++) {
			Region branch = subRegions[i];
			if (branch != null) {
				if (!branch.isLeaf()) {
					branch.aggregate();
				}
				xCOM += branch.xCOM * branch.mass;
				yCOM += branch.yCOM * branch.mass;
				mass += branch.mass;
			}
		}
		xCOM /= mass;
		yCOM /= mass;
	}

	public ArrayList<Polygon> getRects() {
		double shift = Universe.screenSize / Universe.bound;
		int[] xpoints = {
				(int) ((xCenter - size) * shift),
				(int) ((xCenter + size) * shift),
				(int) ((xCenter + size) * shift),
				(int) ((xCenter - size) * shift) };
		int[] ypoints = {
				(int) ((yCenter - size) * shift),
				(int) ((yCenter - size) * shift),
				(int) ((yCenter + size) * shift),
				(int) ((yCenter + size) * shift) };

		ArrayList<Polygon> rects = new ArrayList<Polygon>();
		rects.add(new Polygon(xpoints, ypoints, 4));

		for (int i = 0; i < 4; i++) {
			Region branch = subRegions[i];
			if (branch != null) {
				if (!branch.isLeaf()) {
					rects.addAll(branch.getRects());
				}
			}
		}
		return rects;
	}

	public void attract(Body b) {
		for (int i = 0; i < 4; ++i) {
			Region branch = subRegions[i];
			if (branch != null && branch != b) {
				double dx = branch.xCOM - b.xCOM;
				double dy = branch.yCOM - b.yCOM;
				double invDistSq = 1.0 / (dx * dx + dy * dy + Universe.softening);
				double invDist = Math.sqrt(invDistSq);

				if (branch.size * invDist < Universe.accConst) {
					double acc = Universe.G * branch.mass * invDistSq * invDist;
					b.accX += acc * dx;
					b.accY += acc * dy;
				} else {
					branch.attract(b);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Region = " + mass + " x = " + xCenter + " y = " + yCenter + " size = " + size;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	public List<Node> getColors(int depth) {
		if (depth == 8) {
			return Arrays.asList(this);
		}
		List<Node> reg = new ArrayList<Node>();
		for (int i = 0; i < 4; i++) {
			Region branch = subRegions[i];
			if (branch != null) {
				if (!branch.isLeaf()) {
					reg.addAll(((Node) branch).getColors(depth + 1));
				}
			}
		}
		return reg;
	}
}
