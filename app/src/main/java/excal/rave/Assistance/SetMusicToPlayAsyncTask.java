package excal.rave.Assistance;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.net.Socket;

import excal.rave.Activities.SelectedSongs;

/**
 * Created by hp on 1/20/2017.
 */

public class SetMusicToPlayAsyncTask extends AsyncTask<Void, Void, String> {
    private static String Tag = "SetMusicToPlayAsyncTask";
    private int songno;

    public SetMusicToPlayAsyncTask(int p) {
        songno = p;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected String doInBackground(Void... voids) {
        Log.v(Tag,"--song no received: "+songno);
//        DataInputStream din= null;
//        din = new DataInputStream(socket.getInputStream());
//            String s=din.readUTF();
        // cant do readUTF here.. coz parallely ClientSocket is waiting for readUTF

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        SelectedSongs.pSongs.playSong(songno);
    }
}
