package jsmt.constraints;

import jsmt.core.Constraint;

public class Conjunction extends Constraint {
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
