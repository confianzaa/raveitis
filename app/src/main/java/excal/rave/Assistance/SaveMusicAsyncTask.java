package excal.rave.Assistance;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import excal.rave.Activities.Party;
import excal.rave.R;

/**
 * Created by hp on 1/13/2017.
 */

public class SaveMusicAsyncTask extends AsyncTask<Void, Void, String> {
    private String Tag = "SaveMusicAsyncTask";
    private Socket socket;
    private Activity activity;
    private long fileSize;
    private String fileName;

    public SaveMusicAsyncTask(Socket socket, Activity activity, long fileSize, String fileName) {
        this.socket = socket;
        this.activity = activity;
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {

        InputStream istream = null;
        boolean copied = false;
        String path = null;
        try {
            istream = socket.getInputStream();
            final File f = new File(Environment.getExternalStorageDirectory() + "/Music/"+activity.getResources()
                    .getString(R.string.app_name) + "/" + fileName);
//                   + "/rave-" + System.currentTimeMillis() + ".mp3");
            path = f.toString();

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();

            Log.v(Tag, "copying music file " + path);
            copied = DeviceDetailFragment.copyFile(istream,new FileOutputStream(f), fileSize);
            if(copied){
                Song song = new Song();
                song.setData(path);
                song.setTitle(getFileName(path));
                ClientSocket.clientSongList.add(song);
            }
            Log.v(Tag,copied?"copied":"not copied");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return copied?fileName:"not copied";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //
        if (result != null) {
            Log.v(Tag,"File copied - " + result);

            NotificationManager manager= (NotificationManager)activity.getApplicationContext().getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(activity.getApplicationContext());
            builder.setAutoCancel(false);
            builder.setTicker("RAVE");
            builder.setContentTitle("RAVE");
            builder.setContentText(result);
            builder.setSmallIcon(R.drawable.rave);
//            builder.setContentIntent(sender);
            builder.setOngoing(false);
            builder.setSubText("Music file received");   //API level 16
            builder.build();

            Notification myNotication = builder.getNotification();
            manager.notify(11, myNotication);


            ClientSocket.noOfSongsReceived++;
            Log.v(Tag,"--noOfSongsReceived:"+ClientSocket.noOfSongsReceived);
            if(ClientSocket.noOfSongsReceived == ClientSocket.noOfSongsToReceive){
                ClientSocket.filesReceived = true;
                Log.v(Tag,"--filesReceived:true");
            }

            ClientSocket.isFileCopied = true;
        }else{
            Log.v(Tag,"null result");
        }
    }

    private String getFileName(String path) {
        String result = null;
        result = path;
        int cut = result.lastIndexOf('/');
        if (cut != -1) {
            result = result.substring(cut + 1);
        }

        return result;
    }
}
