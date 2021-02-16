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

    public static final class Polynomial {
        private final Term[] terms;

        public Polynomial(int coefficient,int variable) {
            this.terms = new Term[]{new Term(coefficient, variable)};
        }

        public Polynomial(Term... terms) {
            this.terms = terms;
        }

        public Polynomial add(Polynomial p) {
            // NOTE: could be more efficient!
            for(int i=0;i!=terms.length;++i) {
                p = p.add(terms[i]);
            }
            return p;
        }

        private Polynomial add(Term t) {
            for (int i = 0; i != terms.length; ++i) {
                Term ith = terms[i];
                if (Arrays.equals(ith.variables, t.variables)) {
                    // No need to append!
                    Term[] nterms = Arrays.copyOf(terms, terms.length);
                    nterms[i] = new Term(ith.coefficient + t.coefficient, ith.variables);
                    //
                    return new Polynomial(nterms);
                }
            }
            //
            Term[] nterms = Arrays.copyOf(terms, terms.length + 1);
            nterms[terms.length] = t;
            Arrays.sort(nterms);
            return new Polynomial(nterms);
        }

        public String toString() {
            String r = "";
            for(int i=0;i!=terms.length;++i) {
                if(i != 0) {
                    r = r + " + ";
                }
                r += terms[i].toString();
            }
            return r;
        }

        public String toString(String[] vars) {
            String r = "";
            for(int i=0;i!=terms.length;++i) {
                if(i != 0) {
                    r = r + " + ";
                }
                r += terms[i].toString(vars);
            }
            return r;
        }

        private static class Term implements Comparable<Term> {
            private final int coefficient;
            private final int[] variables;

            public Term(int coefficient, int... variables) {
                Arrays.sort(variables);
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

            @Override
            public int compareTo(Term o) {
                int c = Arrays.compare(variables,o.variables);
                return (c != 0) ? c : Integer.compare(coefficient, o.coefficient);
            }

            public boolean equals(Object o) {
                if(o instanceof Term) {
                    Term t = (Term) o;
                    return coefficient == t.coefficient && Arrays.equals(variables,t.variables);
                }
                return false;
            }

            public int hashCode() {
                return coefficient ^ Arrays.hashCode(variables);
            }

            public String toString() {
                String r = Integer.toString(coefficient);
                for(int i=0;i!=variables.length;++i) {
                    r += "*" + variables[i];
                }
                return "("+r+")";
            }
            public String toString(String[] vars) {
                String r = Integer.toString(coefficient);
                for(int i=0;i!=variables.length;++i) {
                    r += "*" + vars[variables[i]];
                }
                return "("+r+")";
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
        //
        System.out.println("POLY: " + new Polynomial(new Polynomial.Term(1, 0), new Polynomial.Term(2, 1)).toString(new String[]{"x","y"}));
    }
}
