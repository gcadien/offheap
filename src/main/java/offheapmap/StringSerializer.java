package offheapmap;

public class StringSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {

        String str = (String) obj;

        return str.getBytes();
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return new String(bytes);
    }
}
