package com.mycompany.mydevice;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

class ListItem {
    UsbDevice device;
    int port;
    UsbSerialDriver driver;

    ListItem(UsbDevice device, int port, UsbSerialDriver driver) {
        this.device = device;
        this.port = port;
        this.driver = driver;
    }
    
    public int getVendorId() { return device.getVendorId();  }
    
    public int getProductId() { return device.getProductId();  }
    
    public int getPort() { return port;  }

    public String getDriverName() { return driver.getClass().getSimpleName().replace("SerialDriver",""); }
}
