package jsmt.core;

import java.util.Arrays;

public final class Polynomial {
    private final Polynomial.Term[] terms;

    public Polynomial(int coefficient,int variable) {
        this.terms = new Polynomial.Term[]{new Term(coefficient, variable)};
    }

    public Polynomial(Polynomial.Term... terms) {
        this.terms = terms;
    }

    public Polynomial add(Polynomial p) {
        // NOTE: could be more efficient!
        for(int i=0;i!=terms.length;++i) {
            p = p.add(terms[i]);
        }
        return p;
    }

    private Polynomial add(Polynomial.Term t) {
        for (int i = 0; i != terms.length; ++i) {
            Polynomial.Term ith = terms[i];
            if (Arrays.equals(ith.variables, t.variables)) {
                // No need to append!
                Polynomial.Term[] nterms = Arrays.copyOf(terms, terms.length);
                nterms[i] = new Term(ith.coefficient + t.coefficient, ith.variables);
                //
                return new Polynomial(nterms);
            }
        }
        //
        Polynomial.Term[] nterms = Arrays.copyOf(terms, terms.length + 1);
        nterms[terms.length] = t;
        Arrays.sort(nterms);
        return new Polynomial(nterms);
    }

    @Override
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

    static class Term implements Comparable<Polynomial.Term> {
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
        public int compareTo(Polynomial.Term o) {
            int c = Arrays.compare(variables,o.variables);
            return (c != 0) ? c : Integer.compare(coefficient, o.coefficient);
        }

        @Override
		public boolean equals(Object o) {
            if(o instanceof Polynomial.Term) {
                Polynomial.Term t = (Polynomial.Term) o;
                return coefficient == t.coefficient && Arrays.equals(variables,t.variables);
            }
            return false;
        }

        @Override
		public int hashCode() {
            return coefficient ^ Arrays.hashCode(variables);
        }

        @Override
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