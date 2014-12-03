package JavaCL.javacl;

import static org.junit.Assert.*;

import org.junit.*;

public class testKernelReader {
	
	final String path = "/Users/andingo/projects/Java/javacl/src/test/java/JavaCL/javacl/kernels.cl";

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void test() throws Exception {
		KernelReader kr = new KernelReader(path);
		String fname = "fill_in_values";
		System.out.println(kr.getKernel(fname));

		fname = "add_floats";
		System.out.println(kr.getKernel(fname));
	}

}
