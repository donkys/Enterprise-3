package jmsprimeserver;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author donky
 */
public class TextListener implements MessageListener {

    private PrimeNumberChecker checker = PrimeNumberChecker.getInstance();
    private MessageProducer replyProducer;
    private Session session;

    public TextListener(Session session) {

        this.session = session;
        try {
            replyProducer = session.createProducer(null);
        } catch (JMSException ex) {
            Logger.getLogger(TextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message message) {
        TextMessage msg = null;
        try {
            if (message instanceof TextMessage) {
                msg = (TextMessage) message;
                
                String[] parts = msg.getText().split(",");
                int start = Integer.parseInt(parts[0].trim());
                int end = Integer.parseInt(parts[1].trim());
                
                System.out.println("Reading message: " + msg.getText());
                
                TextMessage response = session.createTextMessage("The number of primes between "
                        + start + " and "
                        + end + " is "
                        + checker.countPrimesInRange(start, end));

                response.setJMSCorrelationID(message.getJMSCorrelationID());
                System.out.println("sending message " + response.getText());
                replyProducer.send(message.getJMSReplyTo(), response);
            } else {
                System.err.println("Message is not a TextMessage");
                TextMessage response = session.createTextMessage("Message is not a TextMessage");
                response.setJMSCorrelationID(message.getJMSCorrelationID());
                System.out.println("sending message " + response.getText());
                replyProducer.send(message.getJMSReplyTo(), response);
            }
        } catch (JMSException e) {
            System.err.println("JMSException in onMessage(): " + e.toString());
        } catch (Throwable t) {
            System.err.println("Exception in onMessage():" + t.getMessage());
        }

    }
}
