package JavaCL.javacl;

import java.util.Random;

import com.nativelibs4java.opencl.*;

import org.bridj.Pointer;

public class GPUGaussianGenerator {

	protected int _batchSize = 2000000;
	protected int _batchSize_2 = _batchSize/2;
	final protected CLPlatform _plat = JavaCL.listPlatforms()[0];
	final protected String _kernelFile = "/Users/andingo/projects/Java/javacl/src/main/java/JavaCL/javacl/kernels.cl";
	protected KernelReader _kr;
	protected Pointer<Double> _randn;
	protected int _ind = 0;

	GPUGaussianGenerator() {
		try {
			_kr = new KernelReader(_kernelFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(_kr.getFuncNames().toString());
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
		Pointer<Double> unifs1Ptr = _getUnifs();
		Pointer<Double> unifs2Ptr = _getUnifs();
		CLBuffer<Double>
			unifs1 = context.createDoubleBuffer(CLMem.Usage.Input, unifs1Ptr),
			unifs2 = context.createDoubleBuffer(CLMem.Usage.Input, unifs2Ptr);
		// Create output buffer for N(0,1) samples
		CLBuffer<Double> out = context.createDoubleBuffer(CLMem.Usage.Output, _batchSize);
		// set args
		kernel.setArgs(unifs1,unifs2,out,_batchSize_2);
		
		for (int k : new int[]{0,1}) {
		for (long j : devices[k].getMaxWorkItemSizes()) {
			System.out.println(j);
		}
		}

		CLEvent event = kernel.enqueueNDRange(queue, new int[]{_batchSize}, new int[]{128});
		
		// retrieve results
		_randn = out.read(queue, event);
	}
	
	protected Pointer<Double> _getUnifs() {
		int _batchSize_2 = _batchSize/2;
		Pointer<Double> unifs = Pointer.allocateDoubles(_batchSize_2);
		Random rand = new Random();
		for (int i=0; i<_batchSize_2; i++) {
			unifs.set((long)i, rand.nextDouble());
		}
		return unifs;
	}
	
	public double[] getGaussians(int n) {
		double [] output;
		if (_ind + n < _batchSize) {
			output = _randn.next(_ind).getDoubles(n);
			_ind += n;
		} else {
			output = new double [n];

			int ct = 0;
			while (ct < n) {
				if (_ind == _batchSize) _generateGaussians();
				output[ct++] = _randn.get(_ind++);
			}
		}

		return output;
	}

}
