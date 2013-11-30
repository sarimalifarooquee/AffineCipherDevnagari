public class AffineEquationPair {
	private Mapping<Character, Character> e1;
	private Mapping<Character, Character> e2;
	private int a;
	private int b;
	boolean isSolved = false;

	AffineEquationPair(Mapping<Character, Character> e1,
			Mapping<Character, Character> e2) {
		this.e1 = e1;
		this.e2 = e2;
	}

	public void solve() {
		int d = Helper.getPosition(e1.getKey())
				- Helper.getPosition(e2.getKey());

		int dinverse = Helper.getModularMultiplicativeInverse(d,
				Helper.NUM_DEVNAGRI_CHARS);
		a = dinverse
				* (Helper.getPosition(e1.getValue()) - Helper.getPosition(e2
						.getValue()));
		a = Helper.modulus(a, Helper.NUM_DEVNAGRI_CHARS);
		b = dinverse
				* ((Helper.getPosition(e1.getKey()) * Helper.getPosition(e2
						.getValue())) - (Helper.getPosition(e2.getKey()) * Helper
						.getPosition(e1.getValue())));
		b = Helper.modulus(b, Helper.NUM_DEVNAGRI_CHARS);
		isSolved = true;
	}

	public int geta() {
		if (!isSolved)
			solve();
		return a;
	}

	public int getb() {
		if (!isSolved)
			solve();
		return b;
	}

	public Mapping<Character, Character> gete1() {
		return e1;
	}

	public Mapping<Character, Character> gete2() {
		return e2;
	}
}
