package excal.rave.Assistance;

import android.nfc.Tag;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import excal.rave.Activities.Tab;
import excal.rave.R;
import excal.rave.Activities.Party;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.widget.Toast;

/**
 * Created by Karan on 04-01-2017.
 */
public class DeviceListFragment extends ListFragment implements PeerListListener{
    private static String TAG = "DeviceListFragment";
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;
    public static boolean isListSet = false;

    public DeviceListFragment(){

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));
        isListSet = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);
        return mContentView;
    }

    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
    }

    private static String getDeviceStatus(int deviceStatus) {
        Log.d(Party.TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

        private List<WifiP2pDevice> items;

        /**
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public WiFiPeerListAdapter(Context context, int textViewResourceId,List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }
            WifiP2pDevice device = items.get(position);
            if (device != null) {
                TextView top = (TextView) v.findViewById(R.id.device_name);
                TextView bottom = (TextView) v.findViewById(R.id.device_details);
                if (top != null) {
                    top.setText(device.deviceName);
                }
                if (bottom != null) {
                    bottom.setText(getDeviceStatus(device.status));
                }
            }
            return v;
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d(Party.TAG, "No devices found");
            return;
        }
    }


    /**
     * Initiate a connection with the peer.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);

//        TODO: add dialog to dis/connect

        if(device.status!=WifiP2pDevice.CONNECTED){
            ((DeviceActionListener) getActivity()).showDetails(device);
            Log.v(TAG,"--connecting");
        }else{
            if(Tab.role.equals("SLAVE")) {
                Log.v(TAG,"--disconnecting");
                ((DeviceActionListener) getActivity()).disconnect();
            }
            else{
                Snackbar.make(mContentView,"People are enjoying and you are being selfish by leaving.. Too bad!",Snackbar.LENGTH_LONG);
                Toast.makeText(getActivity(), "People are enjoying and you are being selfish by leaving.. Too bad!", Toast.LENGTH_LONG).show();
            }
        }
        /*if(device.status != WifiP2pDevice.CONNECTED)
            ((DeviceActionListener) getActivity()).showDetails(device);
        else{

            ((DeviceActionListener) getActivity()).disconnect();
//            Toast.makeText(Tab.thisContext, "already connected", Toast.LENGTH_SHORT).show();
        }*/

    }


    /**
     * Update UI for this device.
     *
     * @param device WifiP2pDevice object
     */
    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
//        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
//        view.setText(device.deviceName);
//        view = (TextView) mContentView.findViewById(R.id.my_status);
//        view.setText(getDeviceStatus(device.status));
    }



    public void clearPeers() {
        if(peers!=null){
            peers.clear();
            ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }


    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public interface DeviceActionListener {

        void showDetails(WifiP2pDevice device);

        void cancelDisconnect();

        void connect(WifiP2pConfig config);

        void disconnect();
    }

}
