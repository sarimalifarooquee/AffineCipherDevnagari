import java.util.Comparator;
import java.util.Map;

class CharIntComparator implements Comparator<Character> {

	Map<Character, Integer> base;
    
	public CharIntComparator(Map<Character, Integer> base) {
        this.base = base;
    }
   
    public int compare(Character a, Character b) {
       int result = base.get(a).compareTo(base.get(b));
       if(result == 0) result = a.compareTo(b);
       return (-1 * result);
    }
}