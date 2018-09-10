package com.babbicool.printerwrapper.example;

/**
 * Create by Yosef.sugiarto@gmail.com
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.babbicool.lib.printerwrapper.Printer;

public class DetectImpl extends Printer {

    public DetectImpl() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void connect(String address) {
        Log.d("DetectImpl","ADDRESS : " + address);

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void printString(String text) {

    }

    @Override
    public void printLine() {

    }

    @Override
    public void printImage(Bitmap bitmap) {

    }
}
