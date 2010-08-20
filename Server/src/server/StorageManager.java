package server;

import java.util.Collections;
import java.util.HashMap;


public class StorageManager {
    public HashMap<Object,Data> query = (HashMap<Object, Data>) Collections.synchronizedMap(new HashMap<Object,Data>());
    

    private int insert()
    {
        int ret =0;
        return ret;
    }
    private int update()
    {
        int ret=0;
        return ret;
    }

    private int delete()
    {
        int ret = 0;
        return ret;
    }
    private int search()
    {
        int ret =0;
        return ret;
    }
}


