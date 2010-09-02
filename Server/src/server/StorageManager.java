package server;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class StorageManager extends Thread{
    
    public static Map<Object,HashMap<Object,Data>> table_tree = Collections.synchronizedMap(new HashMap<Object,HashMap<Object,Data>>());

    @Override
    public void run() {

    }

    public Data search(Data d)
    {
        Data ret = null;
        //디스크에서 찾는 루틴
        return ret;
    }

    public void Storage_Start(){
        File f = new File("data.nxz");
        if(!f.exists())
        {

        }
    }
}


