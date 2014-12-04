package montecarlo;

/**
 * Interface for the payout of a financial derivative based
 * on a generated stock path.
 * 
 * @author andingo
 * @version 1.0
 */
public interface Payout {
	/**
	 * Generates a stock path and returns the payout of the
	 * derivative on the stock path.
	 * 
	 * @param path a stock path generator
	 * @return the payout of the derivative
	 */
	public float getPayout(StockPath path);
}