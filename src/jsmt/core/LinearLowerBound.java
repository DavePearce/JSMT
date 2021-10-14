package jsmt.core;

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
public class LinearLowerBound extends Constraint {
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
}