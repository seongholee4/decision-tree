import java.util.HashMap;
import java.lang.Math;

/**
 * For a given attribute, a Distribution object stores information about a
 * probability distribution over the possible attribute values in two different
 * ways: (i) frequencies -- how many times each attribute value was observed,
 * and (ii) probabilities -- the probability that this attribute has each of the
 * possible attribute values. A method is provided for translating frequencies
 * into normalized probabilities.
 * 
 * @author jmac
 * 
 */
public class Distribution {
	// Key is the attribute value, value is the
	// number of instances for which this attribute value was
	// observed
	private HashMap<String, Integer> frequencies = new HashMap<String, Integer>();

	// Key is the name of the attribute value, value is the
	// probability of this attribute value
	private HashMap<String, Double> probabilities = new HashMap<String, Double>();

	// true if the probabilities are valid, false otherwise.
	private boolean probabilitiesAreValid;

	private static final String invalidProbabilitiesMsg = "Probabilities are not valid.";

	/**
	 * Create a distribution on the values of a particular attribute
	 * 
	 * @param attribute
	 *            the attribute whose distribution this object records
	 */
	public Distribution(Attribute attribute) {
		// Initialize frequency and probability of every attribute value to 0
		for (String value : attribute.getValues()) {
			frequencies.put(value, 0);
			probabilities.put(value, 0.0);
		}
		probabilitiesAreValid = false;
	}

	/**
	 * Increment the observed frequency of the given value by 1.
	 * 
	 * @param attributeValue
	 *            the attribute value whose frequency will be incremented by 1
	 */
	public void incrementFrequency(String attributeValue) {
		int new_value = frequencies.get(attributeValue) + 1;
		frequencies.put(attributeValue, new_value);
		probabilitiesAreValid = false;
	}

	/**
	 * Get the name of the most frequently-observed attribute value.
	 * 
	 * @return the name of the most frequent attribute value
	 */
	public String getNameOfMaxFrequency() {
		int max = 0;
		String max_name = null;
		for (String attributeValue : frequencies.keySet()) {
			int frequency = frequencies.get(attributeValue);
			if (frequency >= max) {
				max = frequency;
				max_name = attributeValue;
			}
		}
		return max_name;
	}

	/**
	 * Get the number of times the most frequently-observed value was observed.
	 * 
	 * @return the frequency of the most frequent attribute value
	 */
	public int getValueOfMaxFrequency() {
		return frequencies.get(getNameOfMaxFrequency());
	}

	/**
	 * Get the name of the most probable attribute value.
	 * 
	 * @return the name of the most probable attribute value
	 * @throws DecisionTreeException
	 *             if the probabilities have not been computed via
	 *             <code>computeProbabilitiesFromFrequencies()</code>
	 */
	public String getNameOfMaxProbability() throws DecisionTreeException {
		if (!probabilitiesAreValid) {
			throw new DecisionTreeException(invalidProbabilitiesMsg);
		}
		double max = 0.0;
		String max_name = null;
		for (String attributeValue : probabilities.keySet()) {
			double probability = probabilities.get(attributeValue);
			if (probability >= max) {
				max = probability;
				max_name = attributeValue;
			}
		}
		assert max_name != null;
		return max_name;
	}

	/**
	 * Get the probability of the most probable attribute value.
	 * 
	 * @return the probability of the most probable attribute value
	 * @throws DecisionTreeException
	 *             if the probabilities have not been computed via
	 *             <code>computeProbabilitiesFromFrequencies()</code>
	 */
	public double getValueOfMaxProbability() throws DecisionTreeException {
		if (!probabilitiesAreValid) {
			throw new DecisionTreeException(invalidProbabilitiesMsg);
		}
		return probabilities.get(getNameOfMaxProbability());
	}

	/**
	 * Get the total number of observations.
	 * 
	 * @return the total of all frequencies in this distribution (i.e. the total
	 *         number of instances observed)
	 */
	public int getTotalFrequencies() {
		int total = 0;
		for (String attributeValue : frequencies.keySet()) {
			int frequency = frequencies.get(attributeValue);
			total += frequency;
		}
		return total;
	}

	/**
	 * Get the entropy of the probability distribution.
	 * 
	 * @return the entropy of the probability distribution
	 * @throws DecisionTreeException
	 *             if the probabilities have not been computed via
	 *             <code>computeProbabilitiesFromFrequencies()</code>
	 */
	public double getEntropy() throws DecisionTreeException {
		if (!probabilitiesAreValid) {
			throw new DecisionTreeException(invalidProbabilitiesMsg);
		}
		double entropy = 0.0;
		for (double probability : probabilities.values()) {
			double this_term;
			if (probability == 0.0)
				this_term = 0.0;
			else
				this_term = -probability * Math.log(probability) / Math.log(2);

			entropy += this_term;
		}
		return entropy;
	}

	/**
	 * Take the observed frequencies and create normalized probabilities from
	 * them, which can later be obtained using getProbabilities().
	 * 
	 * @throws DecisionTreeException
	 */
	public void computeProbabilitiesFromFrequencies()
			throws DecisionTreeException {
		probabilitiesAreValid = true;
		int total_frequency = getTotalFrequencies();
		if (total_frequency == 0) {
			return;
		}
		for (String attributeValue : probabilities.keySet()) {
			double probability = (double) frequencies.get(attributeValue)
					/ total_frequency;
			probabilities.put(attributeValue, probability);
		}
		probabilitiesAreValid = true;
	}

	/**
	 * Print out the table of observed frequencies.
	 */
	public void printFrequencies() {
		for (String attributeValue : frequencies.keySet()) {
			System.out.print(" " + attributeValue + ": "
					+ frequencies.get(attributeValue));
		}
		System.out.println();
	}

	/**
	 * Print out the table of probabilities.
	 * 
	 * @throws DecisionTreeException
	 */
	public void printProbabilities() throws DecisionTreeException {
		if (!probabilitiesAreValid) {
			throw new DecisionTreeException(invalidProbabilitiesMsg);
		}
		for (String attributeValue : probabilities.keySet()) {
			System.out.print(" " + attributeValue + ": "
					+ probabilities.get(attributeValue));
		}
		System.out.println();
	}

	/**
	 * Get a HashMap of the probabilities in this distribution.
	 * 
	 * @return a HashMap of the probabilities; the key is the attribute value,
	 *         and the corresponding value is its probability
	 * @throws DecisionTreeException
	 */
	public HashMap<String, Double> getProbabilities()
			throws DecisionTreeException {
		if (!probabilitiesAreValid) {
			throw new DecisionTreeException(invalidProbabilitiesMsg);
		}
		return probabilities;
	}

}