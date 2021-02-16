package jsmt.core;

import java.util.Arrays;

public abstract class Constraint {
    public abstract int pivot();

    public static class Set {
        private final int size;
        private final Constraint[] constraints;

        public Set(int size, Constraint... constraints) {
            this.size = size;
            this.constraints = constraints;
        }

        public int[] sat() {
            return sat(0,0,constraints, new int[size]);
        }

        private static int[] sat(int v, int c, Constraint[] constraints, int[] values) {
            if(v == values.length) {
                return values;
            } else {
                int lb = Integer.MIN_VALUE + 1;
                int ub = Integer.MAX_VALUE;
                //
                while (c < constraints.length && constraints[c].pivot() == v) {
                    LinearInequality ieq = (LinearInequality) constraints[c++];
                    int e = ieq.evaluate(values);
                    // FIXME: somehow we need to get rid of the sign.  I think the coefficient of the variable in question could help here.
                    if (ieq.sign()) {
                        lb = Math.max(lb, -e);
                    } else {
                        ub = Math.min(ub, -e);
                    }
                }
                //
                System.out.println("VAR: " + v + " : " + lb + " .. " + ub);
                //
                for (int i = lb; i <= ub; ++i) {
                    values[v] = i;
                    if (sat(v + 1, c, constraints, values) != null) {
                        return values;
                    }
                }
                //
                return null;
            }
        }
    }

    private static class LinearInequality extends Constraint {
        private final boolean sign;
        private final int[] vars;
        private final int[] coeffs;

        public LinearInequality(boolean sign, int[] vars, int[] coeffs) {
            this.sign = sign;
            this.vars = vars;
            this.coeffs = coeffs;
        }

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

    private static final class Polynomial {
        private final Term[] terms;

        public Polynomial(int coefficient,int variable) {
            this.terms = new Term[]{new Term(coefficient, variable)};
        }

        private static class Term {
            private final int coefficient;
            private final int[] variables;

            public Term(int coefficient, int... variables) {
                this.coefficient = coefficient;
                this.variables = variables;
            }

            public int evaluate(int[] values) {
                int v = coefficient;
                for(int i=0;i!=variables.length;++i) {
                    v = v * values[variables[i]];
                }
                return v;
            }
        }
    }

    public static void main(String[] args) {
        // 0 <= x + y
        LinearInequality ieq = new LinearInequality(true, new int[]{0, 1}, new int[]{1, 1});
        //
        Constraint.Set set = new Constraint.Set(2,ieq);
        //
        System.out.println("SAT: " + Arrays.toString(set.sat()));
    }
}
