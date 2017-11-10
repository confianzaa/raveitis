package excal.rave.Assistance;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import excal.rave.Activities.Tab;
import excal.rave.R;
import  excal.rave.Activities.Party;

/**
 * Created by Karan on 02-01-2017.
 */

public class ReceiverForWifi extends BroadcastReceiver {
    private static String Tag = "ReceiverForWifi";
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    Activity activity;
    FragmentActivity fragActivity;
    Party party;
    Tab tab;

    public ReceiverForWifi(WifiP2pManager manager, Channel channel, Activity activity, Tab t) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        fragActivity = (FragmentActivity) activity;
//        party = p;
        tab = t;
    }


    /*public ReceiverForWifi(WifiP2pManager manager, WifiP2pManager.Channel channel, Reciever activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;f
    }*/
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                tab.setIsWifiP2pEnabled(true);
            } else {
                tab.setIsWifiP2pEnabled(false);
                tab.resetData();
            }
            Log.v(Tag, "P2P state changed - " + state);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
//                manager.requestPeers(channel, (WifiP2pManager.PeerListListener) fragActivity.getSupportFragmentManager()
//                        .findFragmentById(R.id.frag_list));
                manager.requestPeers(channel,Tab.listFragment);
                // onPeersAvailable() is called
            }
            Log.v(Tag, "P2P peers changed");
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Connection state changed!  We should probably do something about that.
            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // We are connected with the other device, request connection
                // info to find group owner IP
//                DeviceDetailFragment fragment = (DeviceDetailFragment) fragActivity
//                        .getSupportFragmentManager().findFragmentById(R.id.frag_detail);

                manager.requestConnectionInfo(channel,Tab.detailFragment);
            } else {
                // It's a disconnect
                //TODO: check
//                activity.resetData();
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
//            DeviceListFragment fragment = (DeviceListFragment) fragActivity.getSupportFragmentManager().findFragmentById(R.id.frag_list);
            Tab.listFragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }
}
