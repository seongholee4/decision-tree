/**
 * An Instance object represents a single instance in a training or test set
 * 
 * @author John MacCormick
 * 
 */
public class Instance {
	// An array of the attribute values for this instance.
	// Note that the order of elements in this array is determined by
	// the ordering of the attributes in the AttributeSet being used
	// for this machine learning problem.
	private String[] values;

	/**
	 * Create a new instance with the given values.
	 * 
	 * @param values
	 *            an array of strings naming the values this instance takes. The
	 *            ordering of this array is the same as the AttributeSet being
	 *            used for the current machine learning problem.
	 */
	public Instance(String[] values) {
		this.values = values;
	}

	/**
	 * print the instance in a legible format
	 */
	public void print() {
		StringBuilder builder = new StringBuilder();
		for (String value : values)
			builder.append(value + ", ");
		// remove the final ", "
		builder.delete(builder.length() - 2, builder.length());
		System.out.println(builder);
	}

	/**
	 * Get an array listing the values of this instance, listed in the same
	 * order as the AttributeSet for the current machine learning problem.
	 * 
	 * @return the values
	 */
	public String[] getValues() {
		return values;
	}
}