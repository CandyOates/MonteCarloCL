package montecarlo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.joda.time.DateTime;

/**
 * Provides an API for generating Geometric Brownian Motion
 * based stock paths.
 * 
 * @author andingo
 * @version 1.0
 * @see StockPath
 */
public class GBMPathGenerator implements StockPath {

	private float rate;
	private float sigma;
	private float S0;
	private int N;
	private DateTime startDate;
	private DateTime endDate;
	private RandomVectorGenerator rvg;
	
	/**
	 * Creates an object that generates stock paths based on the geometric
	 * brownian motion model.
	 * 
	 * @param rate risk free rate
	 * @param N number of time steps
	 * @param sigma volatility
	 * @param S0 starting price
	 * @param startDate starting date
	 * @param endDate ending date
	 * @param rvg a random vector generator
	 * @throws Exception invalid inputs
	 */
	public GBMPathGenerator(float rate, int N,
			float sigma, float S0,
			DateTime startDate, DateTime endDate,
			RandomVectorGenerator rvg) throws Exception {
		
		if (N <= 0) throw ( new Exception ("N must be positive"));
		if (S0 <= 0) throw ( new Exception ("S0 must be positive"));
		if (startDate == null) throw ( new Exception ("startDate is null"));
		if (endDate == null) throw ( new Exception ("endDate is null"));
		if (rvg == null) throw ( new Exception ("rvg is null"));
		
		this.startDate = startDate;
		this.endDate = endDate;
		this.rate = rate;
		this.S0 = S0;
		this.sigma = sigma;
		this.N = N;
		this.rvg = rvg;

	}

	public List<Pair<DateTime, Float>> getPrices() {
		DateTime iter = new DateTime(startDate.getMillis());
		long delta = (endDate.getMillis() - startDate.getMillis())/N;

//		List<Pair<DateTime, Float>> path = new LinkedList<Pair<DateTime,Float>>();
		List<Pair<DateTime, Float>> path = new ArrayList<Pair<DateTime,Float>>(N);
		path.add(new Pair<DateTime, Float>(iter , S0));

		float[] randVec = rvg.getVector();

		float last = path.get(0).getSecond();
		float constant = rate-sigma*sigma/2;
		for ( int i=1; i < N; ++i){
			iter = iter.plusMillis((int) delta);
			float val = last*( (float)Math.exp(constant + sigma * randVec[i-1]));
			last = val;
			path.add(new Pair<DateTime, Float>(iter, val));
		}
		return path;
	}
	
}
