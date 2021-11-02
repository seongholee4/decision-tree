import java.util.ArrayList;
import java.util.HashMap;

/**
 * An AttributeSet object encapsulates the set of attributes for a given machine
 * learning problem. The attributes are stored in a fixed order, and several of
 * the methods access attributes by their index in this ordering. As is usual
 * with machine learning problems, one of the attributes is designated as a
 * <i>class attribute</i> -- this is the attribute that determines which class
 * an instance belongs to. However, to avoid confusion with the Java keyword
 * "class", in all documentation we refer instead to the <i>classification
 * attribute</i>, and the <i>classification</i> of an instance.
 * 
 * @author John MacCormick
 */
public class AttributeSet {
	// A list of the attributes in this AttributeSet; this list fixes the order
	// of the attributes.
	private ArrayList<Attribute> attributes;

	// Key is attribute name, value is the index of this attribute in
	// the ArrayList this.attributes
	private HashMap<String, Integer> attributeIndices;

	// The name of the attribute that determines classification of an instance
	private String classAttribute;

	// Index of the classAttribute in the ArrayList this.attributes
	private int classAttributeIndex;

	/**
	 * Construct a new AttributeSet with an empty set of attributes.
	 */
	public AttributeSet() {
		attributes = new ArrayList<Attribute>();
		attributeIndices = new HashMap<String, Integer>();
	}

	/**
	 * Add the given attribute to the attribute set.
	 * 
	 * @param attribute
	 *            the attribute to be added to this attribute set
	 */
	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
		attributeIndices.put(attribute.getName(), attributes.size() - 1);
	}

	/**
	 * Set the classification attribute to the attribute with the given name
	 * 
	 * @param name
	 *            a string naming the classification attribute
	 */
	public void setClassAttribute(String name) {
		classAttribute = name;
		classAttributeIndex = getAttributeIndex(classAttribute);
	}

	/**
	 * Get the index of the given attribute in the attribute set's ordering.
	 * 
	 * @param attribute
	 *            the attribute whose index is desired
	 * @return the attribute's index in this class's array list
	 */
	public int getAttributeIndex(Attribute attribute) {
		return attributeIndices.get(attribute.getName());
	}

	/**
	 * Get the index of the given attribute in the attribute set's ordering.
	 * 
	 * @param name
	 *            the name of the attribute whose index is desired
	 * @return the attribute's index in this class's array list
	 */
	public int getAttributeIndex(String name) {
		return attributeIndices.get(name);
	}

	/**
	 * Get the attribute with the given name.
	 * 
	 * @param name
	 *            the name of the desired attribute
	 * @return the attribute corresponding to the given name
	 */
	public Attribute getAttribute(String name) {
		return attributes.get(getAttributeIndex(name));
	}

	/**
	 * Get the classification attribute for this attribute set.
	 * 
	 * @return the classification attribute for this attribute set
	 */
	public Attribute getClassAttribute() {
		return attributes.get(classAttributeIndex);
	}

	/**
	 * Get the name of the classification attribute for this attribute set.
	 * 
	 * @return the name of the classification attribute for this attribute set
	 */
	public String getClassAttributeAsString() {
		return classAttribute;
	}

	/**
	 * The classification attribute is the final attribute, by default; this
	 * method is used to set this default.
	 */
	public void setDefaultClassAttribute() {
		setClassAttribute(attributes.get(attributes.size() - 1).getName());
	}

	/**
	 * Print a legible version of the attribute set.
	 */
	public void print() {
		for (Attribute attribute : attributes)
			attribute.print();
		System.out.println("Class attribute is " + classAttribute);
	}

	/**
	 * Get a list of attributes in the attribute set. Attributes are returned in
	 * their fixed ordering defined by the attribute set.
	 * 
	 * @return the attributes in the attribute set
	 */
	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * Get the index of the classification attribute in this attribute set's
	 * ordering of attributes
	 * 
	 * @return the index of the classification attribute in this attribute set's
	 *         ordering of attributes
	 */
	public int getClassAttributeIndex() {
		return classAttributeIndex;
	}

}