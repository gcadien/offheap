import offheapstack.EfficientStack;
import org.junit.Test;
import priorityqueue.EfficientPriorityQueue;
import util.IntegerSerializer;
import util.StringSerializer;

public class PQTester {


    @Test public void testPushNPop()
    {
        StringSerializer ser = new StringSerializer();

        EfficientPriorityQueue<String> stack = new EfficientPriorityQueue<>(100, 1000, ser);

        for (int i=0;i<10;i++)
        {
            stack.add("Hello" + (9-i));
        }


        assert (stack.size()==10);


        for (int i=0;i<10;i++)
        {
            assert(stack.remove().equals("Hello"+(i)));
        }

        assert (stack.size()==0);



    }






}
