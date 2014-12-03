package JavaCL.javacl;

import com.nativelibs4java.opencl.*;

import org.bridj.Pointer;

public class GPUGaussianGenerator {

	final protected CLPlatform _plat = JavaCL.listPlatforms()[0];
	final protected String _kernelFile = "/Users/andingo/projects/Java/javacl/src/main/java/JavaCL/javacl/GPUGaussianGenerator_kernels.cl";
	protected KernelReader _kr;

	GPUGaussianGenerator() {
		try {
			_kr = new KernelReader(_kernelFile);
		} catch (Exception e) {}
		System.out.println(_kr.getFuncNames().toString());
	}
	
	public double[] getGaussians(int n) {
		CLDevice[] devices = _plat.listAllDevices(false);
		CLContext context = JavaCL.createContext(null, devices);
		context.createDefaultQueue();

	}

}
