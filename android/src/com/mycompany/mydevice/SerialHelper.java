// SerialHelper.java
package com.mycompany.mydevice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.FtdiSerialDriver;
import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class SerialHelper extends org.qtproject.qt.android.bindings.QtActivity {
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final String ACTION_USB_PERMISSION = "com.mycompany.mydevice.USB_PERMISSION";
    private static UsbManager usbManager;
    private static UsbSerialPort serialPort;
    private static List<UsbSerialDriver> availableDrivers = new ArrayList<UsbSerialDriver>();
    private static ArrayList<Device> devices = new ArrayList<Device>();

    private static final int WRITE_WAIT_MILLIS = 2000;
    private static final int READ_WAIT_MILLIS = 2000;

    public static native void javaResponseReady(byte[] response);
    public static native void javaConnectedStateChanged(boolean state);
    public static native void javaErrorOccured(String error);
    public static native void javaMyDeviceAttached(boolean state);


    public static void closeDeviceConnection() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (serialPort == null) {
                    javaErrorOccured("Serial port is not initialized. Nothing to close.");
                    return;
                }

                try {
                    serialPort.close();
                    serialPort = null;
                    javaConnectedStateChanged(false);
                    return;
                } catch (IOException e) {
                    javaErrorOccured("CLOSE PORT EXCEPTION");
                    return;
                }
            }
        });
    }

    public static void connectToDevice(Context context, int vid, int pid) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                // Create custom prober for given VID and PID
                ProbeTable customTable = new ProbeTable();
                //customTable.addProduct(vid, pid, FtdiSerialDriver.class);
                 customTable.addProduct(vid, pid, CdcAcmSerialDriver.class);
                UsbSerialProber prober = new UsbSerialProber(customTable);

                // Find all available drivers from attached devices.
                UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                List<UsbSerialDriver> drivers = prober.findAllDrivers(manager);
                if (drivers.isEmpty()) {
                    javaErrorOccured("No Device found");
                    return;
                }

                // Open a connection to the first available driver.
                UsbSerialDriver driver = drivers.get(0);
                UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
                if (connection == null) {
                    PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    manager.requestPermission(driver.getDevice(), permissionIntent);
                    javaErrorOccured("Permission request sent. Awaiting response...");
                    return;
                }
                UsbSerialPort port = driver.getPorts().get(0);
                try {
                    port.open(connection);
                    try {
                        port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                    } catch (UnsupportedOperationException e){
                        javaErrorOccured("UNSUPPORTED OPERATION EXCEPTION");
                        return;
                    }
                } catch (Exception e) {
                    javaErrorOccured("OPEN PORT EXCEPTION");
                    return;
                }

                usbManager = manager;
                serialPort = port;
                javaConnectedStateChanged(true);
            }
        });
    }


    public static void sendCommand(String command) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (serialPort == null) {
                    javaConnectedStateChanged(false);
                    javaErrorOccured("Serial port is not initialized. Call connectToDevice() first.");
                    return;
                }

                try {
                    serialPort.write(command.getBytes(), WRITE_WAIT_MILLIS);
                } catch (Exception e) {
                    javaErrorOccured("WRITE EXCEPTION");
                    javaConnectedStateChanged(false);
                    return;
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];

                try {
		    // Read until command terminator found
                    while (true) {
                        int len = serialPort.read(buffer, READ_WAIT_MILLIS);
                        if (len > 0) {
                            outputStream.write(buffer, 0, len);

		            
                            boolean exit = false;

		            // ADD EXIT CONDITION / response termination pattern (e.g. \r\n)
		
                            if(exit){
                                byte[] originalArray = outputStream.toByteArray();
				// -2 => terminator (e.g. \r\n) removed before sending to C++
                                byte[] trimmedArray = Arrays.copyOf(originalArray, originalArray.length - 2);

                                javaResponseReady(trimmedArray);
                                return;
                            }
                        }
                    }
                } catch (IOException e) {
                    javaErrorOccured("READ EXCEPTION");
                    javaConnectedStateChanged(false);
                    return;
                }
            }
        });
    }
    
    public static ArrayList<Device> getDeviceList(Context context) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
		UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
		devices.clear();
		for(UsbDevice device : usbManager.getDeviceList().values()) {
		    UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
		    if(driver == null) {
		        driver = usbCustomProber.probeDevice(device);
		    }
		    if(driver != null) {
		        for(int port = 0; port < driver.getPorts().size(); port++)
		            devices.add(new Device(device.getVendorId(), device.getProductId(), port, driver.getClass().getSimpleName()));
		    } else {
		        devices.add(new Device(device.getVendorId(), device.getProductId(), 0, ""));
		    }
        }
        
       return devices;
   }   
   /***
   public static ArrayList<UsbSerialDriver> findAllDrivers(Context context) {
          UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
          UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
		  final ArrayList<UsbSerialDriver> result = new ArrayList<UsbSerialDriver>();
		  for (final UsbDevice usbDevice : usbManager.getDeviceList().values()) {
			final UsbSerialDriver driver = usbDefaultProber.probeDevice(usbDevice);
			if (driver != null) {
			  result.add(driver);
			}
		  }
		  return result;
		}
       ****/     

    private static final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            javaMyDeviceAttached(true);
                            int vid = device.getVendorId();
                            int pid = device.getProductId();
                            connectToDevice(context, vid, pid);
                        }
                    }
                }
            }
        }
    };
}

