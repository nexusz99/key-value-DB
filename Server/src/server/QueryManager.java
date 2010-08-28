package server;

import java.util.concurrent.SynchronousQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryManager {

    public static SynchronousQueue<Data> queue = new SynchronousQueue<Data>();
    public Data command_parser(String query)
    {
        Data data = new Data();
       
        return data;
     }
     
}

 
