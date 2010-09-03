package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class Worker extends Thread{

    HashMap<Object,Data> tmpMap;
    SocketChannel sc;

    @Override
    public void run()
    {
        Data data = null;
        String cmd=null,key=null,value=null;
        ByteBuffer buf;
        int b;
        int i;
        while(true)
        {
           
           try
           {
               data = QueryManager.queue.poll();
               cmd = data.cmd;
               key = data.key;
               value = data.value;
               sc = data.sock;
               i=cmdtype(cmd);
               switch(i)
               {
                   case 0: //insert
                       b = insert(data);
                       switch(b)
                       {
                           case 0: //성공
                               
                               String f= "\nok";
                               try{
                               buf = ByteBuffer.allocate(30);
                               buf.put(f.getBytes());
                               buf.flip();
                               sc.write(buf);
                               buf.rewind();
                               }
                               catch(Exception e2){System.out.println(e2.getStackTrace());}
                               //buf=null;
                               break;
                           case 1: //해당 테이블 없음
                               break;
                           case 2: //해당 키 없음
                               break;
                       }
                       data = null;
                       break;
                   case 1: //delete
                       b = delete(data);
                       data = null;
                       break;
                   case 2: //update
                       b = update(data);
                       data = null;
                       break;
                   case 3: //search
                       data = search(data);
                       try
                       {
                           buf = ByteBuffer.allocate(30);
                           String f= data.value;
                           buf.put(f.getBytes());
                           buf.flip();
                           sc.write(buf);
                           buf.clear();
                           buf=null;
                       }
                       catch(Exception e)
                       {
                           System.out.println(e.getMessage());
                       }

                       break;
                   case 4: //create
                       b =create(data.table);
                       switch(b)
                       {
                           case 0: //성공
                               try
                               {
                                   buf = ByteBuffer.allocate(30);
                                   String f= "\nok";
                                   buf.put(f.getBytes());
                                   buf.flip();
                                   sc.write(buf);
                                   buf.clear();
                                   buf = null;
                               }
                               catch(Exception e)
                               {
                                   System.out.println(e.getMessage());
                               }
                               break;
                           case 1: //해당 테이블 없음
                               break;
                           case 2: //해당 키 없음
                               break;
                       }
                       break;
               }
               

           }
           catch(Exception e){
               //System.out.println(e.getMessage());
           }

        }
    }

    private int cmdtype(String cmd)
    {
        int type;
        if(cmd.compareTo("insert")==0)
            type = 0;
        else if(cmd.compareTo("delete")==0)
            type = 1;
        else if(cmd.compareTo("update")==0)
            type = 2;
        else if(cmd.compareTo("search")==0)
            type = 3;
        else if(cmd.compareTo("create")==0)
            type = 4;
        else
            type = -1;
        return type;
    }

    
    private int insert(Data d)
    {
       int ret = -1;
       tmpMap = Table_Check(d.table);
       if(tmpMap!=null)
       {
            Data t;
            if((t=find(d.key,tmpMap))==null)
            {
                d.point++;
                d.update = true;
                tmpMap.put(d.key, d);
                ret = 0;
            }
            else
            {
                if(t.del)
                {
                    t.del=false;
                    t.update = true;
                    t.value = d.value;
                    ret = 0;
                }else{
                    ret = 2; //이미 존재하는 키
                }
            }
       }
       else
       {
            ret = 1; //테이블 없음
       }
       tmpMap = null;
       return ret;
    }

    private int update(Data d)
    {
        int ret=-1;
        tmpMap = Table_Check(d.table);
        if(tmpMap!=null)
        {
            Data t = find(d.key, tmpMap);
            if(t!=null && !t.del)
            {
                t.value = d.value;
                t.point++;
            }
            else
            {
                ret = 2; //해당 키 없음
            }
        }
        else
        {
            ret = 1; //Table not exist
        }
        tmpMap = null;
        return ret;
    }

    private int delete(Data d)
    {
        int ret = -1;
        tmpMap = Table_Check(d.table);
        if(tmpMap!=null){
            Data t = find(d.key,tmpMap);
            if(t!=null && !t.del)
            {
                t.del = true;
                t.update = true;
            }else if(t==null){
                ret = 2; //해당 키 없음..
            }
        }else{
            ret = 1; //table not exist
        }
        tmpMap = null;
        return ret;
    }

    private Data search(Data d)
    {
        Data ret = null;
        tmpMap = Table_Check(d.table);
        if(tmpMap!=null)
        {
            ret = find(d.key,tmpMap);
        }
        return ret;
    }

    private int create(String table)
    {
        int ret;
        ret = Create_Table(table);
        return ret;
    }

    private Data find(String key,HashMap<Object,Data> m) //테이블메모리에서 원하는 키/벨류 쌍 찾기
    {
        Data b = null;
        b = m.get(key);
        return b;
    }

    private HashMap<Object,Data> Table_Check(String table)
    {
        HashMap<Object,Data> ret = null;
        ret = StorageManager.table_tree.get(table);
        if(ret==null)
        {
            int i = check_index(table);
            if(i==0)
            {
                ret = Load_Table_From_Disk(i);
            }
        }

        return ret;
    }

    private int check_index(String table)
    {
        int i = -1;
        return i;
    }
    private HashMap<Object,Data> Load_Table_From_Disk(int index)
    {
        HashMap<Object,Data> map = new HashMap<Object, Data>();
        return map;
    }

    private int Create_Table(String table)
    {
        int ret = -1,i;
        tmpMap = Table_Check(table);
        if(tmpMap==null)
        {
            i = check_index(table);
            if(i==0)
                ret = 1; //이미 존재하는 테이블
            else{
                tmpMap = new HashMap<Object, Data>();
                StorageManager.table_tree.put(table, tmpMap);
                ret = 0;
            }
        }else{
            ret = 1; //이미 존재하는 테이블
        }
        return ret;
    }
}
