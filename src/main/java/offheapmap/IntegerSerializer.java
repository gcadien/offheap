package offheapmap;

import com.google.common.primitives.Ints;

public class IntegerSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {

        Integer i = (Integer) obj;

        return Ints.toByteArray(i);
    }

    @Override
    public Object deserialize(byte[] bytes) {

        return Ints.fromByteArray(bytes);
    }
}
