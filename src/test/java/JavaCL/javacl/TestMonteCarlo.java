package JavaCL.javacl;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import montecarlo.*;

public class TestMonteCarlo {
	Payout option;
	StockPath path;
	RandomVectorGenerator rvg;
	float error;
	float prob;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws Exception {
		// Set parameters
		int days = 252;
		float r = .0001f;
		float sigma = .01f;
		float S0 = 152.35f;
		DateTime startDate = DateTime.now();
		DateTime endDate = startDate.plusDays(252);
		
		float ECstrike = 165f;
		float ACstrike = 164f;
		
		// Select error and probability of attaining this error.
		error = .01f;
		prob = .96f;
		
		// Choose an option payout from the OptionFactory
		option = OptionFactory.getEuropeanCall(ECstrike);
		
		// Create an antithetic gaussian vector generator to be put into the GBMPathGenerator
		rvg = new AntiTheticGenerator(new GPUGaussianGenerator(days));

		// Create a Geometric Brownian Motion model based stock path generator
		path = new GBMPathGenerator(r, days, sigma, S0, startDate, endDate, rvg);
		
		// discount factor to get present value of expected payout (aka price)
		float df = (float) Math.exp(-r*days);
		
		// Run the Monte Carlo Simulation
		float price = 0;
		long tmp = System.currentTimeMillis();
		price = MonteCarloManager.run(option, path, error, prob);
		long dur = System.currentTimeMillis() - tmp;
		
		// Discount the payout to get the price
		price *= df;
		
		System.out.println(String.format("time = %,d millis, price = %.4f",dur,price));
		

		option = OptionFactory.getAsianCall(ACstrike);
		
		tmp = System.currentTimeMillis();
		price = MonteCarloManager.run(option, path, error, prob);
		dur = System.currentTimeMillis() - tmp;

		price *= df;
		
		System.out.println(String.format("time = %,d millis, price = %.4f",dur,price));
		
	}

}
