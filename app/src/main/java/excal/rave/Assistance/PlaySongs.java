package excal.rave.Assistance;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import excal.rave.Activities.SampleActivity;
import excal.rave.Activities.Tab;
import excal.rave.R;

import static android.view.View.GONE;
import static excal.rave.Assistance.DeviceDetailFragment.client_list;

/**
 * Created by Karan on 14-01-2017.
 */

public class PlaySongs implements Runnable, View.OnClickListener,SeekBar.OnSeekBarChangeListener{
    public Button playButton, nextButton, previousButton;
    public MediaPlayer mp;
    private SeekBar seekBar;
    private Context context;
    private View activity;
    int position = -1;
    int prevPosition = -1;
    private List<Song> playlist;
    private View previousView;
    //private List<View> selectedViewsList;
    private RecyclerView songs_list;
    public PlaySongs(Context context, View activity) {
        this.context = context;
        this.activity = activity;
    }

    public void init() {
        previousButton = (Button) activity.findViewById(R.id.prev_button);
        playButton = (Button) activity.findViewById(R.id.play_button);
        nextButton = (Button) activity.findViewById(R.id.next_button);
        songs_list = (RecyclerView) activity.findViewById(R.id.songs_list);
        seekBar = (SeekBar) activity.findViewById(R.id.seekBar);

        if(Tab.role.equals("SLAVE")){
            previousButton.setVisibility(GONE);
            playButton.setVisibility(GONE);
            nextButton.setVisibility(GONE);
        }

        seekBar.setEnabled(true);
        seekBar.setProgress(0);
        mp = new MediaPlayer();
        // this.selectedViewsList = SampleActivity.selectedViewsList;
        /*songs_list.getV*/
        // Toast.makeText(context,"Size of selectedViewsList is "+selectedViewsList.size(),Toast.LENGTH_SHORT).show();
    }
    public void setListeners(){
        if(Tab.role.equals("MASTER")){
            playButton.setOnClickListener(this);
            previousButton.setOnClickListener(this);
            nextButton.setOnClickListener(this);
            seekBar.setOnSeekBarChangeListener(this);
        }
    }
    public void listToBePlayed(List<Song> playlist) {
        this.playlist = playlist;
    }
    public void setPreviousView(View previousView) {
        this.previousView = previousView;
    }
    public View getPreviousView() {
        return previousView;
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        try {
            if (mp.isPlaying() || mp != null) {
                if (b){
                    mp.seekTo(i);
                    sendToClient("position",i);
                }
            } else if (mp == null) {
                Toast.makeText(context, "Media is not running", Toast.LENGTH_SHORT).show();
                seekBar.setProgress(0);
            }
        } catch (Exception e) {
            seekBar.setEnabled(false);
        }
    }

    public void sendToClient(String msg, int i) {
        for(Socket socket : client_list){
            if(socket!=null && socket.isConnected() && !socket.isClosed()){
                try {
                    OutputStream os = socket.getOutputStream();
                    DataOutputStream dout=new DataOutputStream(os);
                    dout.writeUTF(msg);
                    dout.flush();

                    if(!msg.equals("pause/resumeSong")){
                        dout.writeInt(i);
                        dout.flush();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void run() {
        int currentPosition = mp.getCurrentPosition();
        int total = mp.getDuration();
        while (mp != null && currentPosition < total) {
            try {
                Thread.sleep(100);
                currentPosition = mp.getCurrentPosition();
            } catch (Exception e) {
                return;
            }
            seekBar.setProgress(currentPosition);
//                sendToClient("position",currentPosition);
        }
    }
    /*public void getSelectedViews(List<View> selectedViews){
        this.selectedViews = selectedViews;
    }*/
    public void playSong(int position) {
        mp.reset();
        this.position=position;
        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(playlist.get(position).getData());
            mp.prepare();
            if(Tab.role.equals("MASTER")){
                Thread.sleep(800);
            }
            mp.start();
            seekBar.setMax(mp.getDuration());
            if(Tab.role.equals("MASTER")){
                Thread t = new Thread(this);
                t.start();
            }
            sendToClient("playSong",position);
        } catch (Exception e) {
            Toast.makeText(context, "Error in playing song", Toast.LENGTH_SHORT).show();
        }
    }
    /*public View getSelectedView(){
        return selectedViewsList.get(position);
    }*/
  /*  public void playSong(int position,int prevPosition) {
        mp.reset();
        this.position=position;
        this.prevPosition = prevPosition;
        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(playlist.get(position).getData());
            mp.prepare();
            mp.start();
            seekBar.setMax(mp.getDuration());
            Toast.makeText(context,"position = "+position+" previous Position is "+prevPosition,Toast.LENGTH_SHORT).show();
           *//* selectedViewsList.get(position).setBackgroundResource(R.color.selectedItem);
            if(prevPosition!=-1) {
                        selectedViewsList.get(prevPosition).setBackgroundResource(R.color.deselectedItem);
                        Toast.makeText(context,"inside previous position not equla not equal to 1 ",Toast.LENGTH_SHORT).show();
            }*//*
            Toast.makeText(context,"Thread Started",Toast.LENGTH_SHORT).show();
            Thread t = new Thread(this);
            t.start();
            Toast.makeText(context,"Thread Running",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error in playing song", Toast.LENGTH_SHORT).show();
        }
    }*/

    public void changePosition(int position){
        mp.seekTo(position);
        seekBar.setProgress(position);
    }

    public int getPrevPosition(){
        return prevPosition;
    }
    public void setPrevPosition(int prevPosition){
        Toast.makeText(context,"set previous position called",Toast.LENGTH_SHORT).show();
        this.position = prevPosition;
        this.prevPosition = prevPosition;
    }
    @Override
    public void onClick(View view) {
        //prevPosition = position;
        switch (view.getId()) {
            case R.id.play_button :{
                if(mp.isPlaying()){
                    playButton.setText("Resume");
                    mp.pause();
                } else {
                    playButton.setText("Pause");
                    if(Tab.role.equals("MASTER")){
                        try {
                            Thread.sleep(800);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mp.start();
                }
                sendToClient("pause/resumeSong",0);
                break;
            }
            case R.id.next_button:{
                previousView.setBackgroundResource(R.color.deselectedItem);
                //prevPosition = getPrevPosition();
                if((++position)>=playlist.size())
                    this.position = 0;
                /*else
                    this.position = this.position+1;*/
                Toast.makeText(context,"Previous Position is "+prevPosition,Toast.LENGTH_SHORT).show();
                Toast.makeText(context,"Position is "+position,Toast.LENGTH_SHORT).show();
                if(prevPosition!=-1)
                    songs_list.findViewHolderForAdapterPosition(prevPosition).itemView.setBackgroundResource(R.color.deselectedItem);
                songs_list.findViewHolderForAdapterPosition(position).itemView.setBackgroundResource(R.color.selectedItem);
                /* selectedViewsList.get(prevPosition).setBackgroundResource(R.color.deselectedItem);
                selectedViewsList.get(position).setBackgroundResource(R.color.selectedItem);*/
                playSong(position);
                prevPosition = position;
                break;
            }
            case R.id.prev_button :{
                previousView.setBackgroundResource(R.color.deselectedItem);
                //prevPosition = getPrevPosition();
                if(position==0)
                    this.position = playlist.size()-1;
                else this.position = this.position-1;
                Toast.makeText(context,"Previous Position is "+prevPosition,Toast.LENGTH_SHORT).show();
                Toast.makeText(context,"Position is "+position,Toast.LENGTH_SHORT).show();
                if(prevPosition!=-1)
                    songs_list.findViewHolderForAdapterPosition(prevPosition).itemView.setBackgroundResource(R.color.deselectedItem);
                songs_list.findViewHolderForAdapterPosition(position).itemView.setBackgroundResource(R.color.selectedItem);
                // selectedViewsList.get(prevPosition).setBackgroundResource(R.color.deselectedItem);
                // selectedViewsList.get(position).setBackgroundResource(R.color.selectedItem);
                playSong(position);
                prevPosition = position;
                break;
            }
        }
    }
}
