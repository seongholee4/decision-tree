import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * An InstanceSet is a set of instances to be used in a machine learning
 * problem, generally as either a training set or a test set.
 * 
 * @author jmac
 * 
 */
public class InstanceSet {
	// All instances in the InstanceSet share the same set of
	// attributes, stored here as an AttributeSet
	private AttributeSet attributeSet = new AttributeSet();

	// An ArrayList containing all instances in the InstanceSet
	private ArrayList<Instance> instances = new ArrayList<Instance>();

	// List of all of the numeric data
	private HashMap<Integer, ArrayList<Double>> numericData = new HashMap<Integer, ArrayList<Double>>();
	// list of indexes of data on line
	private ArrayList<Integer> numericIndexes = new ArrayList<Integer>();

	public static final boolean VERBOSE = false;

	public static final int NUM_BUCKETS = 300;

	/**
	 * 
	 */
	private String[] mostCommonDataValues;

	/**
	 * 
	 */
	private ArrayList<HashMap<String, Integer>> mostCommonDataValuesCounter = new ArrayList<HashMap<String, Integer>>();

	/**
	 * The character used to start comments in .arff files.
	 */
	public static final String commentStart = "%";

	/**
	 * Construct an InstanceSet by reading a .arff file with the given filename
	 * 
	 * @param inputFilename name of the file to read
	 * @throws IOException
	 * @throws DecisionTreeException
	 * @throws IOException
	 * @throws Exception
	 */
	public InstanceSet(String inputFilename) throws DecisionTreeException, IOException {
		parseInputFile(inputFilename);
	}

	/**
	 * Construct an InstanceSet from a list of instances
	 * 
	 * @param attributeSet the set of attributes for this set of instances
	 * @param instances    a list of the instances to be stored in the instance set
	 */
	public InstanceSet(AttributeSet attributeSet, ArrayList<Instance> instances) {
		super();
		this.attributeSet = attributeSet;
		this.instances = instances;
	}

	public void printNumbericDataRanges() {
		for (int i : numericIndexes) {
			ArrayList<Double> arr = numericData.get(i);
			double range = arr.get(arr.size() - 1) - arr.get(0);
			System.out.println(
					"THE RANGE OF FIRST ATTRIBUTE: " + attributeSet.getAttributes().get(i).getName() + " is " + range);
		}
	}

	private void parseInputFile(String inputFilename) throws DecisionTreeException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inputFilename));
		String line = reader.readLine().toLowerCase();
		line = line.toLowerCase();
		while (!parsePreambleLine(line)) {
			line = reader.readLine();
			line = line.toLowerCase();
		}
		attributeSet.setDefaultClassAttribute();

		// Now parse the data
		ArrayList<String> lines = new ArrayList<String>();
		line = reader.readLine();
		while (line.isEmpty())
			line = reader.readLine().toLowerCase();

		// Initialization of global variables
		mostCommonDataValues = new String[line.split("[,\\s]+").length];
		for (int i = 0; i < mostCommonDataValues.length; i++) {
			mostCommonDataValuesCounter.add(new HashMap<String, Integer>());
		}

		while (!line.trim().equals("%")) {
			line = line.toLowerCase();
			lines.add(line);
			line = reader.readLine();
			if (line == null)
				break;
		}
		reader.close();

		// Comment out below loop for Method 1: Purification by Skipping
		for (String l : lines) {
			mostCommonUtility(l);
		}

		for (String l : lines) {
			parseDataLine(l);
		}

		// Sort each list of values for each numeric attribute
		for (int i : numericIndexes) {
			if (VERBOSE)
				System.out.println("INDEX: " + i);
			Collections.sort(numericData.get(i));
		}

		// Replace index with bucket name
		for (int i : numericIndexes) {
			for (Instance instance : instances) {
				Double value = Double.valueOf(instance.getValues()[i]);
				String bucketName = assignBucket(value, i);
				instance.getValues()[i] = bucketName;
			}
		}

	}

	/**
	 * Used in parse input file method Assigns a bucket to a value Makes continuous
	 * data discrete by assigning values to buckets from list of values decide what
	 * value bucket belongs to
	 * 
	 * @param val
	 * @param numericIndex
	 * @return
	 */
	private String assignBucket(double val, int numericIndex) {
		ArrayList<Double> arr = numericData.get(numericIndex);
		int i = (arr.size()) / NUM_BUCKETS;
		for (int j = 1; j < NUM_BUCKETS; j++) {
			if (val < arr.get(i * j)) {
				return String.valueOf(j);
			}
		}
		return String.valueOf(NUM_BUCKETS);
	}

	private boolean shouldIgnoreLine(String line) {
		if (line.equals(""))
			return true;
		else if (line.startsWith(commentStart))
			return true;
		else if (line.startsWith("@relation"))
			return true;

		// Uncomment for Method 1: Purification by Skipping

		// else if (line.contains("?")) {
		// return true;
		// }

		else
			return false;
	}

	// Return true if this line is the start of the data segment,
	// otherwise return false
	private boolean parsePreambleLine(String line) throws DecisionTreeException {
		if (shouldIgnoreLine(line))
			return false;
		else if (line.startsWith("@attribute")) {
			parseAttribute(line);
			return false;
		} else if (line.startsWith("@data"))
			return true;
		else
			throw new DecisionTreeException("unexpected line: " + line);
	}

	private void parseAttribute(String line) {
		// split on whitespace to extract attribute name

		String[] split_line = line.split("\\s+");
		String attribute_name = split_line[1];
		// Extract the part in braces, which is the list of possible
		// attribute values.
		// To keep things simple, we will split on all brace
		// characters, thus assuming that the only place either
		// brace character appears is at the start and end of the
		// attribute value list
		if (VERBOSE)
			System.out.println("ATTRIBUTE NAME: " + attribute_name);

		// Construct the attribute and add it to the attribute set
		if (!line.contains("continuous")) {

			split_line = line.split("[{}]");
			String value_list = split_line[1];

			// To get the actual values out of value_list, split on commas
			// and whitespace
			String[] values = value_list.split("[,\\s]+");
			Attribute attribute = new Attribute(attribute_name, values);
			this.attributeSet.addAttribute(attribute);
		} else {
			String[] values = new String[NUM_BUCKETS];
			for (int i = 0; i < NUM_BUCKETS; i++) {
				values[i] = String.valueOf(i + 1);
			}

			Attribute attribute = new Attribute(attribute_name, values);
			this.attributeSet.addAttribute(attribute);
			numericIndexes.add(attributeSet.getAttributeIndex(attribute));

		}

	}

	private void parseDataLine(String line) {
		if (shouldIgnoreLine(line))
			return;

		// Comment out if statement for Method 1: Purification by Skipping

		if (line.contains("?")) {
			line = replaceUnknownValue(line);
		}

		// Line of data values should be comma-separated, so split on
		// commas and whitespace
		String[] values = line.split("[,\\s]+");

		//Add list of values in numeric data
		for (int index : numericIndexes) {
			double myval = Double.parseDouble(values[index]);
			if (numericData.get(index) != null) {
				numericData.get(index).add(myval);
			} else {
				ArrayList<Double> cur = new ArrayList<Double>();
				cur.add(myval);
				numericData.put(index, cur);

			}
		}

		Instance instance = new Instance(values);
		this.instances.add(instance);
	}

	/**
	 * Get the set of attributes used by all the instances in this instance set.
	 * 
	 * @return the attributeSet
	 */
	public AttributeSet getAttributeSet() {
		return attributeSet;
	}

	/**
	 * Get a list of all instances in this instance set.
	 * 
	 * @return the instances
	 */
	public ArrayList<Instance> getInstances() {
		return instances;
	}

	/**
	 * Get the number of instances in this instance set.
	 * 
	 * @return the number of instances in this set of instances
	 */
	public int getNumInstances() {
		return instances.size();
	}

	/**
	 * Checks if line contains missing data
	 * 
	 * @param line
	 * 
	 * @return True/False
	 */
	public boolean containsMissingData(String line) {
		if (line.contains("?"))
			return true;
		else
			return false;
	}

	/**
	 * Replaces all of the missing data in a given line based off of the pre-built,
	 * most common data values.
	 * 
	 * @return Updated line
	 */
	private String replaceUnknownValue(String line) {
		String[] values = line.split("[,\\s]+");
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals("?"))
				line = line.replaceFirst("\\?", mostCommonDataValues[i]);
		}
		return line;
	}

	/**
	 * Updates the most common data values based on the given lines of data
	 * 
	 * @param line
	 * 
	 */
	private void mostCommonUtility(String line) {
		String[] values = line.split("[,\\s]+");
		for (int position = 0; position < values.length; position++) {
			if (mostCommonDataValuesCounter.get(position).containsKey(values[position]))
				mostCommonDataValuesCounter.get(position).put(values[position],
						mostCommonDataValuesCounter.get(position).get(values[position]) + 1);
			else
				mostCommonDataValuesCounter.get(position).put(values[position], 1);

			// Updates most common data value
			int max = -1;
			Integer[] populusValues = mostCommonDataValuesCounter.get(position).values()
					.toArray(new Integer[mostCommonDataValuesCounter.get(position).values().size()]);

			for (int i = 0; i < populusValues.length; i++) {
				if (populusValues[i] > max && !values[position].equals("?")) {
					max = populusValues[i];
					mostCommonDataValues[position] = values[position];
				}
			}
		}
	}
}