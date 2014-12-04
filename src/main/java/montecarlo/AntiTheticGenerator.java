package montecarlo;

/**
 * Converts a RandomVectorGenerator into an Antithetic RandomVectorGenerator.
 * 
 * @author andingo
 * @version 1.0
 */
public class AntiTheticGenerator implements RandomVectorGenerator {
	private float [] tmp = null;
	private RandomVectorGenerator rvg = null;
	
	/**
	 * Pass in a RandomVectorGenerator implementation to generate arrays of
	 * (nearly) independent samples. Every second array returned will be the
	 * negation of it's predecessor.
	 * @param rvg
	 * @throws Exception
	 */
	public AntiTheticGenerator(RandomVectorGenerator rvg) throws Exception {
		if (rvg == null) throw ( new Exception ("rvg is null"));
		this.rvg = rvg;
	}

	public float[] getVector() {
		if (tmp != null) {
			float[] vec = new float [tmp.length];
			for (int i=0; i<tmp.length; i++) {
				vec[i] = -tmp[i];
			}
			tmp = null;
			return vec;
		} else {
			tmp = rvg.getVector();
			return tmp;
		}
	}

}
