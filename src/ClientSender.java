
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;


/*
 * Sends messages to the client. Messages waiting to be sent
 * are stored in a message queue. When the queue is empty,
 * ClientSender falls in sleep until a new message is arrived
 * in the queue. When the queue is not empty, ClientSender
 * sends the messages from the queue to the client socket.
 */

public class ClientSender extends Thread
{
    private Vector mMessageQueue = new Vector();

    private ChatThread chatThread;
    private User mClient;
    private PrintWriter mOut;




    public ClientSender(User aClient, ChatThread
            aServerDispatcher) throws IOException {
        mClient = aClient;
        chatThread = aServerDispatcher;
        Socket socket = aClient.mSocket;
        mOut = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()) );

    }



    /*
     * Adds given message to the message queue and notifies
     * this thread (actually getNextMessageFromQueue method)
     * that a message is arrived. sendMessage is always called
     * by other threads (ServerDispatcher).
     */
    public synchronized void sendMessage(String aMessage) {
        mMessageQueue.add(aMessage);
        notify();
    }


    // sends keep alive message if the client is not active for a long time
    public void sendKeepAlive() {
        sendMessage(Server.KEEP_ALIVE);
    }


    /*
     * returns and deletes the next message from the message
     * queue. If the queue is empty, falls in sleep until
     * notified for message arrival by sendMessage method.
     */
    private synchronized String getNextMessageFromQueue()
            throws InterruptedException {

        while (mMessageQueue.size()==0)
            wait();
        String message =(String) mMessageQueue.get(0);
        mMessageQueue.removeElementAt(0);
        return  message;

    }



    // sends given message to the clients socket
    public void sendMessageToClient(String aMessage) {
        mOut.println(aMessage);
        mOut.flush();
    }


    public void run() {
        try {
            while (!isInterrupted()) {
                String message = getNextMessageFromQueue();
                sendMessageToClient(message);


            }
        } catch (Exception e) {
            // Commuication problem
        }

        // Communication is broken. Interrupt both listener
        // and sender threads
        mClient.mClientListener.interrupt();
        chatThread.deleteClient(mClient);
    }

}
