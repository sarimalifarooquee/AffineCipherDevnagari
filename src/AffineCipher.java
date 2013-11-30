import java.io.IOException;
import java.util.HashMap;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;

public class AffineCipher {

	AffineCipher() {
	}

	// ENCRYPTS THE PLAINTEXT STRING USING a AND b AS KEYS FOR AFFINE EQUATION.
	// EACH CHARACTER p IN PLAINTEXT IS MAPPED TO A CHARACTER c SUCH THAT,
	//
	// POSITION(c) = (a * POSITION(p) + b) MOD(M), 1<= a <= M, 1<=b <=M
	//
	// WHERE POSITION(x) IS THE POSITION OF CHARACTER x IN THE DEVNAGRI SECTION
	// OF UNICODE TABLE, AND M IS THE NUMBER OF CHARACTERS IN DEVNAGRI CHARACTER
	// SET.

	String encrypt(String plaintext, int a, int b) throws NotCoprimeException,
			OutOfBoundsException {
		if (!Helper.isCoprime(a, Helper.NUM_DEVNAGRI_CHARS))
			throw new NotCoprimeException();

		if (a < 1 || a > Helper.NUM_DEVNAGRI_CHARS || b < 1
				|| b > Helper.NUM_DEVNAGRI_CHARS)
			throw new OutOfBoundsException();

		String ciphertext = new String();
		int cipher;

		for (char c : plaintext.toCharArray()) {

			if (!Helper.isDevnagri(c)) {
				ciphertext += String.valueOf(c);
				continue;
			}
			
			cipher = Helper.modulus(((a * Helper.getPosition(c)) + b),
					Helper.NUM_DEVNAGRI_CHARS);
			
			ciphertext += String.valueOf((char) (cipher + Helper.DEVNAGRI_MIN));

		}
		return ciphertext;
	}

	// DECRYPTS THE PLAINTEXT STRING USING a AND b AS KEYS FOR THE AFFINE
	// EQUATION
	String decrypt(String ciphertext, int a, int b) throws NotCoprimeException,
			OutOfBoundsException {
		if (!Helper.isCoprime(a, Helper.NUM_DEVNAGRI_CHARS))
			throw new NotCoprimeException();

		if (a < 1 || a > Helper.NUM_DEVNAGRI_CHARS || b < 1
				|| b > Helper.NUM_DEVNAGRI_CHARS)
			throw new OutOfBoundsException();

		String plaintext = new String();

		int ainverse = Helper.getModularMultiplicativeInverse(a,
				Helper.NUM_DEVNAGRI_CHARS);
		int plain;

		for (char c : ciphertext.toCharArray()) {
			if (!Helper.isDevnagri(c)) {
				plaintext += String.valueOf(c);
				continue;
			}
			plain = Helper.modulus((ainverse * (Helper.getPosition(c) - b)),
					Helper.NUM_DEVNAGRI_CHARS);
			plaintext += String.valueOf((char) (plain + Helper.DEVNAGRI_MIN));
		}
		return plaintext;
	}

	
	
	Queue<AffineEquationPair> generateGuesses(
			HashMap<Character, Integer> ciphertextCharFreqMap,
			HashMap<Character, Integer> devnagriCharFreqMap, int charsToConsider) {

		Character[] temp = { 'a', 'b', 'c' };

		Character[] keys = (Character[]) Helper
				.getSortedMap(devnagriCharFreqMap).keySet().toArray(temp);
		Character[] values = (Character[]) Helper
				.getSortedMap(ciphertextCharFreqMap).keySet().toArray(temp);

		Queue<AffineEquationPair> guesses = new ArrayBlockingQueue<AffineEquationPair>(
				keys.length * values.length * values.length);
		AffineEquationPair guess;

		for (int i = 0; i < charsToConsider && i < keys.length; i++) {
			for (int j = 0; j < charsToConsider && j  < keys.length; j++) {
				if (i == j) continue;
				for (int k = 0; k < charsToConsider && k < values.length; k++) {
					for (int l = 0; l < charsToConsider && l < values.length; l++) {
						if( k == l) continue;

		
		
//		for (int i = 0; i + 1 < charsToConsider && i + 1 < keys.length; i++) {
//			for (int j = 0; j < charsToConsider && j  < values.length; j++) {
//				for (int k = 0; k < charsToConsider && k < values.length; k++) {
//					if (j == k)
//						continue;

					// a * p + b = r, p IS PLAINTEXT CHARACTER AND q IS
					// CORRESPONDING CIPHER CHARACTER
					// a * q + b = s
//					Mapping<Character, Character> e1 = new Mapping<Character, Character>(
//							keys[i], values[j]);
//					Mapping<Character, Character> e2 = new Mapping<Character, Character>(
//							keys[i + 1], values[k]);
					Mapping<Character, Character> e1 = new Mapping<Character, Character>(
								keys[i], values[k]);
					Mapping<Character, Character> e2 = new Mapping<Character, Character>(
								keys[j], values[l]);
					guess = new AffineEquationPair(e1, e2);
					// DON'T ADD IMPOSSIBLE GUESSES !
					if (!Helper.isCoprime(guess.geta(),
							Helper.NUM_DEVNAGRI_CHARS))
						continue;
					

					guesses.add(guess);
				}
			}
		}
		}
		return guesses;
	}

	// THIS METHOD TRIES TO DECRYPT THE CIPHERTEXT USING BRUTE FORCE METHOD,
	// TRYING ALL VALID COMBINATIONS OF a, b.
	SortedSet<Solution> bruteforceBreakCipher(String ciphertext) {

		SortedSet<Solution> solutions = new TreeSet<Solution>();
		for (int a = 1; a <= Helper.NUM_DEVNAGRI_CHARS; a++) {
			if (Helper.isCoprime(a, Helper.NUM_DEVNAGRI_CHARS)) {
				for (int b = 1; b <= Helper.NUM_DEVNAGRI_CHARS; b++) {
					try {
						String plaintext = decrypt(ciphertext, a, b);
						Double fitness = Helper.getDevnagriQuadgramAnalyser()
								.analyse(plaintext);
						Solution s = new Solution(a, b, plaintext, fitness);
						solutions.add(s);
					} catch (NotCoprimeException e) {
						System.out.println("NotCoprimeException");
						e.printStackTrace();
					} catch (OutOfBoundsException e) {
						System.out.println("OutOfBoundsException");
						e.printStackTrace();
					}
				}
			}
		}
		return solutions;
	}

	// THIS METHOD TRIES TO DECRYPT THE CIPHERTEXT USING FREQUENCY ANALYSIS,
	// ASSUMING THE
	// CIPHERTEXT TO HAVE BEEN ENCRYPTED USING AFFINE CIPHER. THE OUTPUT IS A
	// SET OF POSSIBLE
	// SOLUTIONS SORTED BY THEIR FITNESS. THE FITNESS OF A SOLUTION IS OBTAINED
	// BY PERFORMING
	// THE QUADGRAM STATISTICS ANALYSIS OF THE SOLUTION WITH RESPECT TO QUADGRAM
	// STATISTICS
	// OF THE HINDI LANGUAGE.

	SortedSet<Solution> breakCipher(String ciphertext) {
		return breakCipher(ciphertext, Helper.DEFAULT_CHARS_TO_CONSIDER);
	}

	SortedSet<Solution> breakCipher(String ciphertext, int charsToConsider) {
		
		// CREATE CIPHERTEXT'S CHARACTER FREQUENCY MAP
		HashMap<Character, Integer> ciphertextCharFreqMap = new CharacterFreqCounter(
				ciphertext, false).getCharFreqMap();
		
		// RETRIVE DEVNAGRI SCRIPT'S CHARACTER FREQUENCY MAP, OBTAINED
		// BY RUNNING FREQUENCY ANALYSIS ON A STANDARD HINDI LANGUAGE CORPORA
		HashMap<Character, Integer> devnagriCharFreqMap = Helper
				.getDevnagriCharFreqCounter().getCharFreqMap();
		// GENERATE GUESSES USING CIPHERTEXT CHARACTER FREQUENCY MAP AND
		// THE DEVNAGRI SCRIPT CHARACTER FREQUENCY MAP OBTAINED ABOVE.

		// EACH GUESS IS A PAIR OF AFFINE EQUATIONS, OBTAINED BY GUESSING
		// A PAIR OF MAPPINGS BETWEEN CIPHERTEXT AND DEVNAGRI SCRIPT CHARACTERS
		Queue<AffineEquationPair> guesses = generateGuesses(
				ciphertextCharFreqMap, devnagriCharFreqMap, charsToConsider);

		SortedSet<Solution> solutions = new TreeSet<Solution>();

		AffineEquationPair guess;

		while (!guesses.isEmpty()) {

			guess = guesses.poll();
			try {

				// OBTAIN a AND b USING GUESSED EQUATINOS AND TRY TO DECRYPT THE
				// CIPHERTEXT
				// USING a AND b. IF THE VALUE a IS NOT COPRIME WITH NUMBER OF
				// CHARACTERS
				// IN DEVNAGRI SCRIPT, THIS WILL RESULT IN AN EXCEPTION. IN
				// WHICH CASE WE
				// IGNORE THIS GUESS AND PROCEED WITH OTHER GUESSES
				String plaintext = decrypt(ciphertext, guess.geta(),
						guess.getb());

				// USE THE QUADGRAM STATISTICS OF THE PLAINTEXT OBTAINED ABOVE,
				// TO DETERMINE
				// HOW LIKELY IT IS TO BE HINDI, AND ASSIGN A FITNESS VALUE.
				Double fitness = Helper.getDevnagriQuadgramAnalyser().analyse(
						plaintext);

				Solution s = new Solution(guess.geta(), guess.getb(),
						plaintext, fitness);
				solutions.add(s);

			} catch (NotCoprimeException e) {

				System.out.println("NotCoprimeException");
				e.printStackTrace();
				continue;

			} catch (OutOfBoundsException e) {

				System.out.println("OutOfBoundsException");
				e.printStackTrace();
				continue;

			}
		}
		return solutions;
	}

	/*
	 * "-e filename a b" for encrypt 
	 * "-d filename a b" for decrypt
	 * "-b filename f" for breaking using frequency analysis
	 * "-b filename b" for breaking using brute force
	 */
	public static void main(String args[]) {


		AffineCipher affineCipher = new AffineCipher();
		
		try {
			if (args[0].equals("-e")) { // ENCRYPT
				String plaintext = Helper.readFile(args[1]);
				String ciphertext = affineCipher.encrypt(plaintext,
						Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				Helper.writeFile(ciphertext, args[1] + ".encrypted.txt");

			} else if (args[0].equals("-b")) { // BREAK
				SortedSet<Solution> solutions = new TreeSet<Solution>();
				String ciphertext = Helper.readFile(args[1]);
				if(args[2].equals("f")){

					solutions = affineCipher.breakCipher(ciphertext);
				}
				else if(args[2].equals("b")){
					solutions = affineCipher.bruteforceBreakCipher(ciphertext);
				}
					System.out.printf("  %c ,  %c    %-14s %-25s%n", 'a', 'b',
							"fitness", "plaintext snapshot");
					System.out
							.println("------------------------------------------------");
		
				for (Solution s : solutions) {
						System.out.println(s.getSnapshot());
				}

			} else if (args[0].equals("-d")) { // DECRYPT
				String plaintext = Helper.readFile(args[1]);
				String ciphertext = affineCipher.decrypt(plaintext,
						Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				Helper.writeFile(ciphertext, args[1] + ".decrypted.txt");
			}

		} catch (NumberFormatException e) {
			System.out.println("Incorrect values of encryption keys.");
		} catch (NotCoprimeException e) {
			System.out.println("Incorrect values of encryption key.");
		} catch (OutOfBoundsException e) {
			System.out.println("Incorrect values of encryption key.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Incorrect number of parameters.");
		}
	}
}