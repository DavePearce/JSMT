package jsmt.util;

import java.math.BigInteger;
import java.util.Arrays;

import jsmt.util.ArrayUtils;

public class Polynomial implements Comparable<Polynomial> {

	public static final Polynomial ZERO = new Polynomial(new Term(BigInteger.ZERO));

	private final Term[] terms;

	private Polynomial(Term...terms) {
		this.terms = terms;
	}

	/**
	 * Get the number of terms in this polynomial.
	 *
	 * @return
	 */
	public int size() {
		return terms.length;
	}
	public boolean isConstant() {
		return (terms.length == 1 && terms[0].isConstant());
	}
	public BigInteger toConstant() {
		return terms[0].getCoefficient();
	}

	/**
	 * Get the ith term of this polynomial.
	 *
	 * @param i
	 * @return
	 */
	public Term getTerm(int i) {
		return terms[i];
	}

	public Polynomial negate() {
		Term[] newTerms = new Term[terms.length];
		for(int i=0;i!=terms.length;++i) {
			newTerms[i] = terms[i].negate();
		}
		return new Polynomial(newTerms);
	}

	/**
	 * <p>
	 * Add two polynomials together, producing a polynomial in normal form.
	 * To do this, we must add the coefficients for terms which have the
	 * same set of atoms, whilst other terms are incorporated as is. For
	 * example, consider adding <code>2+2x</code> with <code>1+3x+4y</code>.
	 * In this case, we have some terms in common, so the result becomes
	 * <code>(2+1) + (2x+3x) + 4y</code> which is simplified to
	 * <code>3 + 5x + 4y</code>.
	 * </p>
	 *
	 * @param poly
	 * @return
	 */
	public Polynomial add(Polynomial p) {
		Polynomial.Term[] combined = new Polynomial.Term[terms.length+p.terms.length];
		System.arraycopy(terms, 0, combined, 0, terms.length);
		System.arraycopy(p.terms, 0, combined, terms.length, p.terms.length);
		return construct(combined);
	}

	public Polynomial add(Polynomial.Term p) {
		Polynomial.Term[] combined = new Polynomial.Term[terms.length+1];
		System.arraycopy(terms, 0, combined, 0, terms.length);
		combined[terms.length] = p;
		return construct(combined);
	}

	public Polynomial subtract(Polynomial p) {
		return add(p.negate());
	}
	public Polynomial subtract(Polynomial.Term p) {
		return add(p.negate());
	}

	/**
	 * Multiply two polynomials together. This is done by reusing the add()
	 * function as much as possible, though this may not be the most
	 * efficient. In essence, to multiply one polynomial (e.g.
	 * <code>2+2x</code>) by another (e.g.<code>1+3x+4y</code>) it breaks it
	 * down into a series of multiplications over terms and additions. That
	 * is, we multiply each term from the first polynomial by the second
	 * (e.g. <code>2*(1+3x+4y)</code> and <code>2x*(1+3x+4y)</code>). Then,
	 * we add the results together (e.g.
	 * <code>(2+6x+8y) + (2x+6x2+8xy)</code>).
	 *
	 * @param p
	 * @return
	 */
	public Polynomial multiply(Polynomial p) {
		int lhs_size = terms.length;
		int rhs_size = p.terms.length;
		Polynomial.Term[] combined = new Polynomial.Term[lhs_size * rhs_size];

		for (int i = 0; i != lhs_size; ++i) {
			Polynomial.Term lhsTerm = terms[i];
			int j_base = i * rhs_size;
			for (int j = 0; j != rhs_size; ++j) {
				Polynomial.Term rhsTerm = p.terms[j];
				combined[j_base + j] = multiply(lhsTerm, rhsTerm);
			}
		}

		return construct(combined);
	}

	public Polynomial multiply(Polynomial.Term rhs) {
		int lhs_size = terms.length;
		Polynomial.Term[] combined = new Polynomial.Term[lhs_size];

		for (int i = 0; i != lhs_size; ++i) {
			Polynomial.Term lhsTerm = terms[i];
			combined[i] = multiply(lhsTerm, rhs);
		}

		return construct(combined);
	}

	public Polynomial multiply(BigInteger rhs) {
		int lhs_size = terms.length;
		Polynomial.Term[] combined = new Polynomial.Term[lhs_size];

		for (int i = 0; i != lhs_size; ++i) {
			combined[i] = terms[i].multiply(rhs);
		}

		return construct(combined);
	}


	/**
	 * Factorise a given polynomial. For example, <code>2x+2</code> is
	 * factorised to be <code>x+1</code>. Observe that this does not preseve the
	 * result of the polynomial. However, it is safe to do when simplifying
	 * equations. For example, <code>2x == 2y</code> can be safely factorised to
	 * <code>x == y</code>.
	 *
	 * @param p
	 * @return
	 */
	public Polynomial factorise() {
		BigInteger factor = terms[0].getCoefficient();
		// In case of just one coefficient which is negative, we need to compute
		// abs() here.
		factor = factor.abs();
		//
		for (int i = 1; i != terms.length; ++i) {
			BigInteger c = terms[i].getCoefficient();
			factor = factor.gcd(c);
		}
		if (factor.equals(BigInteger.ZERO) || factor.equals(BigInteger.ONE)) {
			// No useful factor discovered
			return this;
		} else {
			// Yes, we found a useful factor. Therefore, divide all coefficients
			// by this.
			Polynomial r = Polynomial.ZERO;
			for (int i = 0; i != terms.length; ++i) {
				Polynomial.Term t = terms[i];
				BigInteger c = t.getCoefficient();
				c = c.divide(factor);
				r = r.add(new Polynomial.Term(c, t.getAtoms()));
			}
			return r;
		}
	}

	@Override
	public int compareTo(Polynomial p) {
		int lengthDifference = terms.length - p.terms.length;
		if(lengthDifference != 0) {
			return lengthDifference;
		} else {
			for(int i=0;i!=terms.length;++i) {
				Polynomial.Term t1 = terms[i];
				Polynomial.Term t2 = p.terms[i];
				int c = t1.compareTo(t2);
				if(c != 0) {
					return c;
				}
			}
			return 0;
		}
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Polynomial) {
			Polynomial p = (Polynomial) o;
			return Arrays.equals(terms, p.terms);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(terms);
	}

	@Override
	public String toString() {
		String r = "(";
		for(int i=0;i!=terms.length;++i) {
			if(i != 0) {
				r += " + ";
			}
			r += terms[i].toString();
		}
		return r + ")";
	}

	public static class Term implements Comparable<Term> {
		private final BigInteger coefficient;
		private final int[] atoms;

		public Term(BigInteger coefficient, int...atoms) {
			if(coefficient.equals(BigInteger.ZERO) && atoms.length > 0) {
				throw new IllegalArgumentException("invalid zero term");
			}
			this.coefficient = coefficient;
			this.atoms = atoms;
		}
		public BigInteger getCoefficient() {
			return coefficient;
		}
		public boolean isConstant() {
			return atoms.length == 0;
		}
		public int[] getAtoms() {
			return atoms;
		}

		@Override
		public int compareTo(Term t) {
			int lengthDifference = atoms.length - t.atoms.length;
			if (lengthDifference != 0) {
				return lengthDifference;
			} else {
				for (int i = 0; i != atoms.length; ++i) {
					int t1 = atoms[i];
					int t2 = t.atoms[i];
					int c = Integer.compare(t1,t2);
					if (c != 0) {
						return c;
					}
				}
				return coefficient.compareTo(t.coefficient);
			}
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof Term) {
				Term t = (Term) o;
				return coefficient.equals(t.coefficient) && Arrays.equals(atoms, t.atoms);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return coefficient.hashCode() ^ Arrays.hashCode(atoms);
		}

		public Term negate() {
			return new Term(coefficient.negate(),atoms);
		}

		public Term multiply(BigInteger rhs) {
			if (coefficient.equals(BigInteger.ZERO)) {
				return this;
			} else if (rhs.equals(BigInteger.ZERO)) {
				return new Polynomial.Term(rhs);
			} else {
				BigInteger coefficient = this.coefficient.multiply(rhs);
				return new Polynomial.Term(coefficient, atoms);
			}
		}

		@Override
		public String toString() {
			String r = "";
			if(!coefficient.equals(BigInteger.ONE) || atoms.length == 0) {
				r += coefficient;
				if(atoms.length > 0) {
					r += "*";
				}
			}
			for(int i=0;i!=atoms.length;++i) {
				if(i != 0) {
					r += "*";
				}
				r += VARIABLES[atoms[i]];
			}
			return r;
		}

		private static final String[] VARIABLES = {
				"a","b","c","d","e","f","g","h","i","j","k","l"
		};
	}

	private static Polynomial.Term multiply(Polynomial.Term lhs, Polynomial.Term rhs) {
		if (lhs.coefficient.equals(BigInteger.ZERO)) {
			return lhs;
		} else if (rhs.coefficient.equals(BigInteger.ZERO)) {
			return rhs;
		} else {
			BigInteger coefficient = lhs.getCoefficient().multiply(rhs.getCoefficient());
			int[] lhsAtoms = lhs.getAtoms();
			int[] rhsAtoms = rhs.getAtoms();
			int[] atoms = new int[lhsAtoms.length + rhsAtoms.length];
			System.arraycopy(lhsAtoms, 0, atoms, 0, lhsAtoms.length);
			System.arraycopy(rhsAtoms, 0, atoms, lhsAtoms.length, rhsAtoms.length);
			Arrays.sort(atoms);
			return new Polynomial.Term(coefficient, atoms);
		}
	}

	/**
	 * Given a list of unsorted and potentially overlapping terms, apply the
	 * necessary simplifications to produce a polynomial in normal form. For
	 * example, given <code>[2, 7x, 4y, -x]</code> we would end up with
	 * <code>[1, 3x, 2y]</code>.
	 *
	 * @param terms
	 * @return
	 */
	public static Polynomial construct(Polynomial.Term... terms) {
		terms = merge(terms);
		// In the case that all terms were eliminated as null, simply ensure
		// that zero is present. This can happen is all terms cancelled out.
		if (terms.length == 0) {
			// FIXME: can zero be represented using an empty term array?
			// This does make the manipulation of polynomials more awkward than
			// necessary.
			terms = new Polynomial.Term[] { new Polynomial.Term(BigInteger.ZERO) };
		}
		// Sort remaining terms
		Arrays.sort(terms);
		// Done
		return new Polynomial(terms);
	}

	private static boolean isZero(Polynomial.Term term) {
		BigInteger coefficient = term.getCoefficient();
		return coefficient.equals(BigInteger.ZERO);
	}

	/**
	 * Combine all terms which have the same set of atoms by adding the
	 * coefficients together. For example, [x,2x] is combined into [null,3x].
	 *
	 * @param terms
	 */
	private static Polynomial.Term[] merge(Polynomial.Term[] terms) {
		//
		for (int i = 0; i != terms.length; ++i) {
			Polynomial.Term ith = terms[i];
			if (ith != null) {
				if (isZero(ith)) {
					// Eliminate any zeros which may have arisen during the
					// calculation.
					terms[i] = null;
				} else {
					int[] ithAtoms = ith.getAtoms();
					for (int j = i + 1; j != terms.length; ++j) {
						Polynomial.Term jth = terms[j];
						if (jth != null && Arrays.equals(ithAtoms,jth.getAtoms())) {
							// We have two overlapping terms, namely i and j.
							// Add them together and assign the result to the
							// jth position.
							terms[j] = merge(ith,jth);
							terms[i] = null;
							break;
						}
					}
				}
			}
		}
		// Strip out null entries
		return ArrayUtils.removeAll(terms, null);
	}

	/**
	 * Merge two polynomial terms which are assumed to have the same set of
	 * atoms.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private static Polynomial.Term merge(Polynomial.Term lhs, Polynomial.Term rhs) {
		BigInteger lhsCoeff = lhs.getCoefficient();
		BigInteger rhsCoeff = rhs.getCoefficient();
		BigInteger r = lhsCoeff.add(rhsCoeff);
		if (r.equals(BigInteger.ZERO)) {
			return null;
		} else {
			return new Polynomial.Term(r, lhs.getAtoms());
		}
	}
}
