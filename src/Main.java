import java.util.Arrays;
import java.util.Iterator;

import jsmt.core.Constraint;
import static jsmt.core.Constraints.*;

public class Main {

	public static class Point {
		private final int x;
		private final int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "{x=" + x + ",y=" + y + "}";
		}

		public static Point project(int[] vars) {
			return new Point(vars[0],vars[1]);
		}
	}


	public static void main(String[] args) {
		Constraint.Set<Point> constraints = new Constraint.Set<>(Point::project);
//		Constraint.Variable x = constraints.add(U8);
//		Constraint.Variable y = constraints.add(and(U8, atleast(x)));
		Constraint.Variable x = constraints.add(between(0,5));
		Constraint.Variable y = constraints.add(between(0,5));
		//
		for(Point p : constraints) {
			System.out.println(p);
		}
	}
}
