package server;

import java.util.concurrent.SynchronousQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryManager {

    public static SynchronousQueue<Data> queue = new SynchronousQueue<Data>();
    public Data command_parser(String query)
    {
        Data d = null;
        String[] q = new String[11];
        String pattern = "\\w+|={1}?";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(query);
        int i = 0;
        while(m.find())
        {
            q[i++] = m.group();
        }
        d = parser(q);
        return d;
    }

    private Data parser(String[] q)
    {
        Data d = new Data();
        if(q[0].compareTo("insert")==0) //insert | key | = | a | value | = | b | from | = | c
        {
            d.cmd = q[0]; d.key = q[3]; d.value = q[6]; d.table = q[9]; d.point=0; d.update=true;
        }
        else if(q[0].compareTo("delete")==0)//delete | key | = | a | from | = | b
        {
            d.cmd = q[0]; d.key = q[3]; d.table = q[6]; d.update=true; d.del=true;
        }
        else if(q[0].compareTo("update")==0)//update | key | = | a | value | = | b | from | = | c
        {
            d.cmd=q[0]; d.key = q[3]; d.value=q[6]; d.table = q[9]; d.point++; d.update=true;
        }
        else if(q[0].compareTo("search")==0)//search | key | = | a | from | = | b
        {
            d.cmd = q[0]; d.key=q[3]; d.table = q[6];
        }
        return d;
    }
     
}

 
