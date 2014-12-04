package montecarlo;

/**
 * API for keeping running track of the mean and standard deviation
 * of scalar inputs.
 * 
 * @author andingo
 * @version 1.0
 */
public class StatisticsHandler {
	private float xbar = 0.0f;
	private float x2bar = 0.0f;
	private int n = 0;
	
	/**
	 * @return the number of data points included in the mean
	 */
	public int getN() {
		return n;
	}

	/**
	 * @return the mean
	 */
	public float getMean() {
		return xbar;
	}

	/**
	 * @return the variance
	 */
	public float getVar() {
		if (n == 1 || n == 0) {
			return 0;
		} else {
			return (x2bar - xbar*xbar)*n/(n-1);
		}
	}
	
	/**
	 * @return the standard deviation
	 */
	public float getStdDev() {
		return (float) Math.sqrt(getVar());
	}

	/**
	 * Add a value to the running mean/standard deviation.
	 * @param val
	 */
	public void addValue(float val) {
		++n;
		x2bar = (x2bar*(n-1) + val*val) / n;
		xbar = (xbar*(n-1) + val)/n;
	}

}
