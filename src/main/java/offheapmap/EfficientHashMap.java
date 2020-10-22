package offheapmap;

import util.IntegerSerializer;
import util.StringSerializer;

import java.util.*;

public class EfficientHashMap<K,V> implements Map<K,V>{

    MemMap<K,V> memMap ;

    Map<K,V> map = new CustomLinkedHashMap<>(10);


    public EfficientHashMap(int recordSize, int totalElements, Serializer keySerializer , Serializer valueSerializer)
    {

        memMap = new MemMap<>(recordSize,totalElements,keySerializer,valueSerializer);


    }

    @Override
    public int size() {
        return memMap.numElements;
    }

    @Override
    public boolean isEmpty() {
        return (memMap.numElements==0);
    }

    @Override
    public boolean containsKey(Object o) {
        return false;
    }

    @Override
    public boolean containsValue(Object o) {
        return false;
    }


    @Override
    public V remove(Object o) {

        K key = (K) o;
        map.remove(key);

        memMap.delete(key);

        return null;  // deviates from map convention of returning older object.

    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

        map.entrySet().stream().forEach(entry -> put(entry.getKey(),entry.getValue()));

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public V put(K key, V value) {

        memMap.put(key,value);

        return null;   //TODO - to fix this
    }

    @Override
    public V get(Object obj)
    {
        K key = (K)obj;
        V value = map.get(key);
        if (value!=null)
            return  value;

        Optional<V> perValue =  memMap.get(key);
        if (perValue.isPresent())
        {
            map.put(key,perValue.get());
            return perValue.get();
        }
        else
        {
            return null;
        }

    }


    protected void resize()
    {
        memMap = MemMap.resize(memMap);
    }



    static class CustomLinkedHashMap<K,V> extends LinkedHashMap<K,V>
    {

        int size;

        public CustomLinkedHashMap(int size)
        {
            super(size);

            this.size = size;
        }


        @Override
        protected boolean removeEldestEntry(Entry<K, V> eldest) {

            if (this.size()>=size) {
               // System.out.println("Removing from memory" + eldest);
                return true;
            }
            else
            {
                return false;
            }

        }
    }




}
