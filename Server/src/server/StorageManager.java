package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class StorageManager extends Thread{
    
    public static Map<Object,HashMap<Object,Data>> table_tree = Collections.synchronizedMap(new HashMap<Object,HashMap<Object,Data>>());
    public static Map<String,Long> table_index = Collections.synchronizedMap(new HashMap<String,Long>());

    public static long lastIndexpointer=0; //마지막으로 읽어들인 인덱스를 가르키는 포인터 -> 새로운 테이블 인덱스 추가에 쓰임
    final long firstPageEndaddress = 10239996;//첫번째 페이지 마지막 주소
    final long pageSize = 10000; //10 kb
    public int tableCount=0;
    final File f = new File("data.nxz");
    RandomAccessFile access=null;

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
            //f.delete();
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

    /*
     * 파일의 인덱스(즉 테이블목록)을 초기화 하는 루틴이다.
     */
    private void initIndex()
    {
        TableIndex pi = new TableIndex();
        ByteArrayInputStream bio = null;
        ObjectInputStream obj=null;
        byte[] read;
        try {
            long indexSize = access.readLong(); //인덱스의 총 길이를 읽어옴
            tableCount = access.readChar();
            read = new byte[(int)indexSize];
            access.seek(10);
            access.read(read);

            lastIndexpointer = access.getFilePointer();
            bio = new ByteArrayInputStream(read);
            obj = new ObjectInputStream(bio);
            while((pi = (TableIndex)obj.readObject())!=null)
            {
                table_index.put(pi.table,pi.pointer);
            }
            

        } catch (IOException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch(ClassNotFoundException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }finally{try {
                bio.close(); obj.close();
            } catch (IOException ex) {
                Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            pi = null; bio = null; obj = null; read = null;}
    }

    /*
     * 실제 해쉬맵의 트리구조를 형성하는 루틴이다.
     */
    private void initTree()
    {
        byte[] buf = null;

        Set<Map.Entry<String,Long>> set = table_index.entrySet();
        
        long pointer;
        String table;
        Page p;

        for(Map.Entry<String,Long> index : set )
        {
            table = index.getKey();
            pointer = index.getValue();
            table_tree.put(table, new HashMap<Object,Data>());
            try {
                // 이부분을 루틴으로 만들어버리자. (ReadTable)으로..
                HashMap<String,Long> pimap = ReadTableIndex(pointer);
                ReadPage(pimap, table,pointer);

            }  catch(Exception e){}

        }
    }

    public void writeTableindex(String table)
    {
        TableIndex ti = new TableIndex();

        try {
            ti.id = tableCount++;
            ti.pointer = firstPageEndaddress - (pageSize*tableCount); //페이지의 맨 첫 부분
            ti.table = table;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(ti);
            oos.reset();
            byte[] buf = bos.toByteArray();
            access.seek(lastIndexpointer);
            access.write(buf); //  테이블을 디스크에 씀
            lastIndexpointer = access.getFilePointer();
            long indexSize = lastIndexpointer - 10; //file information부분 : 10바이트

            //파일의 처음으로가서 인덱스 사이즈 조정 & 테이블 갯수 조정.
            access.seek(0);
            access.writeLong(indexSize);
            access.writeChar(tableCount);
            System.out.println(access.getFilePointer());
            bos.close(); oos.close();

        } catch (IOException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /*
     * 페이지(테이블)인덱스를 쓰는 루틴
     *
     * 1. 인덱스 작성 주소로 이동
     * 2. 페이지클래스에 기록된 내용을 오브젝트 스트림으로 부앜
     */
    private void writePageindex(PageIndex p, ByteArrayOutputStream bos)
    {

        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            PageIndex pi = new PageIndex();
            pi.key = p.key.toString();
            pi.pointer = (long)p.pointer;
            oos.writeObject(pi);

        } catch (IOException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    /*페이지 데이터를 쓰는 루틴
     * 1. 쓰려는 페이지 어드레스로 접근(주소는 페이지의 맨 마지막 주소)
     * 2.
     */
    public void writePage(long address,HashMap<Object,Data> map)
    {
        PageIndex pi = new PageIndex();
        Page p;
        long currentAddress=address+pageSize; //페이지의 마지막 부분
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos =null;

        try {
            oos = new ObjectOutputStream(bos);
            if(map.isEmpty()) //비어있다면 처음 만든 페이지
            {
                //none.. 아무것도 안함.. 그냥 코드 보기 편하라고.ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ
            }
            else //기존에 존재하던 페이지
            {
                HashMap<String,Long> indexMap;
                indexMap = ReadTableIndex(address);
                Set<Map.Entry<Object, Data>> entrySet = map.entrySet();
                int tmpsize=0;
                for(Map.Entry<Object,Data> dmap : entrySet)
                {
                    Data d = dmap.getValue();
                    p = new Page();
                    p.point = d.point;
                    p.value = d.value;
                    if(!d.del) //삭제가 되지 않았다면
                    {
                        oos.writeObject(p);
                        byte[] tmp = bos.toByteArray();
                        currentAddress -= (tmp.length - tmpsize); //페이지데이터를쓰는 위치가 이상하다?
                        indexMap.put(d.key, currentAddress);
                        tmpsize = tmp.length;
                    }
                    else
                    {
                        indexMap.remove(d.key);
                        p=null;
                    }

                }
                byte[] buf = bos.toByteArray();
                access.seek(currentAddress);
                access.write(buf);
                Set<Map.Entry<String,Long>> indexSet = indexMap.entrySet();

                bos.reset();
                for(Map.Entry<String,Long> imap : indexSet)
                {
                    pi.key = imap.getKey();
                    pi.pointer = imap.getValue();
                    writePageindex(pi,bos);
                }
                buf = bos.toByteArray();
                access.seek(address);
                access.writeInt(buf.length);
                access.write(buf);
            }
            oos.close(); bos.close();

        } catch (IOException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public HashMap<String,Long> ReadTableIndex(long address) //리턴 = 구성된 인덱스 맵.
    {
        HashMap<String,Long> indexMap = new HashMap<String, Long>();
        try {
            access.seek(address);
            int size = access.readInt();
            byte[] buf = new byte[size];
            access.read(buf);
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bis);
            PageIndex pi;
            while((pi = (PageIndex)ois.readObject())!=null)
            {
                indexMap.put(pi.key,pi.pointer);
            }
            bis.close(); ois.close();
        } catch (IOException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(ClassNotFoundException ex)
        {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return indexMap;
    }

    private void ReadPage(HashMap<String,Long> pimap,String table,Long startAddress)
    {
        Data d = new Data();
        Page p = null;
        long totalSize=0;
        long firstPointer=0;
        long lastAddress = startAddress + pageSize; //페이지의 끝지점
        ArrayList<String> key = new ArrayList<String>();
        Set<Map.Entry<String,Long>> pimapSet = pimap.entrySet();
        for(Map.Entry<String,Long> pimapEntry: pimapSet)
        {
            key.add(pimapEntry.getKey());
            if(totalSize==0) firstPointer = pimapEntry.getValue();
            totalSize += (lastAddress-pimapEntry.getValue());
        }
        byte[] buf = new byte[(int)totalSize];
        ByteArrayInputStream bis = new ByteArrayInputStream(buf);
        ObjectInputStream ois = null;
        HashMap<Object,Data> dmap = table_tree.get(table);
        try {
            access.seek(firstPointer);
            access.read(buf);
            ois = new ObjectInputStream(bis);
            
            int i = 0;
            while((p=(Page)ois.readObject())!=null)
            {
                d.key = key.get(i++);
                d.point = p.point;
                d.value = p.value;
                d.update = false;
                d.del = false;
                d.table = table;
                dmap.put(d.key, d);
            }
            ois.close();bis.close();
        } catch (IOException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex){
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }finally{

        }


    }
    
}


