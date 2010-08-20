
package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Test_Socket {

    Socket sock;
    DataOutputStream out;
    public Test_Socket(Socket sock) throws IOException
    {
        this.sock = sock;
        out = new DataOutputStream(this.sock.getOutputStream());
    }

    public void Send(Data a) throws IOException
    {
        ObjectOutputStream st = new ObjectOutputStream(out);
        st.writeObject(a);
    }
}
