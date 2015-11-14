package gravity;

import java.awt.Polygon;
import java.util.Collection;

public abstract class Region {
   double xCOM, yCOM, mass, size;

   public Region(double xCOM, double yCOM, double mass, double size) {
      this.xCOM = xCOM;
      this.yCOM = yCOM;
      this.mass = mass;
      this.size = size;
   }
   
   public abstract boolean isLeaf();

   public abstract void add(Region r);

   public abstract void aggregate();

   public abstract Collection<Polygon> getRects();

   public abstract void attract(Body b);
}
