package com.babbicool.lib.printerwrapper;

/**
 * Create by Yosef.sugiarto@gmail.com
 */

import android.content.Context;
import android.graphics.Bitmap;

import com.babbicool.lib.printerwrapper.model.PrinterModel;
import java.util.ArrayList;
import java.util.List;


public class PrinterWrapper {


    private Printer impl;

    private List<PrinterModel> supportedPrinter = new ArrayList<>();

    private static PrinterWrapper instance;

    private Context mContext;


    public PrinterWrapper(Context mContext){
        this.mContext = mContext;
        //generateSupportedDevices();
    }

    public static PrinterWrapper getInstance() throws Exception {
        if(instance!=null){
            return instance;
        }
        throw new Exception("You need Init class");
    }

    public static void init(Context mContext){
        instance = new PrinterWrapper(mContext);
    }

    public void setImpl(PrinterModel printerModel) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        Class cls = Class.forName(printerModel.getClassName());
        impl = (Printer) cls.newInstance();
        impl.setContext(mContext);
    }


    public List<PrinterModel> getSupportedPrinter() {
        return supportedPrinter;
    }

    public void addSupportedPrinter(PrinterModel printerModel){
        supportedPrinter.add(printerModel);
    }


    public void connect(String address , Printer.onPrinterConnectedListener printerConnectedListener) throws Exception {
        if(impl == null )
            throw new Exception("Printer impl null you need set implementator");
        impl.setPrinterListener(printerConnectedListener);
        impl.connect(address);
    }


    public void printString(String str) throws Exception {
        if(impl == null )
            throw new Exception("Printer impl null you need set implementator");

    }

    public void printImages(Bitmap image) throws Exception {
        if(impl == null )
            throw new Exception("Printer impl null you need set implementator");
        impl.printImage(image);

    }

    public void printLine() throws Exception {
        if(impl == null )
            throw new Exception("Printer impl null you need set implementator");

        impl.printLine();

    }

    public void disconect() throws Exception {
        if(impl == null )
            throw new Exception("Printer impl null you need set implementator");
        impl.disconnect();
    }

}
