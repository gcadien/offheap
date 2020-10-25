package efficientlist;

import offheapmap.Serializer;

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
    public boolean add(E e) {

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
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return false;
    }

    @Override
    public boolean addAll(int i, Collection<? extends E> collection) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
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

        int index = list.get(i);
        list.set(i,memMap.add(e));

        return memMap.get(index);


    }

    @Override
    public void add(int i, E e) {

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


}
