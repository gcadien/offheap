package offheapmap;

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
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
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
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

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

    public void delete(K key)
    {
        map.remove(key);

        memMap.delete(key);


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


    public static void main(String[] args) throws Exception {

        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer,String> map = new EfficientHashMap<>(100, 10000000,iser,ser );


        System.out.println(Runtime.getRuntime().freeMemory());

        for (int j=0;j<10000;j++) {

            for (int i = 0; i < 250; i++) {
                map.put(j*i, "Generic World int key " + (j*i));

                // map.put(200, "GENERIC WORLD INT key");

            }

            //System.out.println(map.get(j*1)  + "  " + j);

         /*   for (int i = 0; i < 250; i++) {

               // System.out.println(map.get(j*i));

                map.delete(j*i);


            }*/

        }

        System.gc();

        Thread.sleep(5000);

        System.out.println(Runtime.getRuntime().freeMemory());


   /*     System.out.println();



        map.resize();

        System.out.println(map.get(100));

        System.out.println(map.get(200));


        map.delete(100);

        System.out.println("Retrieving deleted key " + map.get(100));*/


        map.put(100,"Key put again after deletion");

        System.out.println(map.get(100));



    }



}
