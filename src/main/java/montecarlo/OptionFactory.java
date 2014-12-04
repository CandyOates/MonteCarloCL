package montecarlo;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.joda.time.DateTime;

/**
 * A Factory class with static methods returning PayOut implementations for various types
 * of options.
 * 
 * @author andingo
 * @version 1.0
 */
public class OptionFactory {

	/**
	 * Returns a European Call Option with strike price K.
	 * 
	 * @param K strike price ( > 0)
	 * @return Payout object with European Call Option payout
	 * @throws Exception
	 */
	public static Payout getEuropeanCall(final float K) throws Exception {
		if (K <= 0) throw ( new Exception ("K must be positive"));
		return new Payout() {
			public float getPayout(StockPath path) {
				List<Pair<DateTime,Float>> p = path.getPrices();
				return Math.max(p.get(p.size()-1).getSecond() - K, 0.0f);
			}
		};
	}

	/**
	 * Returns a European Put Option with strike price K.
	 * 
	 * @param K strike price ( > 0)
	 * @return Payout object with European Put Option payout
	 * @throws Exception
	 */
	public static Payout getEuropeanPut(final float K) throws Exception {
		if (K <= 0) throw ( new Exception ("K must be positive"));
		return new Payout() {
			public float getPayout(StockPath path) {
				List<Pair<DateTime,Float>> p = path.getPrices();
				return Math.max(K - p.get(p.size()-1).getSecond(), 0.0f);
			}
		};
	}

	/**
	 * Returns an Asian Call Option with strike price K.
	 * 
	 * @param K
	 * @return
	 * @throws Exception
	 */
	public static Payout getAsianCall(final float K) throws Exception {
		if (K <= 0) throw ( new Exception ("K must be positive"));
		return new Payout() {
			public float getPayout(StockPath path) {
				List<Pair<DateTime,Float>> p = path.getPrices();
				float sum = 0.0f;
				Iterator<Pair<DateTime,Float>> iter = p.iterator();
				while (iter.hasNext()) {
					sum += iter.next().getSecond();
				}
				sum /= p.size();
				return Math.max(sum - K, 0.0f);
			}
		};
	}

	/**
	 * Returns an Asian Put Option with strike price K.
	 * 
	 * @param K
	 * @return
	 * @throws Exception
	 */
	public static Payout getAsianPut(final float K) throws Exception {
		if (K <= 0) throw ( new Exception ("K must be positive"));
		return new Payout() {
			public float getPayout(StockPath path) {
				List<Pair<DateTime,Float>> p = path.getPrices();
				float sum = 0.0f;
				Iterator<Pair<DateTime,Float>> iter = p.iterator();
				while (iter.hasNext()) {
					sum += iter.next().getSecond();
				}
				sum /= p.size();
				return Math.max(K - sum, 0.0f);
			}
		};
	}
	
	protected OptionFactory() {}

}
