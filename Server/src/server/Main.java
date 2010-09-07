
package server;




//단지 연결을 받아서 클라이언트 리스트에 추가.
public class Main {
    
    public static void main(String[] args) {
        Worker work = new Worker();
        StorageManager sm = new StorageManager();

        sm.Storage_Start();

        /*work.setDaemon(true);
        sm.setDaemon(true);

        work.start();      
        sm.start();
       
        Client_Manager cm = new Client_Manager();
        cm.initLog();
        cm.iniServer();
        cm.startServer();*/


        
    }

}
