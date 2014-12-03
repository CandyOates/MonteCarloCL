package JavaCL.javacl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import org.bridj.Pointer;

public class Test_GPUGaussianGenerator {
	class TestGGG extends GPUGaussianGenerator {

		public void setBatchSize(int i) {
			if (i % 2 != 0) i++;
			_batchSize = i;
			_batchSize_2 = i/2;
		}
		
	}

	TestGGG randn;

	@Before
	public void setUp() throws Exception {
		randn = new TestGGG();
		randn.setBatchSize(10);
	}

	@Test
	public void test_getUnifs() {
		System.out.println("Uniforms:");
		Pointer<Double> unifs = randn._getUnifs();
		for (double d : unifs.getDoubles()) {
			System.out.println(d);
		}
	}
	
	@Test
	public void test_generateGaussians() {
		System.out.println("Gaussians:");
		randn._generateGaussians();
		for (double d : randn._randn.getDoubles()) {
			System.out.println(d);
		}
	}

}
