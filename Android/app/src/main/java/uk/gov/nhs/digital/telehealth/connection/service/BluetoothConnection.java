package uk.gov.nhs.digital.telehealth.connection.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import android.util.Log;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothConnection {
    private static final String LOG_TAG = BluetoothConnection.class.getName();

    protected static final UUID SERIAL_PROFILE_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    //protected static final UUID SERIAL_PROFILE_UUID = UUID.fromString("00001108-0000-1000-8000-00805f9b34fb");
    //protected static final UUID SERIAL_PROFILE_UUID = UUID.fromString("0000111e-0000-1000-8000-00805f9b34fb");
    //protected static final UUID SERIAL_PROFILE_UUID = UUID.fromString("0000110b-0000-1000-8000-00805f9b34fb");


    protected static final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    protected final BluetoothDevice bluetoothDevice;
    protected final String bluetoothAddress;

    protected BluetoothSocket bluetoothSocket = null;
    protected OutputStream outputStream = null;
    protected InputThread inputThread = null;

    private class InputThread extends Thread {
        volatile boolean stop = false;
        InputStream inputStream;
        int pipeDescriptor;

        InputThread (InputStream inputStream, int pipeDescriptor) {
            super("bluetooth-input-" + bluetoothAddress);
            this.inputStream = inputStream;
            this.pipeDescriptor = pipeDescriptor;
        }

        @Override
        public void run () {
            try {
                File pipeFile = new File("/proc/self/fd/" + pipeDescriptor);
                OutputStream pipeStream = new FileOutputStream(pipeFile);
                byte[] buffer = new byte[0X80];

                while (!stop) {
                    int byteCount;
                    try {
                        byteCount = inputStream.read(buffer);
                    } catch (Exception exception) {
                        Log.d(LOG_TAG, "Bluetooth input exception: " + bluetoothAddress + ": " + exception.getMessage());
                        break;
                    }

                    /*if (byteCount > 0) {
                        pipeStream.write(buffer, 0, byteCount);
                        pipeStream.flush();
                    } else if (byteCount < 0) {
                        Log.d(LOG_TAG, "Bluetooth input end: " + bluetoothAddress);
                        break;
                    }*/
                }

                pipeStream.close();
                inputStream.close();
            } catch (Throwable cause) {
                Log.e(LOG_TAG, "Bluetooth input failed: " + bluetoothAddress, cause);
            }
        }
    }

    public static BluetoothDevice getDevice (String deviceAddress) {
        /*byte[] hardwareAddress = new byte[6];

        {
            int i = hardwareAddress.length;

            while (i > 0) {
                hardwareAddress[--i] = (byte)(deviceAddress & 0XFF);
                deviceAddress >>= 8;
            }
        }*/

        /*if (!ApplicationUtilities.haveSdkVersion(Build.VERSION_CODES.JELLY_BEAN)) {
            StringBuilder sb = new StringBuilder();

            for (byte octet : hardwareAddress) {
                if (sb.length() > 0) sb.append(':');
                sb.append(String.format("%02X", octet));
            }

            return bluetoothAdapter.getRemoteDevice(sb.toString());
        }*/

        return bluetoothAdapter.getRemoteDevice(deviceAddress);
    }

    public static String getName (String deviceAddress) {
        return getDevice(deviceAddress).getName();
    }

    public BluetoothConnection (String deviceAddress) {
        bluetoothDevice = getDevice(deviceAddress);
        bluetoothAdapter.cancelDiscovery();
        bluetoothAddress = bluetoothDevice.getAddress();
    }

    public void close () {
        if (inputThread != null) {
            inputThread.stop = true;
            inputThread = null;
        }

        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException exception) {
                Log.w(LOG_TAG, "Bluetooth close failed: " + bluetoothAddress, exception);
            }

            bluetoothSocket = null;
        }
    }

    public boolean open (int inputPipe, boolean secure) {
        InputStream inputStream = null;

        try {

            /*Class<?> clazz = bluetoothDevice.getClass();
            Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};

            Method m = clazz.getMethod("createRfcommSocket", paramTypes);
            Object[] params = new Object[] {Integer.valueOf(1)};

            bluetoothSocket = (BluetoothSocket) m.invoke(bluetoothDevice, params);
            bluetoothSocket.connect();*/

            bluetoothSocket = secure? bluetoothDevice.createRfcommSocketToServiceRecord(SERIAL_PROFILE_UUID):
                    bluetoothDevice.createInsecureRfcommSocketToServiceRecord(SERIAL_PROFILE_UUID);
            bluetoothSocket.connect();

            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException openException) {
            Log.e(LOG_TAG, "Bluetooth connect failed: " + bluetoothAddress + ": " + openException.getMessage());

            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    Log.e(LOG_TAG, "Bluetooth socket close error: " + bluetoothAddress, closeException);
                }

                bluetoothSocket = null;
                outputStream = null;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputThread = new InputThread(inputStream, inputPipe);
        inputThread.start();
        return true;
    }

    public boolean write (byte[] bytes) {
        try {
            outputStream.write(bytes);
            outputStream.flush();
            return true;
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Bluetooth write failed: " + bluetoothAddress, exception);
        }

        return false;
    }
}