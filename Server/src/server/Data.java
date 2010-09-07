package server;

import java.nio.channels.SocketChannel;

public class Data{
    String cmd;
    String key;
    String value;
    String table;
    long point;
    boolean del; //이 데이터가 삭제됬는지에 대한 플래그
    boolean update; //업데이트가 되었는지 체크 -> 디스크에 쓸지 안쓸지 결정
    SocketChannel sock; //update시 보낼 sock
}
