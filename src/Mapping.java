
public class Mapping<K, V> {
	private K key;
	private V value;
	
	Mapping(K key, V value){
		this.key = key;
		this.value = value ;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

}
