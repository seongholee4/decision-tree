/**
 * A class describing an attribute in a machine learning problem: in particular,
 * the name of the attribute and the values it can take
 * 
 * @author John MacCormick
 */
public class Attribute {
	private String name; // The name of the attribute
	private String[] values; // The possible values this attribute can take
	

	/**
	 * Construct a new Attribute with the given name and values.
	 * @param name the name of the attribute
	 * @param values the values this attribute can take
	 */
	public Attribute(String name,  String[] values) {
		this.name = name;
		this.values = values;
	}

	/**
	 * print a description of the attribute
	 */
	public void print() {
		StringBuilder builder = new StringBuilder();
		builder.append(getName() + ": ");
		for (String value : values)
			builder.append(value + " ");
		System.out.println(builder);
	}

	/**
	 * Get the name of the attribute.
	 * @return the name of the attribute
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get an array listing the values this attribute can take.
	 * 
	 * @return the values this attribute can take
	 */
	public String[] getValues() {
		return values;
	}



}