import java.util.Arrays;

import jsmt.core.Constraint;
import jsmt.core.LinearInequality;

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
		// x <= y
		LinearInequality ieq = new LinearInequality(true, new int[] { 0, 1 }, new int[] { -1, 1 });
		//
		Constraint.Set<Point> set = new Constraint.Set<>(Point::project, 2, ieq);
		//
		while(set.hasNext()) {
			System.out.println("SAT: " + set.next());
		}
		//
//		System.out.println("POLY: " + new Polynomial(new Polynomial.Term(1, 0), new Polynomial.Term(2, 1))
//				.toString(new String[] { "x", "y" }));
	}
}
