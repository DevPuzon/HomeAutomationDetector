package com.softwaresolution.homeautomationdetector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Bluetooth extends Thread{

    private static String TAG = "TAGBluetooth";

    String address = null , name=null;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Context context;
    public Bluetooth(Context context ) {
        this.context = context;
        try {
            bluetooth_connect_device();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static IBluetooth iBluetooth;
    public static void getsave(IBluetooth iBluetooth1){
        iBluetooth = iBluetooth1;
    }

    public void bluetooth_connect_device() throws IOException {
//        try
//        {
//            Log.d(TAG,"start" );
//            myBluetooth = BluetoothAdapter.getDefaultAdapter();
//            address = myBluetooth.getAddress();
//            pairedDevices = myBluetooth.getBondedDevices();
//            if (pairedDevices.size()>0)
//            {
//                for(BluetoothDevice bt : pairedDevices)
//                {
//                    address=bt.getAddress().toString();
//                    name = bt.getName().toString();
//                    Toast.makeText(context,"Connected",
//                            Toast.LENGTH_LONG).show();
//                    Log.d(TAG,"loop name"+name+"loop address Address: "+address );
//                }
//            }else{
//                Toast.makeText(context,"Bluetooth not paired, please pair the blutooth HC-05 and refresh the application",
//                        Toast.LENGTH_LONG).show();
//            }
//            Log.d(TAG, String.valueOf(pairedDevices.size()));
//
//        }
//        catch(Exception ex){
//            Log.d(TAG,"excepe "+ex.getLocalizedMessage());
//        }
        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice("98:D3:C1:FD:46:14");//connects to the device's address and checks if it's available
        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
        mmInputStream = btSocket.getInputStream();
        mmOutputStream = btSocket.getOutputStream();
        btSocket.connect();
        beginListenForData();
    }


    volatile boolean stopWorker;
    int readBufferPosition;
    byte[] readBuffer;
    Thread workerThread;
    InputStream mmInputStream;
    static OutputStream mmOutputStream;

    public static void sendMessage(String msg){
        try {
            if (mmOutputStream != null){

                mmOutputStream.write(msg.getBytes());
            }
        } catch (IOException e) {
            Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
        }
    }
    private void beginListenForData() {
        final Handler
                handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            //Get Message
                                            Log.d(TAG,"DATA "+data);
                                            sendSensorData(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }
    private void sendSensorData(String data) {
        int startOfLine = data.indexOf("(");
        int endOfLineIndex = data.indexOf(")");
        Log.d(TAG,"DATA "+ data);
        Log.d(TAG, String.valueOf(endOfLineIndex));
        if (endOfLineIndex > 0 && startOfLine == 0) {
            data = data.replace("(","");
            data = data.replace(")","");
            data = data.replace("\r","");
            String[] saves = data.split(",");
            boolean[] savesbool = new boolean[5];
            Log.d(TAG,"saves bool"+ String.valueOf(saves.length));
            for (int i = 0 ; i<saves.length;i++){
                savesbool[i] = Boolean.valueOf(saves[i]);
                Log.d(TAG,"saves bool"+ String.valueOf(saves[i]));
            }
            Log.d(TAG,"saves bool"+ String.valueOf(savesbool.length));
            if (iBluetooth != null){
                iBluetooth.getSave(savesbool);
            }
        }
    }
    public  interface IBluetooth{
        void getSave(boolean[] save);
    }
}
