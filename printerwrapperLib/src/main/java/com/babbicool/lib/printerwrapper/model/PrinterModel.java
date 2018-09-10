package com.babbicool.lib.printerwrapper.model;

public class PrinterModel {

    private String productName;

    private int bluetoothClass;

    private int bluetoothMayorDeviceClass;

    private String className;


    public PrinterModel(){}

    public PrinterModel(String productName, int bluetoothClass, int bluetoothMayorDeviceClass) {
        this.productName = productName;
        this.bluetoothClass = bluetoothClass;
        this.bluetoothMayorDeviceClass = bluetoothMayorDeviceClass;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getBluetoothClass() {
        return bluetoothClass;
    }

    public void setBluetoothClass(int bluetoothClass) {
        this.bluetoothClass = bluetoothClass;
    }

    public int getBluetoothMayorDeviceClass() {
        return bluetoothMayorDeviceClass;
    }

    public void setBluetoothMayorDeviceClass(int bluetoothMayorDeviceClass) {
        this.bluetoothMayorDeviceClass = bluetoothMayorDeviceClass;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
