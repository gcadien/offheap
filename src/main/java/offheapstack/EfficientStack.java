package offheapstack;

import offheapmap.MemMap;
import offheapmap.Serializer;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Stack;

public class EfficientStack<E>  {


    MemMapForStack<E> memMap;

    public EfficientStack(int recordSize, int totalElements, Serializer serializer)
    {
        memMap = new MemMapForStack<>(recordSize,totalElements,serializer);
    }




    public E push(E e) {

        checkAndResize();


        return memMap.push(e);
    }


    public E pop() {
        return memMap.pop();
    }



    public int size() {
        return memMap.numElements;
    }


    public Iterator<E> iterator() {
        return new ESIterator<>(memMap);
    }


    public Iterator<E> descendingIterator() {
        return new ESDIterator<>(memMap);
    }


    public boolean isEmpty() {
        return memMap.numElements==0;
    }



    public void clear() {

        memMap.clear();

    }

    private void checkAndResize()
    {
        if ((memMap.numElements/memMap.totalElements)*100>=75)
        {
            memMap = MemMapForStack.resize(memMap,2);
        }

    }


    static class ESIterator<E> implements Iterator<E>
    {
        public MemMapForStack<E> memMap;

        int count=0;

        public ESIterator(MemMapForStack<E> memMap)
        {
            this.memMap = memMap;
        }

        @Override
        public boolean hasNext() {

            return count<=memMap.numElements;

        }

        @Override
        public E next() {
            return memMap.get(count++);
        }
    }


    static class ESDIterator<E> implements Iterator<E>
    {
        public MemMapForStack<E> memMap;

        int count=0;

        public ESDIterator(MemMapForStack<E> memMap)
        {
            this.memMap = memMap;
            count=memMap.numElements;
        }

        @Override
        public boolean hasNext() {

            return count>=0;

        }

        @Override
        public E next() {
            return memMap.get(count--);
        }
    }




}
