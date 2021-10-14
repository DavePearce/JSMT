package jsmt.core;

public class Constraints {
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
	 * Create a constraint representing a value which is _at least_ the value of a
	 * given variable (i.e. greater-than-or-equal to).
	 *
	 * @param var
	 * @return
	 */
	public static Constraint atleast(Constraint.Variable var) {
		return new LinearLowerBound(new int[] { var.getIndex() }, new int[] { 1 });
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

	private static class Conjunction extends Constraint {
		private final Constraint[] clauses;

		public Conjunction(Constraint...clauses) {
			this.clauses = clauses;
		}

		@Override
		public int lowerBound(int[] values) {
			int m = clauses[0].lowerBound(values);
			for (int i = 1; i < clauses.length; ++i) {
				int n = clauses[i].lowerBound(values);
				m = Math.max(m, n);
			}
			return m;
		}

		@Override
		public int upperBound(int[] values) {
			int m = clauses[0].upperBound(values);
			for (int i = 1; i < clauses.length; ++i) {
				int n = clauses[i].upperBound(values);
				m = Math.min(m, n);
			}
			return m;
		}

		@Override
		public int pivot() {
			int p = clauses[0].pivot();
			for (int i = 1; i < clauses.length; ++i) {
				int n = clauses[i].pivot();
				p = Math.max(p, n);
			}
			return p;
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
		public int lowerBound(int[] vars) {
			return lowerBound;
		}

		@Override
		public int upperBound(int[] vars) {
			return upperBound;
		}

		@Override
		public int pivot() {
			return -1;
		}
	}

	/**
	 * For a given variable <code>v</code>, this encodes a linear inequality of the
	 * following form:
	 *
	 * <pre>
	 * (w1*c1) + ... + (wn*cn) <= v
	 * </pre>
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class LinearLowerBound extends Constraint {
	    private final int[] vars;
	    private final int[] coeffs;

	    public LinearLowerBound(int[] vars, int[] coeffs) {
	        this.vars = vars;
	        this.coeffs = coeffs;
	    }

	    @Override
		public int lowerBound(int[] values) {
			int v = 0;
	        for(int i=0;i<vars.length;++i) {
	            v = v + (coeffs[i] * values[vars[i]]);
	        }
	        return v;
		}

		@Override
		public int upperBound(int[] values) {
			return Integer.MAX_VALUE;
		}

		@Override
		public int pivot() {
			int p = vars[0];
			for (int i = 1; i != vars.length; ++i) {
				p = Math.max(p, vars[i]);
			}
			return p;
		}
	}
}
