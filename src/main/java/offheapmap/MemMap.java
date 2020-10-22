package offheapmap;


import java.nio.ByteBuffer;
import java.util.Optional;

//TODO - make generic

//TODO - associate with a file

/*
MappedByteBuffer out = file.getChannel()
                                        .map(FileChannel.MapMode.READ_WRITE, 0, length);
 */
public class MemMap<K,V> {

    final int recordSize;
    final int totalElements;

    final int memMapSize ;

    ByteBuffer buffer ;

    static final byte EMPTY = (byte)1;
    static final byte OCCUPIED = (byte)2;
    static final byte DELETED = (byte)3;


    Serializer keySerializer ;
    Serializer valueSerializer;

    int numElements=0;

    public MemMap(int recordSize, int totalElements, Serializer keySerializer , Serializer valueSerializer)
    {
        this.recordSize = recordSize;
        this.totalElements = totalElements;

        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        memMapSize = recordSize*totalElements;

        buffer = ByteBuffer.allocateDirect(memMapSize);

        System.out.println(buffer.mark());

        for (int i=0;i<memMapSize;i++)
        {
            buffer.put(EMPTY);
        }



    }


    // TODO - resize when empty elements are 25% of total size


    // also assumes max elements not exceeded - ie atleast one empty spot to break the infinite loop.


    private void checkRecordSize(byte[] key , byte[] value)
    {
        int length = key.length + value.length + 1;

        if (length>=recordSize)
            throw new RuntimeException("Record size exceeds max specified , size = "+  length + ",max allowed = " + recordSize);
    }

    public void put(K key, V value)
    {
        byte[] serKey = keySerializer.serialize(key) ;
        byte[] serValue = valueSerializer.serialize(value);

        checkRecordSize(serKey,serValue);

        int hash = Math.abs(key.hashCode());

        int index = hash%totalElements;

        int location = index*recordSize;

        buffer.position(location);

        boolean deletedinPath=false;
        int firstDeletedLocation=-1;

        while(true)
        {
            byte type = buffer.get();
            switch(type)
            {
                case OCCUPIED :
                    K curr = getKey();
                    if (key.equals(curr))
                    {
                        // overwriting current key , so do not change elements counter.
                        putRecord(serKey,serValue,location);
                        return;
                    }
                    break;

                case EMPTY :
                    if (deletedinPath)
                    {
                        putRecord(serKey,serValue,firstDeletedLocation);
                    }
                    else
                    {
                        putRecord(serKey,serValue,location);
                    }
                    numElements++;
                    return ;
                case DELETED :
                    curr = getKey();
                    if (key.equals(curr))
                    {
                        putRecord(serKey,serValue,location);
                        numElements++;
                        return;
                    }
                    else if (!deletedinPath)
                    {
                        deletedinPath=true;
                        firstDeletedLocation = location;
                    }

            }
            ++index;
            location = index*recordSize;

            if (location>=memMapSize)
            {
                location=0;
                index = 0;
            }
            buffer.position(location);


        }





    }


    private void putRecord(byte[] key, byte[] value, int location)
    {
        buffer.position(location);
       // byte[] b = keySerializer.serialize(key) ;
        buffer.put(OCCUPIED);
        buffer.putInt(key.length);
        buffer.put(key);
       // b = valueSerializer.serialize(value);
        buffer.putInt(value.length);
        buffer.put(value);

    }

    private K getKey()
    {
        int len = buffer.getInt();

        byte[] b = new byte[len];

        buffer.get(b);

        K key1 = (K)keySerializer.deserialize(b);

        return key1;


    }


    public Optional<V> get(K key)
    {
        int hash = Math.abs(key.hashCode());

        int index = hash%totalElements;

        int location = index*recordSize;

        buffer.position(location);


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
                break;
            }

            if (key.equals(key1))// found
            {
                len = buffer.getInt();
                b = new byte[len];
                buffer.get(b);
                V value =  (V)valueSerializer.deserialize(b);

                return Optional.of(value);
            }

            ++index;
            location = index*recordSize;

            if (location>=memMapSize)
            {
                location=0;
                index = 0;
            }
            buffer.position(location);

        }

        return Optional.empty();


    }


    public void delete(K key)
    {
        int hash = Math.abs(key.hashCode());

        int index = hash%totalElements;

        int location = index*recordSize;

        buffer.position(location);


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
               buffer.position(location);
               buffer.put(DELETED);
               numElements--;

                return;
            }

            ++index;
            location = index*recordSize;

            if (location>=memMapSize)
            {
                location=0;
                index = 0;
            }
            buffer.position(location);

        }

        return ;


    }


    protected static<K,V> MemMap<K, V> resize(MemMap<K,V> orig)
    {

        MemMap<K,V> resized = new MemMap<>(orig.recordSize,orig.totalElements*2, orig.keySerializer,orig.valueSerializer);


        ByteBuffer buffer = orig.buffer;

        for (int i=0;i<orig.totalElements;i++)
        {
            if (buffer.get()!=EMPTY) {
                int len = buffer.getInt();
                byte[] b = new byte[len];
                buffer.get(b);
                K key = (K) orig.keySerializer.deserialize(b);
                len = buffer.getInt();
                b = new byte[len];
                buffer.get(b);
                V value = (V) orig.valueSerializer.deserialize(b);

                resized.put(key, value);
            }
            buffer.position(orig.recordSize*i);

        }



        return resized;




    }



}
