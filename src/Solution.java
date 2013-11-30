public class Solution implements Comparable<Solution> {
	private int a;
	private int b;
	private String plaintext;
	private Double fitness;
	private String snapshot;
	private String plaintextSnap;

	public int geta() {
		return a;
	}

	public int getb() {
		return b;
	}

	public String getPlaintext() {
		return plaintext;
	}

	public Double getFitness() {
		return fitness;
	}

	Solution(int a, int b, String plaintext, Double fitness) {
		this.a = a;
		this.b = b;
		this.plaintext = plaintext;
		this.fitness = fitness;
	}

	// Returns a snapshot of solution
	String getSnapshot() {
		if(snapshot == null){
			int snaplength = plaintext.length() < 40 ? plaintext.length() : 40;
			snapshot = String.format("(%3d, %3d) (%f) (%-25s...)", a, b, fitness,plaintext.substring(0, snaplength)+(plaintext.length() < 40 ? "":"..."));
		}
		return snapshot;
	}
	
	String getPlaintextSnapshot() {
		if(plaintextSnap == null) {
			int snaplength = plaintext.length() < 40 ? plaintext.length() : 40;
			plaintextSnap = plaintext.substring(0,snaplength) +  (plaintext.length() < 40 ? "":"...");
		}
		return plaintextSnap;
	}

	public int compareTo(Solution o) {
		return -1 * this.fitness.compareTo(o.fitness);
	}
}