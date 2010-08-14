package client;

import java.net.Socket;

/*
 * 이 클래스를 사용할 때 생성자로 Socket을 받아서 모든 소켓통신은 이 쓰레드가 관리한다.
 */

public class Connect_Server implements Runnable{
    Socket server;

    public Connect_Server(Socket s) {

        this.server = s;

    }

    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
