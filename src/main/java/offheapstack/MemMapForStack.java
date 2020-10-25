package offheapstack;


import offheapmap.Serializer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;

import static util.BufferUtil.destroyBuffer;

public class MemMapForStack<E> {

    final int recordSize;
    final int totalElements;

    final int memMapSize ;

    ByteBuffer buffer ;




    Serializer keySerializer ;

    int numElements=0;

    int stackPointer=0;


    public MemMapForStack(int recordSize, int totalElements, Serializer keySerializer )
    {
        this.recordSize = recordSize;
        this.totalElements = totalElements;

        this.keySerializer = keySerializer;


        long tmp = (long)recordSize*(long)totalElements;

        if (tmp>Integer.MAX_VALUE) {
            tmp = Integer.MAX_VALUE;

            System.out.println("Size exceeds max integer value , capping the size to " + tmp);

        }

        memMapSize = (int) tmp;

        buffer = ByteBuffer.allocateDirect(memMapSize);

        System.out.println(buffer.mark());




    }

    public void clear()
    {
        numElements=0;
        stackPointer=0;
        buffer.position(0);

    }



    private void checkRecordSize(byte[] value)
    {
        int length = value.length + 4;

        if (length>=recordSize)
            throw new RuntimeException("Record size exceeds max specified , size = "+  length + ",max allowed = " + recordSize);
    }

    protected E push(E element)
    {
        // check stack pointer < size of the map , else resize first before adding to the stack.

        byte[] serElement = keySerializer.serialize(element) ;


        checkRecordSize(serElement);

        int location = stackPointer;

        stackPointer = stackPointer+recordSize;

        buffer.position(location);

        putRecord(serElement,location);

        numElements++;

        return element;


    }


    private E getRecord(int location)
    {
           int len = buffer.getInt();
            byte[] b = new byte[len];
            buffer.get(b);
            E value =  (E)keySerializer.deserialize(b);

            return value;

    }

    private void putRecord(byte[] value, int location)
    {
        buffer.position(location);
        buffer.putInt(value.length);
        buffer.put(value);

    }


    protected E get(int index)
    {
        buffer.position(index*recordSize);
        return getRecord(buffer.position());
    }


    protected E pop()
    {

        if (stackPointer==0)
            return null;

        int location = stackPointer-recordSize;
        stackPointer = stackPointer-recordSize;

        buffer.position(location);

        numElements--;

        return getRecord(location);


    }



/*
    public Collection<V> values()
    {
        List<V> list = new ArrayList<>();

        for (int i=0;i<totalElements;i++)
        {
            buffer.position(recordSize*i);
            if (buffer.get()==OCCUPIED) {
                int len = buffer.getInt();
                buffer.position(buffer.position()+len);
                len = buffer.getInt();
                byte[] b = new byte[len];
                buffer.get(b);
                V value1 = (V) valueSerializer.deserialize(b);

               list.add(value1);
            }

        }
        return Collections.unmodifiableCollection(list);

    }


    public boolean containsValue(V value)
    {

            for (int i=0;i<totalElements;i++)
            {
                buffer.position(recordSize*i);
                if (buffer.get()==OCCUPIED) {
                    int len = buffer.getInt();
                    buffer.position(buffer.position()+len);
                    len = buffer.getInt();
                    byte[] b = new byte[len];
                    buffer.get(b);
                    V value1 = (V) valueSerializer.deserialize(b);

                    if (value.equals(value1))
                        return true;
                }

            }
            return false;


    }




    protected V delete(K key)
    {
        int hash = Math.abs(key.hashCode());

        int index = hash%totalElements;

        int location = index*recordSize;

        buffer.position(location);


        int loopCounter=0;
        V result = null;

        byte type;
        while((type=buffer.get())!=EMPTY)
        {

            // read the record


            int len = buffer.getInt();

            byte[] b = new byte[len];

            buffer.get(b);

            K key1 = (K)keySerializer.deserialize(b);

            if (type==DELETED && key.equals(key1))
            {
                break;  // already deleted , return
            }

            if (key.equals(key1))// found
            {
                result = getRecord(buffer.position());
               buffer.position(location);
               buffer.put(DELETED);
               numElements--;

                return result;
            }

            ++index;
            location = index*recordSize;

            if (location>=memMapSize)
            {
                location=0;
                index = 0;
                loopCounter++;
                if (loopCounter>1) {
                    System.out.println("Delete encountered some unknown bug") ;
                    break;
                }
            }
            buffer.position(location);

        }

        return result;


    }

*/
    protected static<E> MemMapForStack<E> resize(MemMapForStack<E> orig, int factor)
    {

        MemMapForStack<E> resized = new MemMapForStack<>(orig.recordSize,orig.totalElements*factor, orig.keySerializer);
        ByteBuffer buffer = orig.buffer;

        buffer.position(0);

        for (int i=0;i<orig.totalElements;i++)
        {
                int len = buffer.getInt();
                byte[] b = new byte[len];
                buffer.get(b);
                E value = (E) orig.keySerializer.deserialize(b);

                resized.push(value);

            buffer.position(orig.recordSize*i);

        }


        destroyBuffer(orig.buffer);  // without this the direct buffer stays in memory.

        return resized;




    }



}