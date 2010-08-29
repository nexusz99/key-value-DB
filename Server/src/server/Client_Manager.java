

package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Client_Manager {

     private static final String HOST="localhost";
     private static final int PORT = 30621;
        
     private static FileHandler fileHandler;
     private static final Logger logger = Logger.getLogger("NXZ.DB");
        
     private Charset charset = Charset.forName("EUC-KR");
     private CharsetDecoder decoder = charset.newDecoder();
     
     private Selector selector = null;
     private ServerSocketChannel serverSocketChannel = null;
     private ServerSocket serverSocket = null;
        
     private Vector client = new Vector();

     
     private QueryManager qm = new QueryManager();
     public void iniServer()
     {
         try
         {
            selector = Selector.open();
            //서버소켓채널 생성
            serverSocketChannel = ServerSocketChannel.open();
            //비블록킹 모드 설정
            serverSocketChannel.configureBlocking(false);
            //서버소켓채널과 연결된 서버소켓 가져오기
            serverSocket = serverSocketChannel.socket();

            //소켓 바인드
            InetSocketAddress isa = new InetSocketAddress(HOST,PORT);
            serverSocket.bind(isa);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

         }
         catch(IOException e)
         {
            log(Level.WARNING,"Client_Manager.initServer();",e);
         }

     }

    public void startServer()
    {
        info("Server is Started");
        try
        {
            while(true)
            {
                info("wait client");
                selector.select();

                //셀렉터에 준비된 이벤트들을 하나씩 처리
                Iterator it = selector.selectedKeys().iterator();
                while(it.hasNext())
                {
                    SelectionKey key = (SelectionKey)it.next();
                    if(key.isAcceptable())
                    {
                        accept(key);
                    }
                    else if(key.isReadable())
                    {
                        read(key);
                    }

                    it.remove(); // 이미 처리한 이벤트이니까 삭제
                }
            }
        }
        catch(Exception e)
        {
            log(Level.WARNING,"Client_Manager.startServer()",e);
        }
    }

    public void accept(SelectionKey key)
    {
        ServerSocketChannel server = (ServerSocketChannel)key.channel();
        SocketChannel sc;
        try
        {
            sc = server.accept();
            registerChannel(selector, sc, SelectionKey.OP_READ);
            info(sc.toString()+"접속");
        }
        catch(ClosedChannelException e)
        {
            log(Level.WARNING,"Client_Manager.accept()",e);
        }
        catch(IOException e)
        {
            log(Level.WARNING,"Client_Manager.accept()",e);
        }
    }

    public void registerChannel(Selector selector,SocketChannel sc,int ops) throws ClosedChannelException,IOException
    {
        if(sc==null)
        {
            info("invaild Connection");
            return;
        }
        sc.configureBlocking(false);
        sc.register(selector, ops);
        addClient(sc);
    }

    public void read(SelectionKey key) throws ClassNotFoundException
    {
        SocketChannel sc = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        Data data = null;
        try
        {
            int read = sc.read(buffer);
            buffer.flip();
            String temp = "";
            try
            {
                temp = decoder.decode(buffer).toString();
                if(temp.compareTo("")!=0)
                {
                    //System.out.println(temp);
                    data = qm.command_parser(temp); //쿼리를 받아서 처리..
                    data.sock = sc;
                    QueryManager.queue.put(data); //put 인가 add 인가..[To do]

                }else{
                    sc.close();
                }

            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
            
        }
        catch(IOException e)
        {
            try{sc.close();}catch(Exception e1){}
            removeClient(sc);
            info(sc.toString()+" is out");
            clearBuffer(buffer);
        }
    }

    public static void Write(SocketChannel s , ByteBuffer b)
    {
        b.flip();
        try
        {
            s.write(b);
        } catch (IOException ex)
        {
            //log(Level.WARNING,"Client_Manager.write()",ex);
        }
    }

    public void clearBuffer(ByteBuffer buffer)
    {
        if(buffer!=null){
            buffer.clear();
            buffer = null;
        }
    }

    public void addClient(SocketChannel sc)
    {
        client.add(sc);
    }
    public void removeClient(SocketChannel sc)
    {
        client.remove(sc);
    }

    public void initLog()
    {
        try
        {
            fileHandler = new FileHandler("Server.log");
        }
        catch(IOException e)
        {
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        }
    }
    public void log(Level level,String msg, Throwable error)
    {
        logger.log(level,msg,error);
    }

    public void info(String msa)
    {
        logger.info(msa);
    }
    
}
