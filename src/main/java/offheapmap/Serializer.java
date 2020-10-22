package offheapmap;

public interface Serializer {

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes);

}
