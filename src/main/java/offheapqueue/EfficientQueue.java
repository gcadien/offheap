package offheapqueue;

import efficientlist.MemMapForList;
import offheapmap.Serializer;

import java.util.*;

public class EfficientQueue<E> implements Queue<E> {


    MemMapForQueue<E> memMap;

    public EfficientQueue(int recordSize, int totalElements, Serializer serializer)
    {

        memMap = new MemMapForQueue<>(recordSize,totalElements,serializer);
    }


    @Override
    public boolean add(E e) {
        return offer(e);
    }

    @Override
    public boolean offer(E e) {
        return memMap.add(e);
    }

    @Override
    public E remove() {
        return memMap.remove();
    }

    @Override
    public E poll() {
        return remove();
    }

    @Override
    public E element() {
        return peek();
    }

    @Override
    public E peek() {
        return memMap.peek();
    }

    @Override
    public int size() {
        return memMap.numElements;
    }

    @Override
    public boolean isEmpty() {
        return memMap.numElements==0;
    }

    @Override
    public boolean contains(Object o) {
        return memMap.contains((E)o);
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return null;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {

        var iterator = collection.iterator();
        while(iterator.hasNext())
        {
            var value = iterator.next();
            if (!contains(value))
                return false;
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {

        collection.stream().forEach(c-> add(c));
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {

        memMap.clear();

    }
}
