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
           data = QueryManager.queue.poll();
           cmd = data.cmd;
           key = data.key;
           value = data.value;

           switch(cmdtype(cmd))
           {
               case 0: //insert
                   b = insert(data);
                   if(!b)
                   {
                       //디스크에서 데이터 찾기
                   }

                   break;
               case 1: //delete
                   b = delete(data);
                   if(!b)
                   {
                       //디스크에서 데이터 찾기
                   }

                   break;
               case 2: //update
                   b = update(data);
                   if(!b)
                   {
                       //디스크에서 데이터 찾기
                   }

                   break;
               case 3: //search
                   data = search(data);
                   if(data==null)
                   {
                       // 디스크에서 데이터 찾기
                   }
                   else
                   {

                   }
                   break;
               default:
                   break;
           }
           if(b)
           {
               //소켓으로 성공적으로 완료되었다고 전송
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
        else
            type = -1;
        return type;
    }

    private boolean insert(Data d)
    {
       boolean ret=false;
       boolean b = StorageManager.memory.containsKey(d);
       if(!b)
       {
           d.point = 0;
           d.update=true;
           d.del = false;
           StorageManager.memory.put(d.key, d);
           ret = true;
       }
       return ret; //false 이면 메모리에 해당 값이 없거나 아에 없는 것이므로 디스크에서 검색을 해봐야함
    }

    private boolean update(Data d)
    {
        boolean ret = false;
        Data data;
        if((data = StorageManager.memory.get(d))!=null && data.del==false)
        {
            data.value = d.value;
            data.point++;
            data.update = true;
            ret = insert(data);
        }
        return ret; // false일 경우 메모리에 해당 값이 없거나 아에 없는 것이므로 디스크에서 검색을 해봐야함
    }

    private boolean delete(Data d)
    {
        boolean ret = false;
        if(StorageManager.memory.containsKey(d.key)==true)
        {
            Data a = StorageManager.memory.get(d.key);
            a.del = true;
            a.update = true;
            ret = true;
        }
        return ret; // false일 경우 메모리에 해당 값이 없거나 아에 없는 것이므로 디스크에서 검색을 해봐야함
    }

    private Data search(Data d)
    {
        Data ret=null;
        if((ret = StorageManager.memory.get(d.key))!= null){}
        return ret;
    }
}
