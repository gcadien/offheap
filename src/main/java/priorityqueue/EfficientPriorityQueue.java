package priorityqueue;

import offheapmap.Serializer;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class EfficientPriorityQueue<E extends Comparable<E>>  {


    static class PQTuple<E extends Comparable<E>>  implements Comparable<PQTuple<E>>
    {
        MemMapForPQ<E> memMap;  //TODO - during resize , this reference needs to be updated .

        int position;

        public PQTuple(  MemMapForPQ<E> memMap, int position)
        {
            this.memMap = memMap;
            this.position = position;
        }

        E get()
        {
            return memMap.get(position);
        }

        @Override
        public int compareTo(PQTuple<E> epqTuple) {

           E first = get();
           E second = epqTuple.get();

           return first.compareTo(second);
        }
    }

    PriorityQueue<PQTuple<E>> priorityQueue;



    MemMapForPQ<E> memMap;

    public EfficientPriorityQueue(int recordSize , int totalElements, Serializer serializer) {

       priorityQueue = new PriorityQueue<>(totalElements);


        memMap = new MemMapForPQ<>(recordSize,totalElements,serializer);

    }

    public boolean add(E e) {

        if (memMap.numElements>=memMap.totalElements)
            return false;

        int pos = memMap.add(e);

        priorityQueue.add(new PQTuple(memMap,pos));

        return true;

    }

    public E peek() {

        var tuple = priorityQueue.peek();
        return tuple.get();
    }

    public E remove() {
        var tuple = priorityQueue.remove();
        return tuple.get();

    }

    public boolean contains(Object o) {
     throw new UnsupportedOperationException();

    }

    public Iterator<E> iterator() {

        return new PQIterator<>(priorityQueue.iterator());

    }

    public int size() {
        return priorityQueue.size();
    }

    public void clear() {
        priorityQueue.clear();
        memMap.clear();
    }


    static class PQIterator<E extends Comparable<E>> implements Iterator<E>
    {

        Iterator<PQTuple<E>> iterator;

        public PQIterator(Iterator<PQTuple<E>> iterator) {
             this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {

            return iterator.hasNext();
        }

        @Override
        public E next() {
            return iterator.next().get();
        }
    }
}
