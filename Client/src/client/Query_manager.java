package client;

import java.util.StringTokenizer;

/*
 * 쿼리를 해석하고 서버로 보내고, 결과값을 반환받아 프로그램으로 넘기는 Thread
 */

public class Query_manager{
   
    public int command_parser(String query)
    {
        int ret=-1;
        String temp;
        temp = query.split(" ")[0];
        if(temp.compareTo("insert")==0)
            ret = 0;
        else if(temp.compareTo("delete")==0)
            ret = 1;
        else if(temp.compareTo("update")==0)
            ret = 2;
        else if(temp.compareTo("search")==0)
            ret = 3;
        else if(temp.compareTo("help")==0)
            ret = 4;

        return ret;
    }

    public Data Structing_Data(int cmd,String a)
    {
        Data data = null;
        switch(cmd)
        {
            case 0:
                data = Insert_Update(a);
                break;
            case 1:
                break;
            case 2:
                data = Insert_Update(a);
                break;
            case 3:
                break;
        }
        return data;
    }

    private Data Insert_Update(String a)
    {
        Data d = null;
        String key;
        String value;
        String temp;
        StringTokenizer token ;
        temp = a.split(" ")[1];
        key = temp.split("=")[1];
        System.out.println(key);

        return d;
    }

}

