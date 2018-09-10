package com.babbicool.lib.printerwrapper.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;


@SuppressLint("MissingPermission")
public class BluetoothService
{
  private static final String TAG = "BluetoothService";
  private static final boolean D = true;
  public static final int MESSAGE_STATE_CHANGE = 1;
  public static final int MESSAGE_READ = 2;
  public static final int MESSAGE_WRITE = 3;
  public static final int MESSAGE_DEVICE_NAME = 4;
  public static final int MESSAGE_CONNECTION_LOST = 5;
  public static final int MESSAGE_UNABLE_CONNECT = 6;
  private static final String NAME = "BTPrinter";
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  
  private final BluetoothAdapter mAdapter;
  
  private final Handler mHandler;
  
  private AcceptThread mAcceptThread;
  
  private ConnectThread mConnectThread;
  
  private ConnectedThread mConnectedThread;
  
  private int mState;
  
  public static final int STATE_NONE = 0;
  
  public static final int STATE_LISTEN = 1;
  public static final int STATE_CONNECTING = 2;
  public static final int STATE_CONNECTED = 3;
  
  public BluetoothService(Context context, Handler handler)
  {
    mAdapter = BluetoothAdapter.getDefaultAdapter();
    mState = 0;
    mHandler = handler;
  }
  




  public synchronized boolean isAvailable()
  {
    if (mAdapter == null) {
      return false;
    }
    return true;
  }
  
  public synchronized boolean isBTopen()
  {
    if (!mAdapter.isEnabled()) {
      return false;
    }
    return true;
  }
  

  public synchronized BluetoothDevice getDevByMac(String mac)
  {
    return mAdapter.getRemoteDevice(mac);
  }

  public synchronized BluetoothDevice getDevByName(String name)
  {
    BluetoothDevice tem_dev = null;
    Set<BluetoothDevice> pairedDevices = getPairedDev();
    if (pairedDevices.size() > 0) {
      for (BluetoothDevice device : pairedDevices) {
        if (device.getName().indexOf(name) != -1) {
          tem_dev = device;
          break;
        }
      }
    }
    return tem_dev;
  }
  
  public synchronized void sendMessage(String message, String charset) {
    if (message.length() > 0)
    {
      byte[] send;
      try {
        send = message.getBytes(charset);
      }
      catch (UnsupportedEncodingException e) {
        send = message.getBytes();
      }
      
      write(send);
      byte[] tail = new byte[3];
      tail[0] = 10;
      tail[1] = 13;
      write(tail);
    }
  }
  
  public synchronized Set<BluetoothDevice> getPairedDev()
  {
    Set<BluetoothDevice> dev = null;
    dev = mAdapter.getBondedDevices();
    return dev;
  }
  
  public synchronized boolean cancelDiscovery()
  {
    return mAdapter.cancelDiscovery();
  }
  
  public synchronized boolean isDiscovering()
  {
    return mAdapter.isDiscovering();
  }
  
  public synchronized boolean startDiscovery()
  {
    return mAdapter.startDiscovery();
  }
  

  private synchronized void setState(int state)
  {
    mState = state;
    

    mHandler.obtainMessage(1, state, -1).sendToTarget();
  }
  

  public synchronized int getState()
  {
    return mState;
  }
  



  public synchronized void start()
  {
    Log.d("BluetoothService", "start");
    

    if (mConnectThread != null) { mConnectThread.cancel();mConnectThread = null;
    }
    
    if (mConnectedThread != null) { mConnectedThread.cancel();mConnectedThread = null;
    }
    
    if (mAcceptThread == null) {
      mAcceptThread = new AcceptThread();
      mAcceptThread.start();
    }
    setState(1);
  }
  



  public synchronized void connect(BluetoothDevice device)
  {
    Log.d("BluetoothService", "connect to: " + device);
    

    if ((mState == 2) && 
      (mConnectThread != null)) { mConnectThread.cancel();mConnectThread = null;
    }
    

    if (mConnectedThread != null) { mConnectedThread.cancel();mConnectedThread = null;
    }
    
    mConnectThread = new ConnectThread(device);
    mConnectThread.start();
    setState(2);
  }
  




  public synchronized void connected(BluetoothSocket socket, BluetoothDevice device)
  {
    Log.d("BluetoothService", "connected");
    

    if (mConnectThread != null) { mConnectThread.cancel();mConnectThread = null;
    }
    
    if (mConnectedThread != null) { mConnectedThread.cancel();mConnectedThread = null;
    }
    
    if (mAcceptThread != null) { mAcceptThread.cancel();mAcceptThread = null;
    }
    
    mConnectedThread = new ConnectedThread(socket);
    mConnectedThread.start();
    

    Message msg = mHandler.obtainMessage(4);
    
    mHandler.sendMessage(msg);
    
    setState(3);
  }
  


  public synchronized void stop()
  {
    Log.d("BluetoothService", "stop");
    setState(0);
    if (mConnectThread != null) { mConnectThread.cancel();mConnectThread = null; }
    if (mConnectedThread != null) { mConnectedThread.cancel();mConnectedThread = null; }
    if (mAcceptThread != null) { mAcceptThread.cancel();mAcceptThread = null;
    }
  }
  



  public void write(byte[] out)
  {
    ConnectedThread r;
    

    synchronized (this) {
      if (mState != 3) return;
      r = mConnectedThread;
    }
    r.write(out);
  }
  


  private void connectionFailed()
  {
    setState(1);
    

    Message msg = mHandler.obtainMessage(6);
    mHandler.sendMessage(msg);
  }
  





  private void connectionLost()
  {
    Message msg = mHandler.obtainMessage(5);
    mHandler.sendMessage(msg);
  }
  


  private class AcceptThread
    extends Thread
  {
    private final BluetoothServerSocket mmServerSocket;

    @SuppressLint("MissingPermission")
    public AcceptThread()
    {
      BluetoothServerSocket tmp = null;
      
      try
      {
        tmp = mAdapter.listenUsingRfcommWithServiceRecord("BTPrinter", BluetoothService.MY_UUID);
      } catch (IOException e) {
        Log.e("BluetoothService", "listen() failed", e);
      }
      mmServerSocket = tmp;
    }
    
    public void run()
    {
      Log.d("BluetoothService", "BEGIN mAcceptThread" + this);
      setName("AcceptThread");
      BluetoothSocket socket = null;
      

      while (mState != 3) {
        Log.d("AcceptThread线程运行", "正在运行......");
        
        try
        {
          socket = mmServerSocket.accept();
        } catch (IOException e) {
          Log.e("BluetoothService", "accept() failed", e);
          break;
        }
        

        if (socket != null) {
          synchronized (BluetoothService.this) {
            switch (mState)
            {
            case 1: 
            case 2: 
              connected(socket, socket.getRemoteDevice());
              break;
            case 0: 
            case 3: 
              try
              {
                socket.close();
              } catch (IOException e) {
                Log.e("BluetoothService", "Could not close unwanted socket", e);
              }
            }
            
          }
        }
      }
      Log.i("BluetoothService", "END mAcceptThread");
    }
    
    public void cancel() {
      Log.d("BluetoothService", "cancel " + this);
      try {
        mmServerSocket.close();
      } catch (IOException e) {
        Log.e("BluetoothService", "close() of server failed", e);
      }
    }
  }
  

  private class ConnectThread
    extends Thread
  {
    private final BluetoothSocket mmSocket;
    
    private final BluetoothDevice mmDevice;
    

    @SuppressLint("MissingPermission")
    public ConnectThread(BluetoothDevice device)
    {
      mmDevice = device;
      BluetoothSocket tmp = null;
      

      try
      {
        tmp = device.createRfcommSocketToServiceRecord(BluetoothService.MY_UUID);
      } catch (IOException e) {
        Log.e("BluetoothService", "create() failed", e);
      }
      mmSocket = tmp;
    }
    
    @SuppressLint("MissingPermission")
    public void run() {
      Log.i("BluetoothService", "BEGIN mConnectThread");
      setName("ConnectThread");
      

      mAdapter.cancelDiscovery();
      


      try
      {
        mmSocket.connect();
      } catch (IOException e) {
        BluetoothService.this.connectionFailed();
        try
        {
          mmSocket.close();
        } catch (IOException e2) {
          Log.e("BluetoothService", "unable to close() socket during connection failure", e2);
        }
        
        start();
        return;
      }
      

      synchronized (BluetoothService.this) {
        mConnectThread = null;
      }
      

      connected(mmSocket, mmDevice);
    }
    
    public void cancel() {
      try {
        mmSocket.close();
      } catch (IOException e) {
        Log.e("BluetoothService", "close() of connect socket failed", e);
      }
    }
  }
  

  private class ConnectedThread
    extends Thread
  {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    
    public ConnectedThread(BluetoothSocket socket)
    {
      Log.d("BluetoothService", "create ConnectedThread");
      mmSocket = socket;
      InputStream tmpIn = null;
      OutputStream tmpOut = null;
      
      try
      {
        tmpIn = socket.getInputStream();
        tmpOut = socket.getOutputStream();
      } catch (IOException e) {
        Log.e("BluetoothService", "temp sockets not created", e);
      }
      
      mmInStream = tmpIn;
      mmOutStream = tmpOut;
    }
    
    public void run() {
      Log.d("ConnectedThread线程运行", "正在运行......");
      Log.i("BluetoothService", "BEGIN mConnectedThread");
      
      try
      {
        for (;;)
        {
          byte[] buffer = new byte['Ā'];
          
          int bytes = mmInStream.read(buffer);
          if (bytes <= 0) {
            break;
          }
          
          mHandler.obtainMessage(2, bytes, -1, buffer).sendToTarget();
        }
        

        Log.e("BluetoothService", "disconnected");
        BluetoothService.this.connectionLost();
        

        if (mState != 0)
        {
          Log.e("BluetoothService", "disconnected");
          
          start();
        }
      }
      catch (IOException e)
      {
        Log.e("BluetoothService", "disconnected", e);
        BluetoothService.this.connectionLost();
        

        if (mState != 0)
        {

          start();
        }
      }
    }
    




    public void write(byte[] buffer)
    {
      try
      {
        mmOutStream.write(buffer);
        

        mHandler.obtainMessage(3, -1, -1, buffer)
          .sendToTarget();
      } catch (IOException e) {
        Log.e("BluetoothService", "Exception during write", e);
      }
    }
    
    public void cancel() {
      try {
        mmSocket.close();
      } catch (IOException e) {
        Log.e("BluetoothService", "close() of connect socket failed", e);
      }
    }
  }
}
