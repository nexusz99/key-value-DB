
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;

public class Main {

    public static Query_manager qm ;
    public static void main(String[] args) {
        int cmd=-1;
        String input="";
        System.out.print("연결할 서버 IP : ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try
        {
            input = in.readLine();
            //Init_Connection(input);
            test_init();
        }
        catch(IOException e)
        {
        }

        while(true)
        {
            System.out.print(">");
            try
            {
                input = in.readLine();          
                cmd = qm.command_parser(input);
                

            }
            catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
        
    }

    static void Init_Connection(String ip)
    {
        
        try
        {
            Socket sock = new Socket(ip,30621); 
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

    static void test_init()
    {
        qm = new Query_manager();
    }

}



