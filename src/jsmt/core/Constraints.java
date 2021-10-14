package jsmt.core;

import jsmt.constraints.Conjunction;
import jsmt.constraints.LinearLowerBound;
import jsmt.constraints.StaticRange;

public class Constraints {

	public static Constraint and(Constraint... clauses) {
		return new Conjunction(clauses);
	}

	public static Constraint atleast(Constraint.Variable var) {
		return new LinearLowerBound(new int[] { var.getIndex() }, new int[] { 1 });
	}

	public static Constraint between(int lb, int ub) {
		return new StaticRange(lb,ub);
	}
}
