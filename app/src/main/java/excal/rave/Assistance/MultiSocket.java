package excal.rave.Assistance;

import android.util.Pair;

import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by hp on 1/20/2017.
 */

public class MultiSocket {
    private static ArrayList<Pair<Integer,Socket> > list = new ArrayList<>();
    private static int key = 0;

    private MultiSocket(){

    }

    public static int setSocket(Socket socket){
        Pair<Integer,Socket> p = new Pair<>(key,socket);
        list.add(p);
        return key++;
    }

    public static Socket getSocket(int k){
        Pair<Integer,Socket> p = list.get(k);
        return p.second;
    }

    public static void clearList() {
        list.clear();
        key = 0;
    }
}
