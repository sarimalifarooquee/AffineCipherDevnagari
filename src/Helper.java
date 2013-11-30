import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.TreeMap;

public class Helper {

	public static final int DEVNAGRI_MIN = 2304;
	public static final int DEVNAGRI_MAX = 2431;

	public static final int NUM_DEVNAGRI_CHARS = DEVNAGRI_MAX - DEVNAGRI_MIN+ 1;

	public static final String CORPORA_FILENAME = "CFILTHindiCorpora.txt";
	public static final String DEVNAGRI_CHAR_FREQ_OBJECT_FILENAME = "DevnagriCharFreq.ser";

	private static CharacterFreqCounter devnagriCharFreq = null;
	private static QuadgramAnalyser devnagriQuadAnalyser = null;
	
	public static final int DEFAULT_CHARS_TO_CONSIDER = 8;

	public static int gcd(int a, int b) {
		return b == 0 ? a : gcd(b, a % b);
	}

	public static boolean isCoprime(int x, int y) {
		if (gcd(x, y) == 1)
			return true;
		return false;
	}

	public static TreeMap<Character, Integer> getSortedMap(
			Map<Character, Integer> map) {
		CharIntComparator comparator = new CharIntComparator(map);
		TreeMap<Character, Integer> sortedMap = new TreeMap<Character, Integer>(
				comparator);
		sortedMap.putAll(map);
		return sortedMap;
	}

	public static int modulus(int x, int m) {
		int r = x % m;
		if (r < 0) {
			r += m;
		}
		return r;

	}

	public static boolean isDevnagri(char c) {
		return c >= DEVNAGRI_MIN && c <= DEVNAGRI_MAX;
	}

	public static int getPosition(char c) {
		if (isDevnagri(c))
			return c - DEVNAGRI_MIN;
		return (int) c;
	}

	public static int getModularMultiplicativeInverse(int a, int m) {
		if (a < 0)
			a = m+a;//m-a;
		if (a > m)
			a = a % m;

		for (int i = 0; i < m; i++)
			if ((a * i) % m == 1)
				return i;
		return 0;
	}

	public static String readFile(String filename) throws IOException {
		String filetext = new String();
		BufferedReader bfr = new BufferedReader(new FileReader(filename));
		String s = bfr.readLine();
		while (s != null) {
			filetext += s;
			s = bfr.readLine();
		}
		bfr.close();
		return filetext;
	}

	public static void writeFile(String ciphertext, String filename)
			throws IOException {
		BufferedWriter bfw = new BufferedWriter(new FileWriter(filename));
		bfw.write(ciphertext);
		bfw.close();

	}

	public static CharacterFreqCounter getDevnagriCharFreqCounter() {
		if (devnagriCharFreq != null)
			return devnagriCharFreq;
		try {
			FileInputStream filein = new FileInputStream(
					DEVNAGRI_CHAR_FREQ_OBJECT_FILENAME);
			ObjectInputStream objectin = new ObjectInputStream(filein);
			devnagriCharFreq = (CharacterFreqCounter) objectin.readObject();
			objectin.close();
			filein.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (devnagriCharFreq == null)
				devnagriCharFreq = new CharacterFreqCounter(CORPORA_FILENAME);
		}
		return devnagriCharFreq;

	}

	// SERIALIZATION IN CASE OF QUADGRAM ANALYSER IS INCREASING RUNTIME RATHER THAN DECREASING IT.
	// HENCE SERIALISATION NOT USED
	public static QuadgramAnalyser getDevnagriQuadgramAnalyser() {
		if (devnagriQuadAnalyser == null)
			devnagriQuadAnalyser = new QuadgramAnalyser(Helper.CORPORA_FILENAME);
		return devnagriQuadAnalyser;
	}
}
