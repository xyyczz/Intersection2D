package org.intersection.support;

/**
 * @author zhaijz
 * 2元组
 * @param <A>
 * @param <B>
 */
public class TwoTuple<A, B> {
	public final A first;
	public final B second;
	
	public TwoTuple(A first, B second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof TwoTuple)) {
			return false;
		}
		
		@SuppressWarnings("rawtypes")
		TwoTuple castOther = (TwoTuple) other;
		return this.first.equals(castOther.first) && this.second.equals(castOther.second);
	}

	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("(")
				.append(first)
				.append(", ")
				.append(second)
				.append(")")
				.toString();
	}
}
