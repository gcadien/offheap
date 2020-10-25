import offheapmap.EfficientHashMap;
import offheapset.EfficientHashSet;
import offheapstack.EfficientStack;
import org.junit.Test;
import util.IntegerSerializer;
import util.StringSerializer;

import java.util.HashSet;
import java.util.Set;

public class StackTester {


    @Test public void testPushNPop()
    {
        StringSerializer ser = new StringSerializer();

        EfficientStack<String> stack = new EfficientStack<>(100, 1000, ser);

        for (int i=0;i<10;i++)
        {
            stack.push("Hello" + i);
        }


        assert (stack.size()==10);


        for (int i=0;i<10;i++)
        {
            assert(stack.pop().equals("Hello"+(9-i)));
        }

        assert (stack.size()==0);



    }


    @Test public void testReSize()
    {
        //StringSerializer ser = new StringSerializer();
        IntegerSerializer iser = new IntegerSerializer();

        EfficientStack<Integer> map = new EfficientStack<>(100, 100, iser);

        int orig = map.size();

        for (int i=0;i<1000;i++) {

            map.push(i);
        }


        int curr = map.size();

        assert(curr>orig);

    }


}
