
package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Test_Socket {

    Socket sock;
    DataOutputStream out;
    public Test_Socket(Socket sock) throws IOException
    {
        this.sock = sock;
        out = new DataOutputStream(this.sock.getOutputStream());
    }

    public void Send(String a) throws IOException
    {
        out.flush();
        out.writeUTF(a);
        //out.flush();
    }
}
