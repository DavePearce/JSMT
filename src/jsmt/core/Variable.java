package jsmt.core;

import java.util.Arrays;

/**
 * Represents a general polynomial over a given set of variables, such as
 * <code>(x*x)+(2*y)</code>.
 *
 * @author David J. Pearce
 *
 */
public final class Variable {
    private final Variable.Term[] terms;

    public Variable(int coefficient,int variable) {
        this.terms = new Variable.Term[]{new Term(coefficient, variable)};
    }

    public Variable(Variable.Term... terms) {
        this.terms = terms;
    }

    /**
	 * Evaluate this variable using a given assignment of values to variables.
	 *
	 * @param values
	 * @return
	 */
	public int evaluate(int[] values) {
		int v = terms[0].evaluate(values);
		for (int i = 1; i != terms.length; ++i) {
			v = v + terms[i].evaluate(values);
		}
		return v;
	}

    public Variable add(Variable p) {
        // NOTE: could be more efficient!
        for(int i=0;i!=terms.length;++i) {
            p = p.add(terms[i]);
        }
        return p;
    }

    private Variable add(Variable.Term t) {
        for (int i = 0; i != terms.length; ++i) {
            Variable.Term ith = terms[i];
            if (Arrays.equals(ith.variables, t.variables)) {
                // No need to append!
                Variable.Term[] nterms = Arrays.copyOf(terms, terms.length);
                nterms[i] = new Term(ith.coefficient + t.coefficient, ith.variables);
                //
                return new Variable(nterms);
            }
        }
        //
        Variable.Term[] nterms = Arrays.copyOf(terms, terms.length + 1);
        nterms[terms.length] = t;
        Arrays.sort(nterms);
        return new Variable(nterms);
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

    static class Term implements Comparable<Variable.Term> {
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
        public int compareTo(Variable.Term o) {
            int c = Arrays.compare(variables,o.variables);
            return (c != 0) ? c : Integer.compare(coefficient, o.coefficient);
        }

        @Override
		public boolean equals(Object o) {
            if(o instanceof Variable.Term) {
                Variable.Term t = (Variable.Term) o;
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