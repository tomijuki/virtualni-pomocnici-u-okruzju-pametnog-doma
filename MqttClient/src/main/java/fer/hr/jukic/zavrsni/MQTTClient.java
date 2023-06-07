package fer.hr.jukic.zavrsni;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MQTTClient {

    private static final String broker = "tcp://localhost:1883";
    private static final String topic = "iotlab/sensor";
    private static final String username = "hass";
    private static final String password = "hass";

    private static final Logger logger = Logger.getLogger("MQTTClientLogger");
    public static void send() throws MqttException {
        if(WaspmoteLoader.result!=null){
            String publisherId = UUID.randomUUID().toString();
            String content = "{" + WaspmoteLoader.result + "}";
            int qos = 0;

            try{
                //postavke objavitelja
                IMqttClient publisher = new MqttClient(broker, publisherId, new MemoryPersistence());
                MqttConnectOptions options = new MqttConnectOptions();
                options.setUserName(username);
                options.setPassword(password.toCharArray());
                options.setKeepAliveInterval(60);
                options.setConnectionTimeout(60);
                publisher.connect(options);

                //mqtt paket, odnosno poruka
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);

                //objavi
                publisher.publish(topic, message);

                //ispis za praćenje, neobavezno
                System.out.println("Message published");
                System.out.println("topic: " + topic);
                System.out.println("message content: " + content);

                //dispose
                publisher.disconnect();
                publisher.close();

            } catch (MqttException mqttException){
                logger.log(Level.INFO,"New mqtt exception: " + mqttException.getMessage());
                mqttException.printStackTrace();
            }

            //resetiraj očitanje nakon što je prethodno poslano i čekaj novo
            WaspmoteLoader.result = null;
        }
    }
}