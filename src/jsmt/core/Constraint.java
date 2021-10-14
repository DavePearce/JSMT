package jsmt.core;

import java.util.Iterator;
import java.util.function.Function;

public abstract class Constraint {

	/**
	 * For a given variable assignment, determine a lower bound for the variable
	 * being constrained.
	 *
	 * @param values
	 * @return
	 */
	public abstract int lowerBound(int[] values);

	/**
	 * For a given variable assignment, determine an upper bound for the variable
	 * being constrained.
	 *
	 * @param values
	 * @return
	 */
	public abstract int upperBound(int[] values);

    public static class Set<T> implements Iterator<T> {
    	/**
    	 * A simple project which takes a matching solution and converts it into some
    	 * desirable object.
    	 */
    	private final Function<int[],T> projection;
    	/**
    	 * Identifies a satisfying state for the given constraints, or <code>null</code>
    	 * if there are no more solutions. For every variable, this contains its current
    	 * value.
    	 */
    	private int[] values;
    	/**
    	 * Represents the current limit for each variable. This determines when the
    	 * current range is exhausted.
    	 */
    	private final int[] limits;

    	/**
    	 * Identifiers a constraints for each variable.
    	 */
    	private final Constraint[] constraints;

    	public Set(Function<int[],T> proj, Constraint... constraints) {
    		final int n = constraints.length;
			this.projection = proj;
    		this.values = new int[n];
    		this.limits = new int[n];
    		this.constraints = constraints;
    		// Find least solution (if one exists)
    		values = findLeastSolution(0, constraints, values, limits);
    	}

    	@Override
		public boolean hasNext() {
    		return values != null;
    	}

    	/**
    	 * Get the next matching solution.
    	 *
    	 * @return
    	 */
    	@Override
		public T next() {
    		T val = projection.apply(values);
    		values = nextSolution(constraints, values, limits);
    		return val;
    	}

		private static int[] nextSolution(Constraint[] constraints, int[] values, int[] limits) {
			int n = values.length - 1;
			//
			// FIXME: should we back up the c here?
			//
			while (n >= 0) {
				if (values[n] >= limits[n]) {
					// Shift constraint pointer
					n = n - 1;
				} else {
					values[n] = values[n] + 1;
					if (findLeastSolution(n + 1, constraints, values, limits) != null) {
						// Found a solution
						return values;
					}
				}
			}
			//
			return null;
		}

    	/**
    	 * Find the least solution for starting from a given variable, whilst setting
    	 * updating the limits accordingly.
    	 *
    	 * @param v
    	 * @param c
    	 * @param constraints
    	 * @param values
    	 * @return
    	 */
    	private static int[] findLeastSolution(int v, Constraint[] constraints, int[] values, int[] limits) {
    		if (v == values.length) {
    			return values;
    		} else {
    			int lb = constraints[v].lowerBound(values);
    			int ub = constraints[v].upperBound(values);
    			//
    			limits[v] = ub;
    			//
    			for (int i = lb; i <= ub; ++i) {
    				values[v] = i;
    				if (findLeastSolution(v + 1, constraints, values, limits) != null) {
    					return values;
    				}
    			}
    			//
    			return null;
    		}
    	}
    }
}
