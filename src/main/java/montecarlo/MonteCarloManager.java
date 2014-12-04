package montecarlo;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Provides an API for running accuracy specified Monte Carlo simulations
 * on stock path dependent payout functions.
 * 
 * @author andingo
 * @version 1.0
 */
public class MonteCarloManager {
	/**
	 * Runs a Monte Carlo option pricing simulation.
	 * 
	 * @param option the option payout class
	 * @param path the stock path generator
	 * @param error the deviation from the true solution
	 * @param prob the probability of having a solution within 'error' of the true solution
	 * @param discountFactor the discounting factor from the terminal time to current time
	 * @return the estimated price
	 * @throws Exception invalid input
	 */
	public static float run(Payout option, StockPath path,
			float error, float prob) throws Exception {
		if (option == null) throw ( new Exception ("option is null"));
		if (path == null) throw ( new Exception ("path is null"));
		if (error <= 0) throw (new Exception ("error must be positive"));
		if (prob <= 0) throw (new Exception ("prob must be positive"));
		if (prob >= 1) throw (new Exception ("prob must be less than 1"));
		
		StatisticsHandler stats = new StatisticsHandler();
		NormalDistribution norm = new NormalDistribution();
		
		float y = (float) Math.pow(norm.inverseCumulativeProbability( (1-prob)/2 ) / error, 2);
		
		int N = 100000;
		int n = 0;
		long tmp = System.currentTimeMillis();
		for (int i=0; i<N; i++) {
			stats.addValue(option.getPayout(path));
			n = (int) (y * stats.getVar());
		}
		long dur = System.currentTimeMillis() - tmp;
		
		System.out.println(String.format("Mean: %.5f\tVar: %.5f\tN: %d", stats.getMean(), stats.getVar(), n));
		
		if (n > N) {
			System.out.println( String.format("Estimated Run Time: %,d millis", dur*((long)(n-N))/((long)N) ) );
			for (int i=0; i<n-N; i++) {
				stats.addValue(option.getPayout(path));
			}
		}
		
		return stats.getMean();
	}
}
