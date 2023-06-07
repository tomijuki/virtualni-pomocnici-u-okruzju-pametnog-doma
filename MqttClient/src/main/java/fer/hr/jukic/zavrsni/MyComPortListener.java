package fer.hr.jukic.zavrsni;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyComPortListener implements SerialPortDataListener {


    public static String result = null;

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        //ukoliko događaj koji se dogodio nije događaj dostupnosti na ulazu, odustani
        if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;
        //čitanje podataka u readBuffer
        byte[] readBuffer = new byte[serialPortEvent.getSerialPort().bytesAvailable()];
        int numRead = serialPortEvent.getSerialPort().readBytes(readBuffer, readBuffer.length);
        String reading = new String(readBuffer);
        //System.out.println(result); //test metoda
        result = result + reading;
        //triple json key-value pair pattern
        Pattern pattern = Pattern.compile("\\s*\"[^\"]+\"\\s*:\\s*\"[^\"]+\"\\s*,\\s*\"[^\"]+\"\\s*:\\s*\"[^\"]+\"\\s*,\\s*\"[^\"]+\"\\s*:\\s*\"[^\"]+\"\\s*");
        Matcher matcher = pattern.matcher(result);
        if(matcher.find()){
            //ukoliko postoji zapis koji zadovoljava uvjet zapiši ga u statičku varijablu waspmote loader
            WaspmoteLoader.result = roundPressureToHpa(matcher.group(0));
            result = null;
        }
        try {
            if(WaspmoteLoader.result != null){
                //pozovi send metodu, odnosno publish na topic
                MQTTClient.send();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //funkcija za zaokruživanje tlaka u hektopaskale
    public static String roundPressureToHpa(String input) {
        int pressureIndex = input.indexOf("\"pressure\":");
        if (pressureIndex == -1) {
            return input; // Return the input string as is if "pressure" key is not found
        }

        int valueStartIndex = input.indexOf("\"", pressureIndex + 10) + 1; // Adding 10 to skip the "pressure" key and colon
        int valueEndIndex = input.indexOf("\"", valueStartIndex);
        if (valueStartIndex == -1 || valueEndIndex == -1) {
            return input; // Return the input string as is if pressure value is not found
        }

        String pressureValue = input.substring(valueStartIndex, valueEndIndex);
        double pressurePa = Double.parseDouble(pressureValue);
        double pressureHpa = Math.ceil(pressurePa / 100 * 100) / 100; // Rounding to 2 decimal places

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String roundedPressureValue = decimalFormat.format(pressureHpa);
        return input.substring(0, valueStartIndex) + roundedPressureValue + input.substring(valueEndIndex);
    }
}
