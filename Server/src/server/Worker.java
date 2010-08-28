package server;

public class Worker extends Thread{

    @Override
    public void run()
    {
        Data data = null;
        String cmd=null,key=null,value=null;
        boolean b=false;
        while(true)
        {
           
           try
           {
               data = QueryManager.queue.poll();
               cmd = data.cmd;
               key = data.key;
               value = data.value;
               switch(cmdtype(cmd))
               {
                   case 0: //insert
                       insert(data);
                       break;
                   case 1: //delete
                       delete(data);

                       break;
                   case 2: //update
                       update(data);

                       break;
                   case 3: //search
                       data = search(data);

                       break;
               }
               if(b)
               {
                   //소켓으로 성공적으로 완료되었다고 전송
               }
           }
           catch(Exception e){}

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
        else
            type = -1;
        return type;
    }

    
    private void insert(Data d)
    {
       Data tmp ;
       tmp = Memory_Check(d);
       if(tmp!=null) //true
       {
           if(tmp.del)
           {
               tmp.del = false;
               tmp.update = true;
               tmp.value = d.value;
               tmp.point=1;
           }
           else
           {
               //error !!
           }
       }
       else //false
       {
            boolean b;
            b = Disk_Check(d);
            if(!b) //false
            {
                d.point++;
                StorageManager.memory.put(d.key, d);
            }
            else //true
            {
                //error!!
            }
       }
    }

    private void update(Data d)
    {
        Data tmp;
        tmp = Memory_Check(d);
        if(tmp!=null) //true
        {
            tmp.value = d.value;
            tmp.point++;
        }
        else //false
        {
            boolean b;
            b = Disk_Check(d);
            if(b)
            {
                Load_From_Disk(d);
            }
            else
            {
                //error!
            }
        }

    }

    private void delete(Data d)
    {
        boolean b;

    }

    private Data search(Data d)
    {
        Data ret=null;
        if((ret = (Data) StorageManager.memory.get(d.key))!= null)
        {
            if(ret.del == true)
            {
                ret = null;
            }
        }
        return ret;
    }

    private Data Memory_Check(Data d)
    {
        Data b ;
        b = (Data) StorageManager.memory.get(d.key);
        return b;
    }

    private boolean Disk_Check(Data d)
    {
        boolean b = false;
        return b;
    }

    private Data Load_From_Disk(Data d)
    {
        Data load = null;;
        return load;
    }


}
