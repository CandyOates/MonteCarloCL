package montecarlo;

/**
 * Interface for a class which returns vectors of samples from
 * random variables.
 * 
 * @author andingo
 * @version 1.0
 */
public interface RandomVectorGenerator {
	/**
	 * @return an array of independent samples
	 */
	public float[] getVector();
}
