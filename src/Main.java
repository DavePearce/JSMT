import java.util.Arrays;
import java.util.Iterator;

import jsmt.core.Constraint;
import jsmt.core.Variable;

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
		Constraint.Set<String> constraints = new Constraint.Set<>(Arrays::toString);
//		Constraint.Variable x = constraints.add(U8);
//		Constraint.Variable y = constraints.add(and(U8, atleast(x)));
		Variable x = constraints.declare(between(0, 5));
		Variable y = constraints.declare(and(between(0, 5)));
		Variable z = constraints.declare(and(between(0, 5), equal(x.add(y))));
		//
		for(String s : constraints) {
			System.out.println(s);
		}
	}
}
