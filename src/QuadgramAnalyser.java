import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class QuadgramAnalyser implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> quadgramFreqMap = new HashMap<String, Integer>();

	public QuadgramAnalyser(String filename) {
		buildQuadgramFreqMap(filename);

	}

	void buildQuadgramFreqMap(String filename) {
		String s = new String();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
			while (s != null) {
				s = reader.readLine();
				if (s != null) {
					for (int i = 0, j = 4; j <= s.length(); i++, j++) {
						updateFreqMap(s.substring(i, j));
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double analyse(String s) {
		double fitness = 0.0;
		String quadgram = new String();
		for (int i = 0, j = 4; j <= s.length(); i++, j++) {
			quadgram = s.substring(i, j);

			if (quadgramFreqMap.containsKey(quadgram))
				fitness += Math.log10((double) quadgramFreqMap.get(quadgram)
						/ quadgramFreqMap.size());
			else
				// HEURISTICS FOR LOG(0), SINCE LOG(0) is -∞
				fitness += Math.log10((double) 1 / quadgramFreqMap.size()) - 0.3;
		}
		return fitness;
	}

	private void updateFreqMap(String quadgram) {
		if (quadgram.length() != 4)
			return;
		if (quadgramFreqMap.containsKey(quadgram))
			quadgramFreqMap.put(quadgram, quadgramFreqMap.get(quadgram) + 1);
		else
			quadgramFreqMap.put(quadgram, 1);
	}

}