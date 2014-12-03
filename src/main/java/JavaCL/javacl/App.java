package JavaCL.javacl;

import com.nativelibs4java.opencl.*;

import org.bridj.Pointer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	// Creating the platform which is out computer.
        CLPlatform clPlatform = JavaCL.listPlatforms()[0];
        // Getting the GPU device
        //CLDevice device = clPlatform.listGPUDevices(true)[0];
        CLDevice device = clPlatform.listAllDevices(true)[1];
        // Verifing that we have the GPU device
        System.out.println("*** New device *** ");
        System.out.println("Vendor: " + device.getVendor());
        System.out.println("Name: " + device.getName() );
        System.out.println("Type: " + device.getType() );
        // Let's make a context
        CLContext context = JavaCL.createContext(null, device);
        // Lets make a default FIFO queue.
        CLQueue queue = context.createDefaultQueue();

        // Read the program sources and compile them :
//        String src = "__kernel void add_floats(__global const float* a, __global const float* b, __global float* out, int n) \n" +
//                "{\n" +
//                "    int i = get_global_id(0);\n" +
//                "    if (i >= n)\n" +
//                "        return;\n" +
//                "\n" +
//                "    out[i] = a[i] + b[i];\n" +
//                "}\n" +
//                "\n" +
//                "__kernel void fill_in_values(__global float* a, __global float* b, int n) \n" +
//                "{\n" +
//                "    int i = get_global_id(0);\n" +
//                "    if (i >= n)\n" +
//                "        return;\n" +
//                "\n" +
//                "    a[i] = cos((float)i);\n" +
//                "    b[i] = sin((float)i);\n" +
//                "}";
        String src = (new KernelReader("/Users/andingo/projects/Java/javacl/src/test/java/JavaCL/javacl/kernels.cl")).getKernel("fill_in_values");
        CLProgram program = context.createProgram(src);
        program.build();

        CLKernel kernel = program.createKernel("fill_in_values");

        final long tmp = System.currentTimeMillis();
        final int n = 1024*256
                *16;
        final Pointer<Float>
                aPtr = Pointer.allocateFloats(n),
                bPtr = Pointer.allocateFloats(n);

        System.out.println((System.currentTimeMillis() - tmp));

        //for (int i = 0; i < n; i++) {
        //    aPtr.set(i, (float) i);
        //    bPtr.set(i, (float) i);
        //}

        System.out.println((System.currentTimeMillis() - tmp));

        // Create OpenCL input buffers (using the native memory pointers aPtr and bPtr) :
        CLBuffer<Float>
                a = context.createFloatBuffer(CLMem.Usage.Output, aPtr),
                b = context.createFloatBuffer(CLMem.Usage.Output, bPtr);

        // Create an OpenCL output buffer :
        //CLBuffer<Float> out = context.createFloatBuffer(CLMem.Usage.Output, n);
        kernel.setArgs(a, b, n);
        System.out.println((System.currentTimeMillis() - tmp));
        CLEvent event = kernel.enqueueNDRange(queue, new int[]{n}, new int[]{128});
        event.invokeUponCompletion(new Runnable() {
            public void run() {
                System.out.println((System.currentTimeMillis() - tmp));
            }
        });
        //final Pointer<Float> cPtr = out.read(queue,event);
        //System.out.println((System.currentTimeMillis() - tmp));
        //        for ( int i = 0; i < 12; ++i){
        //            System.out.println( aPtr.get(i) + " " + cPtr.get(i) );
        //        }


        float[] aa = new float[n];
        float[] bb = new float[n];
        float[] cc = new float[n];

        //for ( int i = 0; i < n; ++i){
        //    aa[i] = (float) i;
        //    bb[i] = (float) i;
        //}
        System.out.println((System.currentTimeMillis() - tmp));
        for ( int i = 0; i < n; ++i){
            Math.cos(i);
            Math.sin(i);
        }

        System.out.println((System.currentTimeMillis() - tmp));
    }
}
