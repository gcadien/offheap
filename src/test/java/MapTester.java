import offheapmap.EfficientHashMap;
import org.junit.Test;
import util.IntegerSerializer;
import util.JSONSerializer;
import util.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Test public void testKeySet()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 1000, iser, ser);


        for (int i=0;i<100;i++) {

            map.put(i, String.valueOf(i));

        }



      //  System.out.println(map.keySet());

        assert(map.keySet().size()==100);


    }

    @Test public void testValues()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 1000, iser, ser);


        for (int i=0;i<100;i++) {

            map.put(i, "Hello World " + i);

        }


        assert(map.values().size()==100);

    }


    @Test public void testEntrySet()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 1000, iser, ser);


        for (int i=0;i<100;i++) {

            map.put(i, "Hello World " + i);

        }

        map.entrySet().stream().forEach(entry-> { System.out.println(entry.getKey() + "  " + entry.getValue()); });


        assert(map.values().size()==100);

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


    @Test public void testClear()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 1000, iser, ser);

        map.put(1,"Hello");

        map.clear();


        assert(map.isEmpty());

        map.put(1,"Again");

        assert(!map.isEmpty());

    }


    @Test public void testPut()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 1000, iser, ser);

        String prevStr = null;
        for (int i=0;i<10;i++) {
            String result = map.put(1, "Hello" + i);

            assert((result==null&& prevStr==null) || prevStr.equals(result));

            prevStr = "Hello" + i;


        }



    }


    @Test public void testRemove()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 1000, iser, ser);

        for (int i=0;i<10;i++) {

            String value = "Hello" + i;

             map.put(i, value);

             String oldRes = map.remove(i);

            assert(value.equals(oldRes));



        }



    }


    @Test public void testJSON()
    {


        JSONSerializer ser = new JSONSerializer(MyObject.class);

        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, MyObject> map = new EfficientHashMap<>(100, 1000, iser, ser);

        for (int i=0;i<10;i++) {

            MyObject myObject = new MyObject("Hello " , "World");

            map.put(i, myObject);

            MyObject res = map.get(i);

            assert(myObject.equals(res));



        }



    }







    @Test public void testReSize()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 100, iser, ser);

        int orig = map.size();

        for (int i=0;i<1000;i++) {

            map.put(i, "Hello");
        }


        int curr = map.size();

        assert(curr>orig);

    }

    @Test public void testContainsKeyAndValue()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 10000, iser, ser);

        for (int i=0;i<1000;i++) {

            map.put(i, "Hello" +i);
        }

        for (int i=0;i<1000;i++) {

            assert(map.containsKey(i));
            assert(map.containsValue("Hello"+i));
        }



    }


    @Test public void testMemMapallocation()
    {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        int size = Integer.MAX_VALUE;

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, size, iser, ser);

        // will throw an exception if cannot allocate .

    }





    @Test public void test1()  throws Exception {
        StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientHashMap<Integer, String> map = new EfficientHashMap<>(100, 1000, iser, ser);


        System.out.println(Runtime.getRuntime().freeMemory());

        for (int j = 0; j < 100; j++) {

            for (int i = 0; i < 250; i++) {
                map.put(j * i, "Generic World int key " + (j * i));

                map.remove(j*i);


            }

            System.out.println(j);
            assert(map.isEmpty());


        }

    }


}
