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
        return null;
    }


    public Iterator<E> descendingIterator() {
        return null;
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




}
