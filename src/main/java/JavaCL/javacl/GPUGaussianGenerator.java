package JavaCL.javacl;

import java.util.Random;

import com.nativelibs4java.opencl.*;

import org.bridj.Pointer;

public class GPUGaussianGenerator implements RandomVectorGenerator {

	protected int _batchSize = 2000000;
	protected int _batchSize_2 = _batchSize/2;
	final protected CLPlatform _plat = JavaCL.listPlatforms()[0];
	final protected String _kernelFile = "/Users/andingo/projects/Java/javacl/src/main/java/JavaCL/javacl/kernels.cl";
	protected KernelReader _kr;
	protected Pointer<Float> _randn;
	protected int _ind = 0;
	protected int _vecSize;

	GPUGaussianGenerator(int vecSize) {
		try {
			_kr = new KernelReader(_kernelFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		_vecSize = vecSize;
	}
	
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
	
	protected Pointer<Float> _getUnifs() {
		int _batchSize_2 = _batchSize/2;
		Pointer<Float> unifs = Pointer.allocateFloats(_batchSize_2);
		Random rand = new Random();
		for (int i=0; i<_batchSize_2; i++) {
			unifs.set((long)i, rand.nextFloat());
		}
		return unifs;
	}
	
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
