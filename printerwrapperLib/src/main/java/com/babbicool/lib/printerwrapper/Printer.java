package com.babbicool.lib.printerwrapper;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;

public abstract class Printer {

    //public BluetoothSocket btSocket, mBtSocket;
    private Context mContext;
    private onPrinterConnectedListener printerListener;



    public interface onPrinterConnectedListener{
        void onPrinterConnected(String printerAddress);
        void onError();
    }



    public Printer(){
    }

    public void setContext(Context mContext){
        this.mContext = mContext;
    }

    public void setPrinterListener(onPrinterConnectedListener printerListener){
        this.printerListener = printerListener;
    }

    public  abstract boolean isConnected();

    public abstract void connect(String address);

    public abstract void disconnect();

    public abstract void printString(String text);

    public abstract void printLine();

    public abstract void printImage(Bitmap bitmap);

}
