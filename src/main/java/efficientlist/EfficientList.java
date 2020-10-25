package efficientlist;

import offheapmap.Serializer;
import offheapstack.MemMapForStack;

import java.util.*;

public class EfficientList<E> implements List<E> {


    MemMapForList<E> memMap;

    List<Integer> list = new ArrayList<>();

    public EfficientList(int recordSize, int totalElements, Serializer serializer)
    {

        memMap = new MemMapForList<>(recordSize,totalElements,serializer);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {

        int index = memMap.find((E) o);

        if (index==-1) return false;

        return list.contains(index);

    }

    @Override
    public Iterator<E> iterator() {
        return new ELIterator<>(this);
    }

    @Override
    public Object[] toArray() {

        Object[] objects = new Object[list.size()];

        for (int i=0;i<list.size();i++)
        {
            objects[i] = memMap.get(list.get(i));
        }
        return objects;
    }

    @Override
    public <T> T[] toArray(T[] ts) {


        for (int i=0;i<list.size();i++)
        {
            ts[i] = (T)memMap.get(list.get(i));
        }

        return ts;
    }

    @Override
    public boolean add(E e) {

        checkAndResize();

        int pos = memMap.add(e);

        list.add(pos);

        return false;
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {

        List<Integer>  ints = new ArrayList<>();
        for (var e : collection)
        {
           int index =  memMap.find((E) e);
           if (index==-1)
               return false;
           ints.add(index);

        }
        return list.containsAll(ints);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {

        collection.stream().forEach(c->add(c));
        return true;
    }

    @Override
    public boolean addAll(int i, Collection<? extends E> collection) {

        collection.stream().forEach(c->add(i,c));
        return true;

    }

    @Override
    public boolean removeAll(Collection<?> collection) {

        return list.removeAll(collection);

    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return list.retainAll(collection);
    }

    @Override
    public void clear() {

        memMap.clear();
        list.clear();

    }

    @Override
    public E get(int i) {

        int index = list.get(i);
        return memMap.get(index);
    }

    @Override
    public E set(int i, E e) {

        checkAndResize();

        int index = list.get(i);
        list.set(i,memMap.add(e));

        return memMap.get(index);


    }

    @Override
    public void add(int i, E e) {
        checkAndResize();

        list.add(i,memMap.add(e));

    }

    @Override
    public E remove(int i) {
        int index = list.remove(i);
        return memMap.get(index);
    }

    @Override
    public int indexOf(Object o) {
        int index = memMap.find((E) o);
        return index==-1?-1 : list.indexOf(index);

    }

    @Override
    public int lastIndexOf(Object o) {
        int index = memMap.findLast((E) o);
        return index==-1?-1 : list.lastIndexOf(index);
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int i) {
        return null;
    }

    @Override
    public List<E> subList(int start, int end) {

        List<E> tmp = new ArrayList<>();
        for (int i=start;i<end;i++)
        {
            tmp.add(memMap.get(list.get(i)));
        }


        return tmp;
    }

    static class ELIterator<E> implements Iterator<E>
    {
        EfficientList<E> list;

        int index =0;

        public ELIterator(EfficientList<E> list)
        {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return index<list.size();
        }

        @Override
        public E next() {

            int spot = list.list.get(index++);

            return list.memMap.get(spot);


        }
    }

    private void checkAndResize()
    {
        if ((memMap.numElements/memMap.totalElements)*100>=75)
        {
            if (list.size()*2 < memMap.numElements)  // no need to increase size , just compact
                 memMap = MemMapForList.resize(memMap,1,list);
            else
                memMap = MemMapForList.resize(memMap,2,list);

        }

    }

}
