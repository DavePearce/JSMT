package jsmt.core;

public class StaticRange extends Constraint {
	public static final StaticRange UNLIMITED = new StaticRange(Integer.MIN_VALUE + 1, Integer.MAX_VALUE);

	private final int lowerBound;
	private final int upperBound;

	public StaticRange(int lower, int upper) {
		this.lowerBound = lower;
		this.upperBound = upper;
	}

	@Override
	public int lowerBound(int[] vars) {
		return lowerBound;
	}

	@Override
	public int upperBound(int[] vars) {
		return upperBound;
	}
}
