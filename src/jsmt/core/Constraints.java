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
//	public static Constraint greaterOrEqual(Variable variable) {
//		return new RelaxedLowerBound(variable);
//	}

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
//	public static Constraint lessOrEqual(Variable variable) {
//		return new RelaxedUpperBound(variable);
//	}

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
	 * Create a constraint representing a value between a fixed lower and upper
	 * bound.
	 *
	 * @param lb
	 * @param ub
	 * @return
	 */
	public static Constraint below(int lb) {
		return new StaticRange(Integer.MIN_VALUE + 1, lb - 1);
	}
	
	/**
	 * Create a constraint representing a value between a fixed lower and upper
	 * bound.
	 *
	 * @param lb
	 * @param ub
	 * @return
	 */
	public static Constraint above(int lb) {
		return new StaticRange(lb + 1, Integer.MAX_VALUE);
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
			int m = Integer.MAX_VALUE;
			for (int i = 0; i < clauses.length; ++i) {
				int n = clauses[i].greatestLowerBound(value, values);
				if(n != Integer.MAX_VALUE) {
					if(m == Integer.MAX_VALUE) {
						m = n;
					} else {
						m = Math.max(m, n);
					}
				}
			}			
			return m;
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
			int m = Integer.MIN_VALUE;
			for (int i = 0; i < clauses.length; ++i) {
				int n = clauses[i].leastUpperBound(value, values);
				if (n != Integer.MIN_VALUE) {
					if (m == Integer.MIN_VALUE) {
						m = n;
					} else {
						m = Math.min(m, n);
					}
				}
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
			int m = Integer.MAX_VALUE;
			//
			for (int i = 0; i < clauses.length; ++i) {
				int n = clauses[i].greatestLowerBound(value, values);
				//
				if(n != Integer.MAX_VALUE) {
					if(m == Integer.MAX_VALUE) {
						m = n;
					} else {
						m = Math.min(m, n);
					}
				}
			}
			return m;
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
			int m = Integer.MIN_VALUE;
			// Hmmm, this is icky.
			for (int i = 0; i < clauses.length; ++i) {
				int n = clauses[i].leastUpperBound(value, values);
				//
				if (n != Integer.MIN_VALUE) {
					if (m == Integer.MIN_VALUE) {
						m = n;
					} else {
						m = Math.min(m, n);
					}
				}
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
			if (value > lowerBound) {
				return Integer.MAX_VALUE;
			} else {
				return lowerBound;
			}
		}

		@Override
		public int leastUpperBound(int value, int[] vars) {
			if(value > upperBound) {
				return Integer.MIN_VALUE;
			} else {
				return upperBound;
			}
		}
	}

	private static class Congruence extends Constraint {
	    private final Variable variable;

	    public Congruence(Variable variable) {
	        this.variable = variable;
	    }

	    @Override
		public int greatestLowerBound(int value, int[] values) {
			int val = variable.evaluate(values);
			if (value > val) {
				return Integer.MAX_VALUE;
			} else {
				return val;
			}
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
			int val = variable.evaluate(values);
			if (value > val) {
				return Integer.MIN_VALUE;
			} else {
				return val;
			}
		}
	}

	private static class StrictLowerBound extends Constraint {
	    private final Variable variable;

	    public StrictLowerBound(Variable variable) {
	        this.variable = variable;
	    }

	    @Override
		public int greatestLowerBound(int value, int[] values) {
			int val = variable.evaluate(values) + 1;
			if (value > val) {
				return Integer.MAX_VALUE;
			} else {
				return val;
			}
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
			return Integer.MAX_VALUE;
		}
	}
	
	private static class StrictUpperBound extends Constraint {
	    private final Variable variable;

	    public StrictUpperBound(Variable variable) {
	        this.variable = variable;
	    }

	    @Override
		public int greatestLowerBound(int value, int[] values) {
			return Integer.MIN_VALUE + 1;			
		}

		@Override
		public int leastUpperBound(int value, int[] values) {
			int val = variable.evaluate(values) - 1;
			if (value > val) {
				return Integer.MIN_VALUE;
			} else {
				return val;
			}
		}
	}
}
