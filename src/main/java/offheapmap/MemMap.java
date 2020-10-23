package offheapmap;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;

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

    int numSpots=0;  // a new put will add to this. delete will not reduce this. update in place will not increase this.

    public MemMap(int recordSize, int totalElements, Serializer keySerializer , Serializer valueSerializer)
    {
        this.recordSize = recordSize;
        this.totalElements = totalElements;

        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;

        long tmp = (long)recordSize*(long)totalElements;

        if (tmp>Integer.MAX_VALUE) {
            tmp = Integer.MAX_VALUE;

            System.out.println("Size exceeds max integer value , capping the size to " + tmp);

        }

        memMapSize = (int) tmp;

        buffer = ByteBuffer.allocateDirect(memMapSize);

        System.out.println(buffer.mark());

        for (int i=0;i<memMapSize;i++)
        {
            buffer.put(EMPTY);
        }



    }

    public void clear()
    {
        numElements=0;
        numSpots=0;
        buffer.position(0);
        for (int i=0;i<memMapSize;i++)
        {
            buffer.put(EMPTY);
        }
    }


   //TODO - refactor the navigation in each method to common functions.


    // also assumes max elements not exceeded - ie atleast one empty spot to break the infinite loop.


    private void checkRecordSize(byte[] key , byte[] value)
    {
        int length = key.length + value.length + 1;

        if (length>=recordSize)
            throw new RuntimeException("Record size exceeds max specified , size = "+  length + ",max allowed = " + recordSize);
    }

    protected void put(K key, V value)
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

        int loopCounter=0;

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
                    numSpots++;
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
                    break;
                default :
                    System.out.println("Type unknown - this should not happen");

            }
            ++index;
            location = index*recordSize;

            if (location>=memMapSize)
            {
                location=0;
                index = 0;
                loopCounter++;
                if (loopCounter>1) {
                    System.out.println("Put encountered some unknown bug") ;
                    break;
                }

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



    protected Optional<V> get(K key)
    {
        int hash = Math.abs(key.hashCode());

        int index = hash%totalElements;

        int location = index*recordSize;

        buffer.position(location);

        int loopCounter=0;


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
                loopCounter++;

                if (loopCounter>1) {
                    System.out.println("Get encountered some unknown bug") ;
                    break;
                }
            }
            buffer.position(location);

        }

        return Optional.empty();


    }


    public boolean containsKey(K key)
    {
       Optional<V> value = get(key);
       return value.isPresent();


    }

    public Set<K> keySet()
    {

        Set<K> keys = new HashSet<>();
        buffer.position(0);

        for (int i=0;i<totalElements;i++)
        {
            if (buffer.get()==OCCUPIED) {
                int len = buffer.getInt();
                byte[] b = new byte[len];
                buffer.get(b);
                K key = (K) keySerializer.deserialize(b);
                keys.add(key);
            }
            buffer.position(recordSize*i);

        }

        return keys;

    }

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




    protected void delete(K key)
    {
        int hash = Math.abs(key.hashCode());

        int index = hash%totalElements;

        int location = index*recordSize;

        buffer.position(location);


        int loopCounter=0;
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
                loopCounter++;
                if (loopCounter>1) {
                    System.out.println("Delete encountered some unknown bug") ;
                    break;
                }
            }
            buffer.position(location);

        }

        return ;


    }


    protected static<K,V> MemMap<K, V> resize(MemMap<K,V> orig, int factor)
    {

        MemMap<K,V> resized = new MemMap<>(orig.recordSize,orig.totalElements*factor, orig.keySerializer,orig.valueSerializer);


        ByteBuffer buffer = orig.buffer;

        buffer.position(0);

        for (int i=0;i<orig.totalElements;i++)
        {
            if (buffer.get()==OCCUPIED) {
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


        destroyBuffer(orig.buffer);  // without this the direct buffer stays in memory.

        return resized;




    }

    public static void destroyBuffer(Buffer buffer) {
        if(buffer.isDirect()) {
            try {
                if(!buffer.getClass().getName().equals("java.nio.DirectByteBuffer")) {
                    Field attField = buffer.getClass().getDeclaredField("att");
                    attField.setAccessible(true);
                    buffer = (Buffer) attField.get(buffer);
                }

                Method cleanerMethod = buffer.getClass().getMethod("cleaner");
                cleanerMethod.setAccessible(true);
                Object cleaner = cleanerMethod.invoke(buffer);
                Method cleanMethod = cleaner.getClass().getMethod("clean");
                cleanMethod.setAccessible(true);
                cleanMethod.invoke(cleaner);
            } catch(Exception e) {
                throw new RuntimeException("Could not destroy direct buffer " + buffer, e);
            }
        }
    }



}
