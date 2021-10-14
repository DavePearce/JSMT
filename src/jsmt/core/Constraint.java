package jsmt.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public abstract class Constraint {
    public abstract int pivot();

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

    	public Set(Function<int[],T> proj, int nvars, Constraint... constraints) {
    		this.projection = proj;
    		this.values = new int[nvars];
    		this.limits = new int[nvars];
    		this.constraints = constraints;
    		// Find least solution (if one exists)
    		values = findLeastSolution(0, 0, constraints, values, limits);
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
			int c = constraints.length - 1;
			//
			// FIXME: should we back up the c here?
			//
			while (n >= 0) {
				if (values[n] >= limits[n]) {
					// Shift constraint pointer
					while (c > 0 && n >= constraints[c].pivot()) {
						c = c - 1;
					}
					n = n - 1;
				} else {
					System.out.println("INCREMENTING: " + n);
					values[n] = values[n] + 1;
					if (findLeastSolution(n + 1, c, constraints, values, limits) != null) {
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
    	private static int[] findLeastSolution(int v, int c, Constraint[] constraints, int[] values, int[] limits) {
    		if (v == values.length) {
    			return values;
    		} else {
    			int lb = 0; // Integer.MIN_VALUE + 1;
    			int ub = 2; // Integer.MAX_VALUE;
    			//
    			while (c < constraints.length && constraints[c].pivot() == v) {
    				LinearInequality ieq = (LinearInequality) constraints[c++];
    				int e = ieq.evaluate(values);
    				//
    				System.out.println("EVALUATEd: " + e);
    				// FIXME: somehow we need to get rid of the sign. I think the coefficient of the
    				// variable in question could help here.
    				if (ieq.sign()) {
    					lb = Math.max(lb, -e);
    				} else {
    					ub = Math.min(ub, -e);
    				}
    			}
    			//
    			System.out.println(lb + " <= " + v + " <= " + ub);
    			//
    			limits[v] = ub;
    			//
    			for (int i = lb; i <= ub; ++i) {
    				values[v] = i;
    				if (findLeastSolution(v + 1, c, constraints, values, limits) != null) {
    					return values;
    				}
    			}
    			//
    			return null;
    		}
    	}
    }
}
