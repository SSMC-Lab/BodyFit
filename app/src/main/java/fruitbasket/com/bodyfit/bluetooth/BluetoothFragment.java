package fruitbasket.com.bodyfit.bluetooth;

import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public abstract class BluetoothFragment extends Fragment {
    private static final String TAG="BluetoothFragment";

    private Intent intentToBluetootthService;
    private BluetoothService bluetoothService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        intentToBluetootthService=new Intent(this.getContext(),BluetoothService.class);
    }

    private void showBluetootListDialog(){
        if(bluetoothService!=null){
            new AlertDialog.Builder(getContext()).setTitle("Bluetooth List")
                    .setAdapter(bluetoothService.getArrayAdapter(), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int which) {
                            bluetoothService.stopDiscovery();
                            bluetoothService.connectToDevice(bluetoothService.getDeviceArrayList().get(which).getAddress());
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface arg0) {
                    bluetoothService.stopDiscovery();
                }
            }).show();
        }
    }

    protected void startWork(){
        this.getActivity().bindService(intentToBluetootthService, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    protected void stopWork(){
        this.getActivity().unbindService(serviceConnection);
    }

    protected abstract void updateUI(Bundle bundle);

    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected()");
            bluetoothService=((BluetoothService.MyBinder)service).getService();
            bluetoothService.setHandler(handler);
            showBluetootListDialog();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"onServiceDisconnected()");
            bluetoothService=null;
        }
    };

    final Handler handler= new Handler() {
        private Bundle bundle;
        public void handleMessage(Message msg) {
            Log.i(TAG,"handleMessage()");
            bundle=msg.getData();
            updateUI(bundle);
        }
    };
}