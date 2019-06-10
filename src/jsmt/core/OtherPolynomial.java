// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package jsmt.core;

import java.util.*;

import jmodelgen.util.Pair;

import java.math.*;

/**
 * Provides a general class for representing OtherPolynomials. <b>Please note that
 * OtherPolynomial division and gcd is experimental and probably contains bugs</b>.
 *
 * @author David J. Pearce
 *
 */
public final class OtherPolynomial implements Iterable<OtherPolynomial.Term>,
		Comparable<OtherPolynomial> {
	public static final OtherPolynomial MTWO = new OtherPolynomial(-2);
	public static final OtherPolynomial MONE = new OtherPolynomial(-1);
	public static final OtherPolynomial ZERO = new OtherPolynomial(0);
	public static final OtherPolynomial ONE = new OtherPolynomial(1);
	public static final OtherPolynomial TWO = new OtherPolynomial(2);
	public static final OtherPolynomial THREE = new OtherPolynomial(3);
	public static final OtherPolynomial FOUR = new OtherPolynomial(4);
	public static final OtherPolynomial FIVE = new OtherPolynomial(5);
	public static final OtherPolynomial TEN = new OtherPolynomial(10);

	// NOTE: ZERO is represented only by the empty set of terms and not, for
	// example, as a single term with no variables and zero coefficient.
	private final HashSet<Term> terms;

	public OtherPolynomial() {
		terms = new HashSet<>();
	}

	public OtherPolynomial(int constant) {
		terms = new HashSet<>();
		if (constant != 0) {
			this.terms.add(new Term(constant));
		}
	}

	public OtherPolynomial(BigInteger constant) {
		terms = new HashSet<>();
		if (!constant.equals(BigInteger.ZERO)) {
			this.terms.add(new Term(constant));
		}
	}

	public OtherPolynomial(String atom) {
		terms = new HashSet<>();
		terms.add(new Term(1, atom));
	}

	public OtherPolynomial(Term term) {
		terms = new HashSet<>();
		if (!term.coefficient().equals(BigInteger.ZERO)) {
			this.terms.add(term);
		}
	}

	public OtherPolynomial(Term... terms) {
		this.terms = new HashSet<>();
		for (Term t : terms) {
			if (!t.coefficient().equals(BigInteger.ZERO)) {
				this.terms.add(t);
			}
		}
	}

	public OtherPolynomial(Set<Term> terms) {
		this.terms = new HashSet<>();
		for (Term t : terms) {
			if (!t.coefficient().equals(BigInteger.ZERO)) {
				this.terms.add(t);
			}
		}
	}

	public OtherPolynomial(OtherPolynomial poly) {
		this.terms = (HashSet<Term>) poly.terms.clone();
	}

	/* =========================================================== */
	/* ========================== ACCESSORS ====================== */
	/* =========================================================== */

	@Override
	public Iterator<Term> iterator() {
		return terms.iterator();
	}

	public Set<Term> terms() {
		return Collections.unmodifiableSet(terms);
	}

	public boolean isConstant() {
		for (Term e : terms) {
			if (!e.isConstant()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Assumes that isConstant() holds.
	 *
	 * @return
	 */
	public BigInteger constant() {
		BigInteger c = BigInteger.ZERO;
		for (Term e : terms) {
			c = c.add(e.coefficient());
		}
		return c;
	}

	public boolean isLinear() {
		for (Term e : terms) {
			if (e.atoms().size() > 1) {
				return false;
			}
		}
		return true;
	}

	public boolean isAtom() {
		if (terms.size() != 1) {
			return false;
		}
		Term t = terms.iterator().next();
		if (t.atoms().size() != 1 || !t.coefficient().equals(BigInteger.ONE)) {
			return false;
		}

		return true;
	}

	public String atom() {
		return terms.iterator().next().atoms().get(0);
	}

	public Set<String> atoms() {
		HashSet<String> fvs = new HashSet();
		for (Term e : terms) {
			fvs.addAll(e.atoms());
		}
		return fvs;
	}

	// ===========================================================
	// ADDITION
	// ===========================================================

	public OtherPolynomial add(int i) {
		return add(new Term(i));
	}

	public OtherPolynomial add(BigInteger i) {
		return add(new Term(i));
	}

	public OtherPolynomial add(Term e) {
		final BigInteger zero = BigInteger.ZERO;

		for (Term me : terms) {
			if (me.atoms().equals(e.atoms())) {
				BigInteger ncoeff = me.coefficient().add(e.coefficient());
				OtherPolynomial r = new OtherPolynomial(this);
				r.terms.remove(me);
				if (!(ncoeff.equals(zero))) {
					r = r.add(new Term(ncoeff, me.atoms()));
				}
				return r;
			}
		}
		if (!e.coefficient().equals(zero)) {
			OtherPolynomial r = new OtherPolynomial(this);
			r.terms.add(e);
			return r;
		} else {
			return this;
		}
	}

	public OtherPolynomial add(OtherPolynomial poly) {
		OtherPolynomial r = this;
		for (Term e : poly.terms) {
			r = r.add(e);
		}
		return r;
	}

	// ===========================================================
	// SUBTRACTION
	// ===========================================================

	public OtherPolynomial subtract(int i) {
		return subtract(new Term(i));
	}

	public OtherPolynomial subtract(BigInteger i) {
		return subtract(new Term(i));
	}

	public OtherPolynomial subtract(Term e) {
		Term ne = new Term(e.coefficient().negate(), e.atoms());
		return add(ne);
	}

	public OtherPolynomial subtract(OtherPolynomial poly) {
		OtherPolynomial r = this;
		for (Term e : poly.terms) {
			r = r.subtract(e);
		}
		return r;
	}

	// ===========================================================
	// MULTIPLICATION
	// ===========================================================

	public OtherPolynomial multiply(int i) {
		return multiply(new Term(i));
	}

	public OtherPolynomial multiply(BigInteger i) {
		return multiply(new Term(i));
	}

	public OtherPolynomial multiply(Term e1) {
		OtherPolynomial r = new OtherPolynomial();
		for (Term e2 : terms) {
			r = r.add(e1.multiply(e2));
		}
		return r;
	}

	public OtherPolynomial multiply(OtherPolynomial poly) {
		OtherPolynomial r = new OtherPolynomial();
		for (Term e : poly.terms) {
			r = r.add(this.multiply(e));
		}
		return r;
	}

	// ===========================================================
	// GCD
	// ===========================================================

	/**
	 * <p>
	 * This method computes the Greatest Common Divisor of this OtherPolynomial and
	 * the supplied OtherPolynomial. That is, the "biggest" OtherPolynomial that divides
	 * evenly into both OtherPolynomials. For example:
	 * </p>
	 *
	 * <pre>
	 * gcd(2x,x) = x
	 * gcd(2x+1,x) = 1
	 * </pre>
	 *
	 * <p>
	 * In the special case that both OtherPolynomials are constants, then it simply
	 * resolves to the normal gcd operation.
	 * </p>
	 * <p>
	 * For more information, see for example this <a href=
	 * "http://en.wikipedia.org/wiki/Greatest_common_divisor_of_two_OtherPolynomials"
	 * >Wikipedia page</a>.
	 * </p>
	 * <b>NOTE: THE IMPLEMENTATION OF THIS METHOD IS CURRENTLY BUGGY. IN
	 * PARTICULAR, IT CAN RETURN NEGATIVE INTEGERS UNLIKE NORMAL GCD</b>
	 */
	public OtherPolynomial gcd(OtherPolynomial a) {
		final OtherPolynomial zero = OtherPolynomial.ZERO;
		OtherPolynomial b = this;
		OtherPolynomial c;

		// First, decide the right way around for a + b

		// BUG HERE: currently there is a bug here, since it doesn't always make
		// the right choice. In particular, if both OtherPolynomials are in fact
		// integers, then it doesn't always pick the largest.
		Pair<OtherPolynomial, OtherPolynomial> r = a.divide(b);

		if (r.first().equals(zero)) {
			r = a.divide(b);
			if (r.first().equals(zero)) {
				// a + b are mutually indivisible
				return OtherPolynomial.ONE;
			} else {
				// b is divisible by a, but not the other way around.
				c = a;
				a = b;
				b = c;
			}
		}

		while (!b.equals(OtherPolynomial.ZERO)) {
			r = a.divide(b);
			c = r.second();
			if (c.equals(OtherPolynomial.ZERO)) {
				return b;
			} else if (r.first().equals(OtherPolynomial.ZERO)) {
				// no further division is possible.
				return b;
			}
			a = b;
			b = c;
		}
		return a;
	}

	/**
	 * This method divides this OtherPolynomial by the term argument using simple
	 * division. The method produces the pair (quotient,remainder). For example:
	 *
	 * <pre>
	 * (x + 2xy) / x = (1+2y,0)
	 * (x + 2xy) / y = (2x,x)
	 *
	 * &#064;param x
	 * &#064;return
	 *
	 * For more information on OtherPolynomial division see: &lt;a href=&quot;http://en.wikipedia.org/wiki/OtherPolynomial_long_division&quot;&gt;wikipedia&lt;/a&gt;
	 *
	 */
	public Pair<OtherPolynomial, OtherPolynomial> divide(Term t1) {
		OtherPolynomial quotient = new OtherPolynomial(0);
		OtherPolynomial remainder = new OtherPolynomial(0);

		for (Term t2 : terms) {
			Pair<Term, Term> r = t2.divide(t1);
			quotient = quotient.add(r.first());
			remainder = remainder.add(r.second());
		}

		return new Pair(quotient, remainder);
	}

	/**
	 * This method divides this OtherPolynomial by the OtherPolynomial argument using long
	 * division. The method produces the pair (quotient,remainder). For example:
	 *
	 * <pre>
	 * (x + 2xy) / x = (1+2y,0)
	 * (x + 2xy) / y = (2x,x)
	 *
	 * &#064;param x
	 * &#064;return
	 *
	 * For more information on OtherPolynomial long division see: &lt;a href=&quot;http://en.wikipedia.org/wiki/OtherPolynomial_long_division&quot;&gt;wikipedia&lt;/a&gt;
	 *
	 */
	public Pair<OtherPolynomial, OtherPolynomial> divide(OtherPolynomial x) {

		// Ok, yes, this piece of code is horribly inefficient. But, it's tough
		// even to make it work properly, let alone make it work fast.

		Term max = null;

		for (Term t : x) {
			if (max == null || max.compareTo(t) > 0) {
				max = t;
			}
		}

		if (max == null) {
			// this indicates an attempt at division by zero!
			throw new ArithmeticException("OtherPolynomial division by zero");
		}

		ArrayList<Term> myterms = new ArrayList<>(terms);
		Collections.sort(myterms);

		for (Term t1 : myterms) {
			Pair<Term, Term> d = t1.divide(max);
			if (!d.first().equals(Term.ZERO)) {
				Term quotient = d.first();

				OtherPolynomial remainder = this.subtract(x.multiply(quotient));

				Pair<OtherPolynomial, OtherPolynomial> r = remainder.divide(x);
				return new Pair(r.first().add(quotient), r.second());
			}
		}

		// base case for recursion.
		return new Pair(OtherPolynomial.ZERO, this);
	}

	// ===========================================================
	// NEGATION
	// ===========================================================

	public OtherPolynomial negate() {
		OtherPolynomial r = new OtherPolynomial(0);

		for (Term t : terms) {
			r.terms.add(t.negate());
		}

		return r;
	}

	// ===========================================================
	/* ======================= FACTORISATION ===================== */
	// ===========================================================

	/**
	 * The purpose of this method is to factorise the OtherPolynomial for a given
	 * variable, producing a factor and a remainder. For example:
	 *
	 * <pre>
	 * 2x + xy + 2 ======&gt; (2+y, 2)
	 * </pre>
	 *
	 * Here, <code>2+y</code> is the factor, whilst <code>2</code> is the
	 * remainder. Thus, <code>x * (2+y) + 2</code> yields the original
	 * OtherPolynomial.
	 *
	 * Notice, that in the case where the variable in question is raised to a
	 * power, then the factor will contain the original variable. For example:
	 *
	 * <pre>
	 * 2x&circ;2 + xy + 2 ======&gt; (2x+y, 2)
	 * </pre>
	 */
	public Pair<OtherPolynomial, OtherPolynomial> factoriseFor(String atom) {
		OtherPolynomial factor = new OtherPolynomial(0);
		OtherPolynomial remainder = new OtherPolynomial(0);

		for (Term t : terms) {
			if (t.atoms().contains(atom)) {
				ArrayList<String> atoms = new ArrayList(t.atoms());
				atoms.remove(atom); // remove one instance of var only
				factor = factor.add(new Term(t.coefficient(), atoms));
			} else {
				remainder = remainder.add(t);
			}
		}

		return new Pair(factor, remainder);
	}

	// ===========================================================
	// OTHER
	// ===========================================================

	@Override
	public int compareTo(OtherPolynomial p) {
		Collection<Term> p_terms = p.terms;

		if (terms.size() < p_terms.size()) {
			return -1;
		} else if (terms.size() > p_terms.size()) {
			return 1;
		}

		Iterator<Term> mi = terms.iterator();
		Iterator<Term> pi = p_terms.iterator();
		while (mi.hasNext()) {
			Term mt = mi.next();
			Term pt = pi.next();
			int c = mt.compareTo(pt);
			if (c != 0) {
				return c;
			}
		}
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof OtherPolynomial) {
			OtherPolynomial p = (OtherPolynomial) o;
			return p.terms.equals(terms);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return terms.hashCode();
	}

	@Override
	public String toString() {
		if (terms.isEmpty()) {
			return "0";
		}

		String r = "";
		boolean firstTime = true;
		if (terms.size() > 1) {
			r += "(";
		}
		for (Term e : terms) {
			if (!firstTime) {
				if (e.coefficient().compareTo(BigInteger.ZERO) > 0) {
					r += "+";
				}
			}
			boolean ffirstTime = true;

			// yugly.
			if (!e.coefficient().equals(BigInteger.ONE.negate())
					&& (!e.coefficient().equals(BigInteger.ONE))
					|| e.atoms().isEmpty()) {
				firstTime = false;
				r += e.coefficient();
			} else if (e.coefficient().equals(BigInteger.ONE.negate())) {
				firstTime = false;
				r += "-";
			} else if (e.atoms().size() > 0) {
				firstTime = false;
			}
			for (String v : e.atoms()) {
				if (!ffirstTime) {
					r += "*";
				}
				ffirstTime = false;
				r += v;
			}
		}
		if (terms.size() > 1) {
			r += ")";
		}
		return r;
	}

	// ===========================================================
	// TERM
	// ===========================================================

	public final static class Term implements Comparable<Term> {
		public static final Term ZERO = new Term(0);
		public static final Term ONE = new Term(1);

		private final BigInteger coefficient;
		private final List<String> subterms;

		public Term(int coeff, List<String> atoms) {
			coefficient = BigInteger.valueOf(coeff);
			if (coeff != 0) {
				this.subterms = new ArrayList<>(atoms);
				Collections.sort(this.subterms);
			} else {
				this.subterms = new ArrayList<>();
			}
		}

		public Term(int coeff, String... atoms) {
			coefficient = BigInteger.valueOf(coeff);
			this.subterms = new ArrayList<>();
			if (coeff != 0) {
				for (String v : atoms) {
					this.subterms.add(v);
				}
				Collections.sort(this.subterms);
			}
		}

		public Term(BigInteger coeff, List<String> atoms) {
			coefficient = coeff;
			if (!coeff.equals(BigInteger.ZERO)) {
				this.subterms = new ArrayList<>(atoms);
				Collections.sort(this.subterms);
			} else {
				this.subterms = new ArrayList<>();
			}
		}

		public Term(BigInteger coeff, String... atoms) {
			coefficient = coeff;
			this.subterms = new ArrayList<>();
			if (!coeff.equals(BigInteger.ZERO)) {
				for (String v : atoms) {
					this.subterms.add(v);
				}
				Collections.sort(this.subterms);
			}
		}

		public BigInteger coefficient() {
			return coefficient;
		}

		public List<String> atoms() {
			return Collections.unmodifiableList(subterms);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Term)) {
				return false;
			}
			Term e = (Term) o;
			return e.coefficient.equals(coefficient)
					&& e.subterms.equals(subterms);
		}

		@Override
		public String toString() {
			String r = "";

			if (subterms.size() == 0) {
				return coefficient.toString();
			} else if (coefficient.equals(BigInteger.ONE.negate())) {
				r += "-";
			} else if (!coefficient.equals(BigInteger.ONE)) {
				r += coefficient.toString();
			}
			boolean firstTime = true;
			for (String v : subterms) {
				if (!firstTime) {
					r += "*";
				}
				firstTime = false;
				r += v;
			}
			return r;
		}

		@Override
		public int hashCode() {
			return subterms.hashCode();
		}

		@Override
		public int compareTo(Term e) {
			int maxp = maxPower();
			int emaxp = e.maxPower();
			if (maxp > emaxp) {
				return -1;
			} else if (maxp < emaxp) {
				return 1;
			}
			if (subterms.size() < e.subterms.size()) {
				return 1;
			} else if (subterms.size() > e.subterms.size()) {
				return -1;
			}
			if (coefficient.compareTo(e.coefficient) < 0) {
				return 1;
			} else if (coefficient.compareTo(e.coefficient) > 0) {
				return -1;
			}
			for (int i = 0; i < Math.min(subterms.size(), e.subterms.size()); ++i) {
				String v = subterms.get(i);
				String ev = e.subterms.get(i);
				int r = v.compareTo(ev);
				if (r != 0) {
					return r;
				}
			}
			return 0;

		}

		public boolean isConstant() {
			return subterms.isEmpty() || coefficient.equals(BigInteger.ZERO);
		}

		public Term multiply(Term e) {
			ArrayList<String> nvars = new ArrayList<>(subterms);
			nvars.addAll(e.subterms);
			return new Term(coefficient.multiply(e.coefficient), nvars);
		}

		public Pair<Term, Term> divide(Term t) {
			if (subterms.containsAll(t.subterms)
					&& coefficient.compareTo(t.coefficient) >= 0) {
				BigInteger[] ncoeff = coefficient
						.divideAndRemainder(t.coefficient);
				ArrayList<String> nvars = new ArrayList<>(subterms);
				for (String v : t.subterms) {
					nvars.remove(v);
				}
				Term quotient = new Term(ncoeff[0], nvars);
				Term remainder = new Term(ncoeff[1], nvars);
				return new Pair(quotient, remainder);
			} else {
				// no division is possible.
				return new Pair(ZERO, this);
			}
		}

		public Term negate() {
			return new Term(coefficient.negate(), subterms);
		}

		private int maxPower() {
			int max = 0;
			String last = null;
			int cur = 0;
			for (String v : subterms) {
				if (last == null) {
					cur = 1;
					last = v;
				} else if (v.equals(last)) {
					cur = cur + 1;
				} else {
					max = Math.max(max, cur);
					cur = 1;
					last = v;
				}
			}
			return Math.max(max, cur);
		}
	}
}
