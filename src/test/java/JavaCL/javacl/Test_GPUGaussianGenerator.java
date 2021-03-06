package JavaCL.javacl;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.bridj.Pointer;

/**
 * Generated points are written to an csv to be analyzed in
 * MATLAB for accuracy.
 * 
 * @author andingo
 *
 */
public class Test_GPUGaussianGenerator {
	class TestGGG extends GPUGaussianGenerator {

		TestGGG(int vecSize) {
			super(vecSize);
			// TODO Auto-generated constructor stub
		}

		public void setBatchSize(int i) {
			if (i % 2 != 0) i++;
			_batchSize = i;
			_batchSize_2 = i/2;
		}
		
	}

	TestGGG randn;
	BufferedWriter br;

	@Before
	public void setUp() throws Exception {
		randn = new TestGGG(1);
	}

	@Test
	public void test_getUnifs() throws IOException {
		System.out.println("Uniforms:");
		long tmp = System.currentTimeMillis();
		Pointer<Float> unifs = randn._getUnifs();
		System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);

		br = new BufferedWriter(new FileWriter(new File("/Users/andingo/Documents/MATLAB/unifsamples.csv")));
		for (float f : unifs)
			br.write(String.format("%f\n",f));
		
		br.flush();
		br.close();
		System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);
	}
	
	@Test
	public void test_generateGaussians() throws IOException {
		System.out.println("Gaussians:");
		long tmp = System.currentTimeMillis();
		randn._generateGaussians();
		System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);

		br = new BufferedWriter(new FileWriter(new File("/Users/andingo/Documents/MATLAB/normalsamples.csv")));
		for (float f : randn._randn.getFloats())
			br.write(String.format("%f\n",f));
		
		br.flush();
		br.close();
		System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);
	}
	
	@Test
	public void test_getGaussians() {
		System.out.println("Get Gaussians:");
		System.out.println("Generating...");
		long tmp = System.currentTimeMillis();
		randn.getGaussians(randn._batchSize);
		System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);
		
		int N = 1000005;
		for (int i=0; i<2; i++) {
			tmp = System.currentTimeMillis();
			System.out.println("Generating...");
			randn.getGaussians(N);
			System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);
		}
		tmp = System.currentTimeMillis();
		randn.getGaussians(2000000-10);
		System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);
		
		System.out.println("Time non-GPU:");
		tmp = System.currentTimeMillis();
		Random rand = new Random();
		float PI = (float) Math.PI;
		for (int i=0; i<randn._batchSize; i++) {
			float u1 = rand.nextFloat();
			float u2 = rand.nextFloat();
			float two = (float) 2.0;
			float tmp1 = (float) Math.sqrt(-two*Math.log(u1));
			float tmp2 = two*PI*u2;
			float out1 = tmp1*((float) Math.cos(tmp2));
			float out2 = tmp1*((float) Math.sin(tmp2));
		}
		System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);
		System.out.println("End non-GPU");
		
		tmp = System.currentTimeMillis();
		System.out.println("Generating...");
		System.out.println("Generating...");
		float[] fs = randn.getGaussians(2000001);
		System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);

		tmp = System.currentTimeMillis();
		fs = randn.getGaussians(1999999);
		System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);
	}
	
	@Test
	public void test_getGaussians2() throws IOException {
		long tmp = System.currentTimeMillis();
		float[] fs = randn.getGaussians((int)(1.5*randn._batchSize));
		System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);

		br = new BufferedWriter(new FileWriter(new File("/Users/andingo/Documents/MATLAB/normalsamples2.csv")));
		for (float f : fs)
			br.write(String.format("%f\n",f));
		
		br.flush();
		br.close();
		System.out.format("Time elapsed: %,d\n",System.currentTimeMillis()-tmp);
	}
	
	@Test
	public void test_noNaNsorInfs() throws Exception {
		float[] fs;
		int M = 100;
		for (int i=0; i<M; i++) {
			fs = randn.getGaussians(randn._batchSize);
			for (float f : fs) {
				if (Float.isInfinite(f)) {
					throw( new Exception("Infinite"));
				}
				if (Float.isNaN(f)) {
					throw( new Exception("NaN"));
				}
			}
		}
	}

}
