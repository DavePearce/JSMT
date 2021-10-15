package jsmt.core;

public class Constraints {
	/**
	 * A constant representing the value <code>1</code>. This is normally used to
	 * constraint zeroth indexed variable.
	 */
	public static Constraint ONE = between(1,1);

	/**
	 * A constraint representing the an unsigned byte type.
	 */
	public static Constraint U8 = between(0,255);
	/**
	 * A constraint representing the a signed byte type.
	 */
	public static Constraint I8 = between(-128,127);

	/**
	 * Create a constraint as the logical conjunction of several constraints.
	 *
	 * @param clauses
	 * @return
	 */
	public static Constraint and(Constraint... clauses) {
		return new Conjunction(clauses);
	}

	/**
	 * Create a constraint as the logical disjunction of several constraints.
	 *
	 * @param clauses
	 * @return
	 */
	public static Constraint or(Constraint... clauses) {
		return new Disjunction(clauses);
	}
	
	/**
	 * Create a constraint representing a value which is greater than or equal to a
	 * given variable.
	 *
	 * @param variable
	 * @return
	 */
	public static Constraint greaterOrEqual(Variable variable) {
		return new RelaxedLowerBound(variable);
	}

	/**
	 * Create a constraint representing a value which is greater than a given
	 * variable.
	 *
	 * @param variable
	 * @return
	 */
	public static Constraint greaterThan(Variable variable) {
		return new StrictLowerBound(variable);
	}

	/**
	 * Create a constraint representing a value which is less than or equal to a
	 * given variable.
	 *
	 * @param variable
	 * @return
	 */
	public static Constraint lessOrEqual(Variable variable) {
		return new RelaxedUpperBound(variable);
	}

	/**
	 * Create a constraint representing a value which is less than a given variable.
	 *
	 * @param variable
	 * @return
	 */
	public static Constraint lessThan(Variable variable) {
		return new StrictUpperBound(variable);
	}

	/**
	 * Create a constraint requiring a variable to equal another.
	 *
	 * @param variable
	 * @return
	 */
	public static Constraint equal(Variable variable) {
		return new Congruence(variable);
	}

	/**
	 * Create a constraint representing a value between a fixed lower and upper
	 * bound.
	 *
	 * @param lb
	 * @param ub
	 * @return
	 */
	public static Constraint between(int lb, int ub) {
		return new StaticRange(lb,ub);
	}

	/**
	 * Provides a reasonably straightforward implementation for combining
	 * constraints such that they all must hold..
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class Conjunction extends Constraint {
		private final Constraint[] clauses;

		public Conjunction(Constraint...clauses) {
			this.clauses = clauses;
		}

		@Override
		public int greatestLowerBound(int value, int[] values) {
			int m = clauses[0].greatestLowerBound(value, values);
			for (int i = 1; i < clauses.length; ++i) {
				int n = clauses[i].greatestLowerBound(value, values);
				m = Math.max(m, n);
			}
			return m;
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
			int m = clauses[0].leastUpperBound(value, values);
			for (int i = 1; i < clauses.length; ++i) {
				int n = clauses[i].leastUpperBound(value, values);
				m = Math.min(m, n);
			}
			return m;
		}
	}

	/**
	 * Provides a reasonably straightforward implementation for combining
	 * constraints such that at least one must hold.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class Disjunction extends Constraint {
		private final Constraint[] clauses;

		public Disjunction(Constraint...clauses) {
			this.clauses = clauses;
		}

		@Override
		public int greatestLowerBound(int value, int[] values) {
			int m = clauses[0].greatestLowerBound(value, values);
			for (int i = 1; i < clauses.length; ++i) {
				int n = clauses[i].greatestLowerBound(value, values);
				m = Math.min(m, n);
			}
			return m;
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
			int m = clauses[0].leastUpperBound(value, values);
			for (int i = 1; i < clauses.length; ++i) {
				int n = clauses[i].leastUpperBound(value, values);
				m = Math.max(m, n);
			}
			return m;
		}
	}
	
	/**
	 * Represents a very simple constraint on a variable, namely a fixed lower and
	 * upper bound. Such a constraint normally arises from the type of the variable
	 * in question.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class StaticRange extends Constraint {
		private final int lowerBound;
		private final int upperBound;

		public StaticRange(int lower, int upper) {
			this.lowerBound = lower;
			this.upperBound = upper;
		}

		@Override
		public int greatestLowerBound(int value, int[] vars) {
			return Math.max(lowerBound, value);
		}

		@Override
		public int leastUpperBound(int value, int[] vars) {
			return Math.min(upperBound, value);
		}
	}

	private static class Congruence extends Constraint {
	    private final Variable variable;

	    public Congruence(Variable variable) {
	        this.variable = variable;
	    }

	    @Override
		public int greatestLowerBound(int value, int[] values) {
//			return variable.evaluate(values);
	    	throw new IllegalArgumentException("what to do?");
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
//			return variable.evaluate(values);
			throw new IllegalArgumentException("what to do?");
		}
	}

	private static class RelaxedLowerBound extends Constraint {
	    private final Variable variable;

	    public RelaxedLowerBound(Variable variable) {
	        this.variable = variable;
	    }

		@Override
		public int greatestLowerBound(int value, int[] values) {
			return Math.max(value, variable.evaluate(values));
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
			return Math.min(value, Integer.MAX_VALUE);
		}
	}

	private static class StrictLowerBound extends Constraint {
	    private final Variable variable;

	    public StrictLowerBound(Variable variable) {
	        this.variable = variable;
	    }

	    @Override
		public int greatestLowerBound(int value, int[] values) {
			return Math.max(value, variable.evaluate(values) + 1);
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
			return Math.min(value, Integer.MAX_VALUE);
		}
	}

	private static class RelaxedUpperBound extends Constraint {
	    private final Variable variable;

	    public RelaxedUpperBound(Variable variable) {
	        this.variable = variable;
	    }

	    @Override
		public int greatestLowerBound(int value, int[] values) {
			return Math.max(value, Integer.MIN_VALUE + 1);
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
			return Math.min(value, variable.evaluate(values));
		}
	}

	private static class StrictUpperBound extends Constraint {
		private final Variable variable;

		public StrictUpperBound(Variable variable) {
			this.variable = variable;
		}

		@Override
		public int greatestLowerBound(int value, int[] values) {
			return Math.max(value, Integer.MIN_VALUE + 1);
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
			return Math.min(value, variable.evaluate(values) + 1);
		}
	}
}
