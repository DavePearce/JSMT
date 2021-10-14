package jsmt.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Represents a constraint (or set of constraints) applied to an individual
 * variable.
 *
 * @author David J. Pearce
 *
 */
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

	/**
	 * Represents the largest variable used in this constraint (by index), or
	 * <code>-1</code> if no variables are used.
	 *
	 * @return
	 */
	public abstract int pivot();

	/**
	 * Represents an allocated constraint variable.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Variable {
		private final int index;

		Variable(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}
	}

	/**
	 * Represents a constrained set of items.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public static class Set<T> implements Iterable<T> {
		private final ArrayList<Constraint> constraints = new ArrayList<>();
		private final Function<int[], T> projection;

		public Set(Function<int[], T> projection) {
			this.projection = projection;
		}

		/**
		 * Add a new variable to the constraint set. This returns a handle through which
		 * we can interact with the allocated variable in creating subsequent downstream
		 * constraints.
		 *
		 * @param constraint
		 * @return
		 */
		public Variable add(Constraint constraint) {
			int n = constraints.size();
			constraints.add(constraint);
			return new Variable(n);
		}

		@Override
		public Iterator<T> iterator() {
			Constraint[] array = constraints.toArray(new Constraint[constraints.size()]);
			return new InternalIterator<>(projection, array);
		}
	}

	/**
	 * Represents a set of constraints over one or more variables. This provides the
	 * mechanism for efficiently iterating solutions to the constraints.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
    private static class InternalIterator<T> implements Iterator<T> {
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

    	/**
		 * Construct a constraint system for a given number of variables, where each
		 * constraint corresponds to a given variable. A projection function is provided
		 * for extracting solutions.
		 *
		 * @param proj
		 * @param constraints
		 */
    	public InternalIterator(Function<int[],T> proj, Constraint... constraints) {
			final int n = constraints.length;
			// Sanity check constraints
			for (int i = 0; i != n; ++i) {
				int ith = constraints[i].pivot();
				if (ith >= i) {
					throw new IllegalArgumentException("constraint " + i + " depends on variable " + ith);
				}
			}
    		//
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
