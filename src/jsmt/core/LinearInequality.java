package jsmt.core;

public class LinearInequality extends Constraint {
    private final boolean sign;
    private final int[] vars;
    private final int[] coeffs;

    public LinearInequality(boolean sign, int[] vars, int[] coeffs) {
        this.sign = sign;
        this.vars = vars;
        this.coeffs = coeffs;
    }

    @Override
	public int pivot() {
        return vars[vars.length-1];
    }

    public boolean sign() {
        return sign;
    }

    public int evaluate(int[] values) {
        int v = 0;
        for(int i=0;i<vars.length;++i) {
            v = v + (coeffs[i] * values[vars[i]]);
        }
        return v;
    }
}