package utils;

import java.util.*;

public class ArrayMap<K, V> extends AbstractMap<K, V> {

    /* the class creates a map of generic types, using an ArrayList of ArrayMapEntries */
    private ArrayList<ArrayMapEntry<K, V>> dictionary;
    private HashSet entries;

    public ArrayMap() {
        this.dictionary = new ArrayList<>();
        this.entries = new HashSet<>();
    }

    /* adds a map entry to the dictionary */
    public V put(K key, V value) {
        dictionary.add(new ArrayMapEntry<>(key, value));
        return this.get(key);
    }

    /* checks if the dictionary contains the given key */
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    /* returns the value mapped to the given key */
    public V get(Object key) {
        for (ArrayMapEntry<K, V> kvArrayMapEntry : dictionary) {
            if ((kvArrayMapEntry.getKey()).equals(key))
                return kvArrayMapEntry.getValue();
        }
        return null;
    }

    /* returns the size of the dictionary */
    public int size() {
        return dictionary.size();
    }

    /* returns all the entries of the dictionary */
    public Set<Entry<K, V>> entrySet() {
        entries.addAll(dictionary);
        return entries;
    }

    /* creates an inner class, representing each entry of the map, mapping a generic K type key to
     * a V type value */
    public static class ArrayMapEntry<K, V> implements Map.Entry<K, V> {

        private K key;
        private V value;

        ArrayMapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(Object value) {
            this.value = (V) value;
            return this.value;
        }

        public String toString() {
            return "(" + this.key + ", " + this.value + ")\n";
        }

        public int hashCode() {
            return (this.getKey() == null ? 0 : this.getKey().hashCode());
        }

        public boolean equals(Object obj) {
            ArrayMapEntry o = (ArrayMapEntry) obj;

            return (this.getKey() == null ? o.getKey() == null : this.getKey().equals(o.getKey()));
        }
    }
}
