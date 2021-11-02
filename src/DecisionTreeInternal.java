import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents an internal node in a decision tree.
 * 
 * @author jmac
 */
public class DecisionTreeInternal extends DecisionTree {

	// A map consisting of the children of this internal node in the decision
	// tree. The key is a possible value of this node's split attribute, and the
	// corresponding value is a DecisionTree for classifying instances that
	// agree with the key. For example, if this node's split attribute is
	// "color", and the possible values of the attribute include "red", then the
	// key "red" maps to a DecisionTree for instances whose "color" is "red".
	HashMap<String, DecisionTree> children;

	// The attribute on which this internal node splits its instances. In the
	// conventional way of drawing decision trees, the node would also be
	// labeled with this attribute. See figure 18.6 of Russell and Norvig for an
	// example.
	Attribute splitAttribute;

	public static final boolean VERBOSE = false;

	/**
	 * This protected constructor cannot be called by external code; decision trees
	 * should be constructed using the constructDecisionTree factory method in the
	 * DecisionTree class.
	 * 
	 * @param examples   The examples from which this tree should be learned.
	 * @param attributes A list of attributes on which this tree is permitted to
	 *                   make decisions.
	 * @param label      The label on the edge leading to this DecisionTree node, or
	 *                   <code>DecisionTree.ROOT_LABEL</code> for the root.
	 * @param depth      The depth of this node in the full decision tree.
	 * @throws DecisionTreeException
	 */
	@SuppressWarnings("unchecked")
	protected DecisionTreeInternal(InstanceSet examples, ArrayList<Attribute> attributes, String label, int depth)
			throws DecisionTreeException {
		super(label, depth);
		assert attributes.size() > 0;

		// compute and store the split attribute
		splitAttribute = getSplitAttribute(examples, attributes);

		// Make a list of valid attributes for child nodes, which consists of
		// all the valid attributes for this node except the split attribute.
		ArrayList<Attribute> childAttributes = (ArrayList<Attribute>) attributes.clone();
		childAttributes.remove(splitAttribute);

		// compute the children of this node, using recursion
		children = makeChildren(examples, childAttributes);
	}

	/**
	 * Compute the attribute on which this internal node will split its instances,
	 * using the criterion of maximum information gain.
	 * 
	 * @param examples   A set of instances that will be used to determine the split
	 *                   attribute.
	 * @param attributes A list of attributes that are valid candidates for the
	 *                   split attribute.
	 * @return The chosen split attribute.
	 * @throws DecisionTreeException
	 */
	private Attribute getSplitAttribute(InstanceSet examples, ArrayList<Attribute> attributes)
			throws DecisionTreeException {
		// TODO: fill in the body of this method and fix the return statement
		// HINT: the expectedEntropy() method will be needed
		double minEntropy = Integer.MAX_VALUE;
		Attribute minAt = null;
		for (Attribute at : attributes) {
			double curr = expectedEntropy(at, examples);
			if (curr < minEntropy) {
				minEntropy = curr;
				minAt = at;
			}
		}
		return minAt;
	}

	/**
	 * Compute the matching instances from a given set of instances, where a match
	 * is determined by having a given value on a given attribute. For example, we
	 * might return all instances that have the value "red" on the attribute
	 * "color".
	 * 
	 * @param attribute The attribute to which matching will apply (e.g. "color").
	 * @param value     The value of the attribute which is considered a match (e.g.
	 *                  "red").
	 * @param examples  The set of instances from which matches will be found.
	 * @return A set of all matching instances.
	 */
	private InstanceSet getMatches(Attribute attribute, String value, InstanceSet examples) {
		ArrayList<Instance> matches = new ArrayList<Instance>();

		// find out the index of the attribute to be matched
		AttributeSet attributes = examples.getAttributeSet();
		int attributeIndex = attributes.getAttributeIndex(attribute);

		if(VERBOSE) {
			System.out.println("VALUE OF INSTANCEs in ATTRIBUTE " + attribute.getName() + " is:");
		}
		// Loop through the examples, looking for matching instances and adding
		// them to our list of matches
		for (Instance instance : examples.getInstances()) {
			String instanceValue = instance.getValues()[attributeIndex];
			if(VERBOSE) {
				System.out.println(" " + instanceValue);
				System.out.println("VALUE: " + value);
			}
			if (instanceValue.equals(value)) {
				
				matches.add(instance);
			}
		}

		return new InstanceSet(examples.getAttributeSet(), matches);
	}

	/**
	 * Create and compute the children of this node.
	 * 
	 * @param examples   A list of all training examples provided to this node
	 * @param attributes A list of attributes valid for children of this node
	 * @return A map consisting of the children of this internal node in the
	 *         decision tree. The key is a possible value of this node's split
	 *         attribute, and the corresponding value is a DecisionTree for
	 *         classifying instances that agree with the key. For example, if this
	 *         node's split attribute is "color", and the possible values of the
	 *         attribute include "red", then the key "red" maps to a DecisionTree
	 *         for instances whose "color" is "red".
	 * @throws DecisionTreeException
	 */
	private HashMap<String, DecisionTree> makeChildren(InstanceSet examples, ArrayList<Attribute> attributes)
			throws DecisionTreeException {
		// TODO: fill in the body of this method and fix the return statement
		// HINT: the getMatches() method will be useful

		HashMap<String, DecisionTree> children = new HashMap<>();

		for (String st : splitAttribute.getValues()) {
			InstanceSet in = getMatches(splitAttribute, st, examples);
			DecisionTree dt = DecisionTree.constructDecisionTree(in, attributes, examples, st, depth);
			children.put(st, dt);
		}

		return children;
	}

	/**
	 * Compute the expected entropy of the given attribute, based on the given
	 * examples.
	 * 
	 * @param attribute The attribute whose expected entropy will be computed.
	 * @param examples  The examples used to compute the expected entropy of the
	 *                  attribute.
	 * @return The expected entropy of the given attribute.
	 * @throws DecisionTreeException
	 */
	private double expectedEntropy(Attribute attribute, InstanceSet examples) throws DecisionTreeException {
		// TODO: fill in the body of this method and fix the return statement
		// HINT: use the Distribution class

		String[] Sd = attribute.getValues(); // get list of names of the attribute values
		double entropy = 0.0;
		Distribution d = new Distribution(attribute);
		d.computeProbabilitiesFromFrequencies();
		if (VERBOSE) {
			for(int i =0; i< Sd.length; i++) {
				System.out.println("ATTRIBUTE VALUES: " + Sd[i]);
			}
			
		}
		
		for (int i = 0; i < Sd.length; i++) { // Loop through the attributes
			InstanceSet curr = getMatches(attribute, Sd[i], examples); // get a list of instances that have this value
			int Nd = curr.getNumInstances(); // get number of instances in that list
			int PId = Nd / Sd.length; // get the probability of current value

			// Get the entropy of the distribution of classes
			double Hd = d.getEntropy();

			entropy += PId * Hd;

		}

		if (VERBOSE) {
			System.out.print("Expected ENTROPY: " + entropy);
		}

		return entropy;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see DecisionTree#decide(AttributeSet, Instance)
	 */
	@Override
	public String decide(AttributeSet attributes, Instance instance) {
		// TODO: fill in the body of this method and fix the return statement
		// HINT: use the Distribution class
		int index = attributes.getAttributeIndex(splitAttribute);
		
		String atVal = instance.getValues()[index];
		
		return children.get(atVal).decide(attributes, instance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see DecisionTree#print()
	 */
	@Override
	public void print() {
		super.print();
		System.out.println("[attribute " + splitAttribute.getName() + "]");
		for (DecisionTree child : children.values()) {
			child.print();
		}
	}
}
