package offheapset;

import offheapmap.EfficientHashMap;
import offheapmap.MemMap;
import offheapmap.Serializer;
import util.StringSerializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class EfficientHashSet<K> implements Set<K> {

    final static String DUMMY = " ";

    EfficientHashMap<K,String> ehm ;

    public EfficientHashSet(int recordSize, int totalElements, Serializer keySerializer)
    {

       ehm = new EfficientHashMap<>(recordSize,totalElements,keySerializer,new StringSerializer());


    }


    @Override
    public int size() {
        return ehm.size();
    }

    @Override
    public boolean isEmpty() {
        return ehm.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return ehm.containsKey(o);
    }

    @Override
    public Iterator<K> iterator() {
        return new EHSIterator<>(ehm.keySet());
    }

    @Override
    public Object[] toArray() {

        return ehm.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {


        int i=0;
        for (Object k : ehm.keySet().toArray())
        {
            ts[i++] = (T)k;
        }

        return ts;
    }


    @Override
    public boolean add(K k) {
        if (ehm.containsKey(k))
            return false;
        else {
            ehm.put(k, DUMMY);
            return true;
        }
    }

    @Override
    public boolean remove(Object o) {

        if (ehm.remove(o)!=null)
            return true;
        else
            return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {

        for (Iterator<?> it = collection.iterator(); it.hasNext(); ) {
            var key  = it.next();
            if (!ehm.containsKey(key))
                return false;


        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends K> collection) {
        int orig = ehm.size();

        collection.stream().forEach(k->ehm.put(k,DUMMY));
        return orig==ehm.size();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {

        int size = ehm.size();

        ehm.keySet().stream().forEach(k-> {

            if (!collection.contains(k))
            {
                ehm.remove(k);

            }
        });

        return size==ehm.size();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        int orig = ehm.size();

        collection.stream().forEach(k->ehm.remove(k));
        return orig==ehm.size();
    }

    @Override
    public void clear() {

        ehm.clear();

    }

    static class EHSIterator<K> implements Iterator<K>
    {
        Iterator<K> ehmKeySetIterator;
        public EHSIterator(Set<K> ehmKeySet)
        {

            ehmKeySetIterator = ehmKeySet.iterator();
        }
        @Override
        public boolean hasNext() {
            return ehmKeySetIterator.hasNext();
        }

        @Override
        public K next() {
            return ehmKeySetIterator.next();
        }
    }
}
