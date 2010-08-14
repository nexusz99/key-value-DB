
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {


    public static void main(String[] args) {
        String ip="";
        System.out.print("연결할 서버 IP : ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try
        {
            ip = in.readLine();
            Init_Connection(ip);
        }
        catch(IOException e)
        {
        }
        
    }

    static void Init_Connection(String ip)
    {
        Connect_Server server;
        try
        {
            Socket sock = new Socket(ip,30621);
            server = new Connect_Server(sock);
            server.run();
        }
        catch(UnknownHostException e)
        {
            System.out.println(e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }

    }

}
