import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class CharacterFreqCounter implements java.io.Serializable{	
	private static final long serialVersionUID = 1L;
	private HashMap<Character, Integer> charFreqMap = null;
	
	public CharacterFreqCounter(String filename){		
		this(filename, true);
	}
	
	public CharacterFreqCounter(String s, boolean isFilename){		
		charFreqMap = new HashMap<Character, Integer>();
		
		if(isFilename){
			buildCharFreqMap(s);
		}
		else{
			for(char c : s.toCharArray()) updateCharFreqMap(c);
		}
	}
	

	private void updateCharFreqMap(Character c){
		if((int)c < Helper.DEVNAGRI_MIN || (int)c > Helper.DEVNAGRI_MAX) return;
			
		if(charFreqMap.containsKey(Character.valueOf(c)))
			charFreqMap.put(Character.valueOf(c), charFreqMap.get(Character.valueOf(c))+1);
		else
			charFreqMap.put(Character.valueOf(c), 1);
	}
	
	public void buildCharFreqMap(String filename) {
		String s = new String();
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(filename));
			while (s != null) {
				s = reader.readLine();				
				if (s != null) {
					for(char c : s.toCharArray()) updateCharFreqMap(c);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public HashMap<Character, Integer> getCharFreqMap(){
    	return charFreqMap;
    }
    
    public static void main(String args[]) {
		CharacterFreqCounter devnagriCharFreq = new CharacterFreqCounter(Helper.CORPORA_FILENAME);
		
		FileOutputStream fileout = null;
		ObjectOutputStream objectout = null;
		try {
			fileout = new FileOutputStream(Helper.DEVNAGRI_CHAR_FREQ_OBJECT_FILENAME);
			objectout = new ObjectOutputStream(fileout);
			objectout.writeObject(devnagriCharFreq);
			objectout.close();
			fileout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}