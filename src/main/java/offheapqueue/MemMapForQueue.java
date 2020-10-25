package offheapqueue;


import offheapmap.Serializer;

import java.nio.ByteBuffer;
import java.util.List;

import static util.BufferUtil.destroyBuffer;

public class MemMapForQueue<E> {

    final int recordSize;
    final int totalElements;

    final int memMapSize ;

    ByteBuffer buffer ;




    Serializer keySerializer ;

    int numElements=0;  // queue is empty when numElements = 0;

    int head=0;
    int tail = 0;


    public MemMapForQueue(int recordSize, int totalElements, Serializer keySerializer )
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
        head=0; tail=0;
        buffer.position(0);

    }



    private void checkRecordSize(byte[] value)
    {
        int length = value.length + 4;

        if (length>=recordSize)
            throw new RuntimeException("Record size exceeds max specified , size = "+  length + ",max allowed = " + recordSize);
    }

    protected boolean add(E element)
    {
        // check stack pointer < size of the map , else resize first before adding to the stack.

        byte[] serElement = keySerializer.serialize(element) ;


        checkRecordSize(serElement);

        int location = tail;

        buffer.position(location);

        putRecord(serElement,location);

        numElements++;


        tail = tail+recordSize;

        if (tail>buffer.capacity())
        {
            tail=0;
        }

        return true;


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


    protected int find(E e)
    {
        for (int i=0;i<numElements;i++)
        {
            E other = get(i);
            if (e.equals(other))
                return i;
        }

        return -1;

    }

    protected int findLast(E e)
    {
        int result = -1;
        for (int i=0;i<numElements;i++)
        {
            E other = get(i);
            if (e.equals(other))
                result = i;
        }

        return result;

    }


    protected E remove()
    {

        if (numElements==0)
            return null;

        int location = head;

        buffer.position(location);

        numElements--;

        head = head + recordSize;
        if (head>buffer.capacity())
        {
            head = 0;
        }

        return getRecord(location);


    }

    protected E peek()
    {

        if (numElements==0)
            return null;

        int location = head;
        buffer.position(location);
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


    protected static<E> MemMapForQueue<E> resize(MemMapForQueue<E> orig, int factor, List<Integer> list)
    {

        MemMapForQueue<E> resized = new MemMapForQueue<>(orig.recordSize,orig.totalElements*factor, orig.keySerializer);
        ByteBuffer buffer = orig.buffer;

       for (int i=0;i<list.size();i++)
       {
           int index = list.get(i);
           buffer.position(index);
           int len = buffer.getInt();
           byte[] b = new byte[len];
           buffer.get(b);
           E value = (E) orig.keySerializer.deserialize(b);
           int newindex= resized.add(value);
           list.set(i,newindex);
       }

        destroyBuffer(orig.buffer);  // without this the direct buffer stays in memory.

        return resized;


    }


     */


}
