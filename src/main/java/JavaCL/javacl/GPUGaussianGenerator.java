package JavaCL.javacl;

import java.util.Random;

import com.nativelibs4java.opencl.*;

import montecarlo.RandomVectorGenerator;

import org.bridj.Pointer;

/**
 * Uses GPUs to generate large batches of Gaussian random numbers and
 * then distribute them as requested.
 * 
 * @author andingo
 * @version 1.0
 */
public class GPUGaussianGenerator implements RandomVectorGenerator {

	protected int _batchSize = 2000000;
	protected int _batchSize_2 = _batchSize/2;
	final protected CLPlatform _plat = JavaCL.listPlatforms()[0];
	final protected String _kernelFile = "/Users/andingo/projects/Java/javacl/src/main/java/JavaCL/javacl/kernels.cl";
	protected KernelReader _kr;
	protected Pointer<Float> _randn;
	protected int _ind = 0;
	protected int _vecSize;

	/**
	 * 
	 * @param vecSize default vector length for {@link RandomVectorGenerator#getVector()}
	 */
	GPUGaussianGenerator(int vecSize) {
		try {
			_kr = new KernelReader(_kernelFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		_vecSize = vecSize;
	}
	
	/**
	 * Generates and stores Gaussians internally.
	 */
	protected void _generateGaussians() {
		_ind = 0;
		try {
			_randn.release();
		} catch (Exception e) {}
		// Get Devices
		CLDevice[] devices = _plat.listGPUDevices(false);
		// Create Context
		CLContext context = JavaCL.createContext(null, devices);
		// Create FIFO queue
		CLQueue queue = context.createDefaultQueue();
		// Build box_muller program
		String src = _kr.getKernel("box_muller");
		CLProgram program = context.createProgram(src);
		program.build();
		// Create box-muller kernel
		CLKernel kernel = program.createKernel("box_muller");
		// Allocate memory and fill with U(0,1) samples for input
		Pointer<Float> unifs1Ptr = _getUnifs();
		Pointer<Float> unifs2Ptr = _getUnifs();
		CLBuffer<Float>
			unifs1 = context.createFloatBuffer(CLMem.Usage.Input, unifs1Ptr),
			unifs2 = context.createFloatBuffer(CLMem.Usage.Input, unifs2Ptr);
		// Create output buffer for N(0,1) samples
		CLBuffer<Float> out = context.createFloatBuffer(CLMem.Usage.Output, _batchSize);
		// set args
		kernel.setArgs(unifs1,unifs2,out,_batchSize_2);
		
		CLEvent event = kernel.enqueueNDRange(queue, new int[]{_batchSize_2});
		
		// retrieve results
		_randn = out.read(queue, event);
		unifs1.release();
		unifs2.release();
		unifs1Ptr.release();
		unifs2Ptr.release();
	}
	
	/**
	 * Generates a batch of uniform random numbers.
	 * @return
	 */
	protected Pointer<Float> _getUnifs() {
		int _batchSize_2 = _batchSize/2;
		Pointer<Float> unifs = Pointer.allocateFloats(_batchSize_2);
		Random rand = new Random();
		for (int i=0; i<_batchSize_2; i++) {
			float f = rand.nextFloat();
			if (f == 0)
				unifs.set((long)i, (float)Math.pow(2.0, -24));
			else
				unifs.set((long)i, f);
		}
		return unifs;
	}
	
	/**
	 * 
	 * @param n number of Gaussians in the vector
	 * @return a vector of i.i.d. Gaussian random numbers
	 */
	public float[] getGaussians(int n) {
		float [] output;
		if (_randn == null) _generateGaussians();
		if (_ind + n <= _batchSize) {
			output = _randn.next(_ind).getFloats(n);
			_ind += n;
			if (_ind == _batchSize) {_randn.release(); _randn = null;}
		} else {
			output = new float [n];

			int ct = 0;
			while (ct < n) {
				if (_ind == _batchSize) _generateGaussians();
				output[ct++] = _randn.get(_ind++);
			}
		}

		return output;
	}

	public float[] getVector() {
		return getGaussians(_vecSize);
	}

}
