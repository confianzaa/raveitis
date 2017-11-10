package excal.rave.Assistance;

import java.io.Serializable;
import java.net.Socket;

/**
 * Created by hp on 1/19/2017.
 */

public class MySocket implements Serializable {
    private static Socket socket;

    public MySocket(Socket s){
        socket = s;
    }

    public static void setSocket(Socket s){
        socket = s;
    }

    public static  Socket getSocket(){
        return socket;
    }

}
