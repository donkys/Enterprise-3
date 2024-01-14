package jmsprimeclient;

import java.util.Scanner;
import java.util.UUID;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author donky
 */
public class Main {

    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/TempQueue")
    private static Queue queue;

    public static void main(String[] args) {
        try (Connection connection = connectionFactory.createConnection();
                Scanner sc = new Scanner(System.in)) {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);

            Queue tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);
            TextListener listener = new TextListener();
            responseConsumer.setMessageListener(listener);

            connection.start();

            String input;
            while (true) {
                System.out.println("Enter two numbers.Use ',' to separated each comma. To end the program, press enter");
                input = sc.nextLine();
                if (input.trim().isEmpty()) {
                    break;
                }
                
                //String correlationId = "12345";
                String correlationId = UUID.randomUUID().toString();
                TextMessage message = session.createTextMessage(input);
                message.setJMSReplyTo(tempDest);
                message.setJMSCorrelationID(correlationId);

                System.out.println("Sending message: " + message.getText());
                producer.send(message);
            }

        } catch (JMSException e) {
            System.err.println("JMS Exception occurred: " + e.toString());
        }
    }
}
