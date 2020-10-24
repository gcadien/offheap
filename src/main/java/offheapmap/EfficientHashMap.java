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


      return map.containsKey(o) || memMap.containsKey((K)o);

    }

    // has to traverse the entire collection - void using this method.
    @Override
    public boolean containsValue(Object o) {

        return map.containsValue(o) || memMap.containsValue((V)o);
    }


    @Override
    public V remove(Object o) {

        K key = (K) o;
        map.remove(key);

        return memMap.delete(key);


    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

        map.entrySet().stream().forEach(entry -> put(entry.getKey(),entry.getValue()));

    }

    @Override
    public void clear() {

        map.clear();

        memMap.clear();

    }

    @Override
    public Set<K> keySet() {
        return memMap.keySet();
    }

    @Override
    public Collection<V> values() {

        return memMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {


        return memMap.entrySet();
    }

    @Override
    public V put(K key, V value) {


        checkAndResize();


        return memMap.put(key,value);


    }


    private void checkAndResize()
    {
        if ((memMap.numElements/memMap.totalElements)*100>=75)
        {
            resize(2);
        }
        else if ((memMap.numSpots/memMap.totalElements)*100>=75)  // gets rid of the deleted records
        {
            resize(1);
        }
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


    protected void resize(int factor)
    {
        memMap = MemMap.resize(memMap,factor);
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
