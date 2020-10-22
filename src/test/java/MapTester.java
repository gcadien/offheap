import offheapmap.EfficientHashMap;
import org.junit.Test;
import util.IntegerSerializer;
import util.StringSerializer;

import java.util.HashMap;
import java.util.Map;

public class MapTester {


    @Test public void testNumElements()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 1000, iser, ser);

        map.put(1,"Hello");

        assert (map.size()==1);

        map.remove(1);

        assert(map.size()==0);

        map.put(1,"Again");

        assert(map.size()==1);

    }

    @Test public void testPutAll()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 1000, iser, ser);

        Map<Integer,String>  map1 = new HashMap<>();

        for (int i=0;i<100;i++) {

            map1.put(i, String.valueOf(i));

        }

        map.putAll(map1);

        assert (map.size()==100);


    }


    @Test public void testEmpty()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 1000, iser, ser);

        map.put(1,"Hello");

        assert (!map.isEmpty());

        map.remove(1);

        assert(map.isEmpty());

        map.put(1,"Again");

        assert(!map.isEmpty());

    }



    //TODO - see why its hanging
    @Test public void test1()  throws Exception {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 1000, iser, ser);


        System.out.println(Runtime.getRuntime().freeMemory());

        for (int j = 0; j < 10; j++) {

            for (int i = 0; i < 250; i++) {
                map.put(j * i, "Generic World int key " + (j * i));

                map.remove(j*i);


            }

            System.out.println(j);
            assert(map.isEmpty());


        }

    }


}
