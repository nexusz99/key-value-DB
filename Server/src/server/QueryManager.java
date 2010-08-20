package server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.Queue;

public class QueryManager {
    
    public static Queue query = new Queue();
   
    public void Push(Data data)
    {
        query.enqueue(data);
    }
    public Data command_parser(String query)
    {
        Data data = new Data();
        boolean chk=false;
        String[] pattern = {"^(insert) key=\"([a-z]+)\" value=\"([a-z]+)\"","^(delete) key=\"([a-z]+)\"",
        "^(update) key=\"([a-z]+)\" value=\"([a-z]+)\"","^(search).key=\"([a-z]+)\""};

        for(int i=0;i<pattern.length;i++)
        {
            Pattern p = Pattern.compile(pattern[i]);
            Matcher m = p.matcher(query);
            while(m.find())
            {
                try
                {
                    data.cmd = m.group(1);
                    data.key = m.group(2);
                    data.value = m.group(3);
                    System.out.println("Command : "+data.cmd+", key = "+data.key+", value = "+data.value);
                }
                catch(Exception e)
                {
                    data.cmd = m.group(1);
                    data.key = m.group(2);
                    System.out.println("Command : "+data.cmd+", key = "+data.key);
                }
                chk = true;
            }
        }
        return data;

    }
    


}
