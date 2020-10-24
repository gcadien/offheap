package util;

import offheapmap.Serializer;

public class JSONSerializer implements Serializer {

    Class<?> clazz;
    public JSONSerializer(Class<?> clazz)
    {
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(Object obj) {

        String str = JSONUtil.toJSON(obj);

        return str.getBytes();
    }

    @Override
    public Object deserialize(byte[] bytes) {

        String str = new String(bytes);
        return JSONUtil.fromJSON(str,clazz);
    }
}
