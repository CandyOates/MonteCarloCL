package JavaCL.javacl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Reads __kernel functions from a source page. Proper format of a __kernel function
 * is as follows:
 * 
 * __kernel void funcname(parameters...)
 * {
 * 		inside matter
 * }
 * 
 * The line closing a function declaration should read "}(.*)" in regex.
 * Lines not within a function not starting with __kernel are ignored. Parameters should
 * follow C conventions using pointers (*) and may use the __global identifier.
 * 
 * __kernel function source can be fetched by invoking the {@link KernelReader#getKernel()}
 * method with the __kernel function's name as the input.
 * 
 * @author andingo
 *
 */
public class KernelReader {
	// Maps the function name to its source code
	HashMap<String,String> __kernels = new HashMap<String,String>();
	
	/**
	 * Returns the source code associated with the function called fname.
	 * 
	 * @param fname
	 * @return
	 */
	public String getKernel(String fname) {
		return __kernels.get(fname);
	}
	
	/**
	 * 
	 * @return a set of __kernel function names
	 */
	public Set<String> getFuncNames() { return __kernels.keySet();}
	
	/**
	 * filename should contain __kernel source code in the format explained above.
	 * 
	 * @param filename
	 * @throws Exception
	 */
	KernelReader(String filename) throws Exception {
		File file = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		boolean open = false;
		String line = null;
		String fname = null;
		LinkedList<String> kernel = new LinkedList<String>();
		while ( (line = br.readLine()) != null ) {
			/*
			 * If not open, if the line is the function header, mark as open,
			 * extract the function name, and add to the List of lines in the function.
			 * If open, unless the line is the closing bracket "}", add the line to the List
			 * of lines in the function. If it is "}", then mark as closed, compile the lines
			 * into a single string, and add the (function name, function source) pair to the
			 * map.
			 */
			if (open) {
				if ( line.matches("}") ) {
					open = false;
					kernel.add(line+"\n");
					String program = "";
					Iterator<String> iter = kernel.iterator();
					while (iter.hasNext()) program += iter.next();
					__kernels.put(fname,program);
				} else {
					kernel.add(line+"\n");
				}
			} else {
				if ( line.matches("__kernel (.*)") ) {
					open = true;
					kernel.clear();
					kernel.add(line+"\n");
					fname = getFunctionName(line);
				}
				continue;
			}
			
		}
		
		br.close();
	}
	
	protected String getFunctionName(String line) {
		String[] spl = line.split(" ");
		for (String word : spl) {
			int ind = word.indexOf("(");
			if (ind < 0) continue;
			else {
				return word.substring(0, ind);
			}
		}
		return null;
	}

}
