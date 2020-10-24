package util;

import offheapmap.Serializer;

import java.io.*;

public class GenericObjectSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {

        byte[] bytes =null;

        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
        )
        {
            out.writeObject(obj);
            out.flush();
            bytes = bos.toByteArray();

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return bytes;

    }

    @Override
    public Object deserialize(byte[] bytes) {

        try( ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis);
        )
        {
            Object o = in.readObject();
            return o;

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;

    }
}
