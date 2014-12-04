package montecarlo;

import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.joda.time.DateTime;

/**
 * Interface for a stock path generator.
 * 
 * @author andingo
 * @version 1.0
 */
public interface StockPath {
	public List<Pair<DateTime, Float>> getPrices();
}
