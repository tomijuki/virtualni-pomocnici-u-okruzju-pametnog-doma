package fer.hr.jukic.zavrsni;


import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WaspmoteLoader {
    public static String result = null;
    public static SerialPort comPort;
    public static void main(String[] args) throws IOException {

        //dohvaćanje serijskih COM portova
        SerialPort[] allAvailableComPorts = SerialPort.getCommPorts();
        for (SerialPort port : allAvailableComPorts){
            System.out.println("List of all available serial ports: " + port.getDescriptivePortName());
        }

        if(allAvailableComPorts.length != 0){
            //postavljanje parametara serijskog ulaza
            comPort = allAvailableComPorts[0];
            comPort.setBaudRate(115200);
            comPort.setComPortParameters(115200, Byte.SIZE, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
            comPort.openPort();
            System.out.println("opened the first available serial port: " + comPort.getDescriptivePortName());
            InputStream comPortInputStream = comPort.getInputStream();
            comPortInputStream.skip(comPortInputStream.available());

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                comPort.closePort();}));

            //dodavanje promatrača
            MyComPortListener listener = new MyComPortListener();
            comPort.addDataListener(listener);
            System.out.println("Listen: " + listener.getListeningEvents());
        } else {
            System.out.println("No available devices on COM ports.");
        }
    }
}
