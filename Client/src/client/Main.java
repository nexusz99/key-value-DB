
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {

    public static Query_manager qm ;
    public static Test_Socket test ;
    public static Data data;
    public static void main(String[] args) {
        
        String input="";
        //System.out.print("연결할 서버 IP : ");
        qm = new Query_manager();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try
        {
            //input = in.readLine();
            Init_Connection(input);
            //test_init();
        }
        catch(Exception e)
        {
            System.exit(-1);
        }

        while(true)
        {
            System.out.print(">");
            try
            {
                input = in.readLine(); 
                test.Send(input);
            }
            catch(IOException e)
            {
                System.out.println(e.getStackTrace());
            }
        }
        
    }

    static void Init_Connection(String ip)
    {
        
        try
        {
            Socket sock = new Socket("127.0.0.1",30621);
            test = new Test_Socket(sock);
            System.out.println("System Message : 소켓 생성 성공");
        }
        catch(UnknownHostException e)
        {
            System.out.println(e.getStackTrace());
        }
        catch(IOException e)
        {
            System.out.println(e.getStackTrace());
        }

    }

    static void test_init()
    {
        qm = new Query_manager();
    }

}



