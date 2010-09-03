
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

public class Test_Socket {

    private static final String host = "localhost";
    private static final int port = 30621;
    
    private Selector selector = null;
    private SocketChannel sc = null;

    private Charset charset = null;
    private CharsetDecoder decoder = null;

    public Test_Socket(){
        charset = Charset.forName("EUC-KR");
        decoder = charset.newDecoder();
    }

    public void initConnection()
    {
        try
        {
            selector = Selector.open();
            sc = SocketChannel.open(new InetSocketAddress(host,port));
            sc.configureBlocking(false);
            sc.register(selector,SelectionKey.OP_READ);
            System.out.println("서버 연결 완료");
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    
    public void startClient()
    {
        startWriter();
        startReader();
    }
    
    private void startWriter()
    {
        Thread t = new Writer(sc);
        t.start();
    }
    private void startReader()
    {
        while(true)
        {
            try{
                selector.select();
                Iterator it = selector.selectedKeys().iterator();
                while(it.hasNext())
                {
                    SelectionKey key = (SelectionKey)it.next();
                    if(key.isReadable())
                    {
                        read(key);
                    }
                    it.remove();
                }
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
    
    private void read(SelectionKey key)
    {
        SocketChannel sc2 = (SocketChannel)key.channel();
        ByteBuffer buf = ByteBuffer.allocateDirect(1020);
        try
        {
            sc2.read(buf);
            buf.flip();
            try
            {
                String data = decoder.decode(buf).toString();
                System.out.println(data);
            }
            catch(CharacterCodingException a)
            {
                System.out.println(a.getMessage());
            }
        }
        catch(IOException e)
        {
            try{
                sc2.close();
            }
            catch(Exception a)
            {}
        }
    }
    
}

class Writer extends Thread
{
    private SocketChannel sc = null;
    public Writer(SocketChannel s)
    {
        sc = s;
    }
    @Override
    public void run() {
       ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
       BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
       while(!Thread.currentThread().isInterrupted())
       {
           try{
               buffer.clear();
               System.out.print(">");
               String input = in.readLine();
               buffer.put(input.getBytes());
               buffer.flip();
               sc.write(buffer);
           }
           catch(Exception e)
           {
               System.out.println(e.getMessage());
           }
           finally
           {
                buffer.clear();
           }
       }
    }
    
}
