package com.babbicool.printerwrapper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.babbicool.lib.printerwrapper.Printer;
import com.babbicool.lib.printerwrapper.model.PrinterModel;
import com.google.gson.Gson;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            PrinterWrapper.init(getApplicationContext());

            String[] strSupportedDevices = getResources().getStringArray(R.array.supportedPrinter);
            for (String device: strSupportedDevices) {
                PrinterWrapper.getInstance().addSupportedPrinter(new Gson().fromJson(device,PrinterModel.class));
            }


            List<PrinterModel> printerModels = PrinterWrapper.getInstance().getSupportedPrinter();

            for (PrinterModel printerModel:printerModels) {
                if(printerModel.getProductName().equals("Detects")){
                    PrinterWrapper.getInstance().setImpl(printerModel);
                }
            }

            PrinterWrapper.getInstance().connect("test", new Printer.onPrinterConnectedListener() {
                @Override
                public void onPrinterConnected(String printerAddress) {

                }

                @Override
                public void onError() {

                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
