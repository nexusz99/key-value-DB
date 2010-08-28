package server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class StorageManager extends Thread{
    public static Map<Object,Data> memory = Collections.synchronizedMap(new HashMap<Object,Data>());

    @Override
    public void run() {

    }

    public Data search(Data d)
    {
        Data ret = null;
        //디스크에서 찾는 루틴
        return ret;
    }
}


