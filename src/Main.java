import java.math.BigInteger;
import java.util.List;

import jmodelgen.core.Domain;
import jmodelgen.util.AbstractDomain;
import jmodelgen.util.Domains;
import jsmt.core.Polynomial;
import jsmt.core.Polynomial.Term;

public class Main {

	public static class TermDomain extends AbstractDomain.Binary<Polynomial.Term, Integer, List<Integer>> {

		public TermDomain(Domain<Integer> coefficients, Domain<List<Integer>> variables) {
			super(coefficients, variables);
		}

		@Override
		public Term get(Integer left, List<Integer> right) {
			int[] items = new int[right.size()];
			for (int i = 0; i != items.length; ++i) {
				items[i] = right.get(i);
			}
			if(left <= 0) {
				// avoid zero coefficient
				left = left - 1;
			}
			return new Polynomial.Term(BigInteger.valueOf(left), items);
		}
	}

	public static class PolynomialDomain extends AbstractDomain.Nary<Polynomial, Polynomial.Term> {

		public PolynomialDomain(int max, Domain<Term> generator) {
			super(max, generator);
		}

		@Override
		public Polynomial generate(List<Term> items) {
			return Polynomial.construct(items.toArray(new Term[items.size()]));
		}
	}

	public static void main(String[] args) {
		Domain<Polynomial> domain = new PolynomialDomain(3,new TermDomain(Domains.Int(-2, 2),Domains.List(0, 2, Domains.Int(0, 3))));
		for(int i=0;i!=domain.size();++i) {
			System.out.println("GOT: " + domain.get(i));
		}
	}
}
