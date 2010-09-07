package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class StorageManager extends Thread{
    
    public static Map<Object,HashMap<Object,Data>> table_tree = Collections.synchronizedMap(new HashMap<Object,HashMap<Object,Data>>());
    public static Map<String,Long> table_index = Collections.synchronizedMap(new HashMap<String,Long>());

    public static long lastIndexpointer;
    private int tableCount;
    File f = new File("data.nxz");
    RandomAccessFile access;

    private final int fileSize = 10240000;

    @Override
    public void run() {

    }

    public Data search(Data d)
    {
        Data ret = null;
        //디스크에서 찾는 루틴
        return ret;
    }

    public void Storage_Start() {

        try
        {
            boolean b = f.createNewFile();
            access = new RandomAccessFile(f, "rw");
            if(b){
                access.setLength(fileSize);
                access.writeInt(0);
                access.writeChar(0);
            }
            initIndex();
            initTree();
        }
        catch(FileNotFoundException e)
        {}
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }

    }

    private void initIndex()
    {
        PageIndex pi = new PageIndex();
        ByteArrayInputStream bio;
        ObjectInputStream obj;
        byte[] read;
        try {
            int indexSize = access.readInt(); //인덱스의 총 길이를 읽어옴
            tableCount = access.readChar();
            read = new byte[indexSize];
            access.read(read);
            bio = new ByteArrayInputStream(read);
            obj = new ObjectInputStream(bio);
            while((pi = (PageIndex)obj.readObject())!=null)
            {
                table_index.put(pi.table,pi.pointer);
            }
            bio.close(); obj.close();
            pi = null; bio = null; obj = null; read = null;

        } catch (IOException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch(ClassNotFoundException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTree()
    {
        byte[] buf = null;

        Set<Map.Entry<String,Long>> set = table_index.entrySet();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(buf);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bis);
        } catch (IOException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        long pointer;
        String table;
        Page p;

        for(Map.Entry<String,Long> index : set )
        {
            table = index.getKey();
            pointer = table_index.get(table);
            try {
                access.seek(pointer); //해당 포인터로 이동
                buf = new byte[4];
                access.read(buf); //인덱스부분
                while((p=(Page)ois.readObject())!=null)
                {

                }

            } catch (IOException ex) {
                Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch(ClassNotFoundException e){
                Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, e);
            }

        }
    }

    private void writeTable(String table)
    {
        PageIndex pi = new PageIndex();

        try {
            pi.id = tableCount++;
            pi.pointer = access.getFilePointer();
            pi.table = table;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(pi);
            oos.reset();
            byte[] buf = bos.toByteArray();
            access.write(buf);
            lastIndexpointer = access.getFilePointer();
            bos.close(); oos.close();

        } catch (IOException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
}


