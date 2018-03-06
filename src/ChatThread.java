import java.net.Socket;
import java.util.Date;
import java.util.Vector;

/*
 * ChatThread class is purposed to listen for messages
 * received from the clients and to forward them to all the
 * clients connected to the chat server.
 */
public class ChatThread extends Thread {

    private Vector mMessageQueue = new Vector();
    public Vector mClients = new Vector();

    //adds a client to the server´s client list

    public synchronized void addClient(User aClient) {
        mClients.add(aClient);

    }


     // Deletes given client from the server's client list if


    public synchronized void deleteClient(User aClient) {
        int clientIndex = mClients.indexOf(aClient);
        if (clientIndex != -1)
            mClients.removeElementAt(clientIndex);
    }

    /* Adds given message to the dispatcher's message queue and
* notifies this thread to wake up the message queue reader
* (getNextMessageFromQueue method). dispatchMessage method
* is called by other threads (ClientListener) when a
* message is arrived.
*/

    public synchronized void dispatchMessage(
            User aClient,String aMessage) {
        Socket socket = aClient.mSocket;
        String senderIP =
                socket.getInetAddress().getHostAddress();

        String senderPort = "" + socket.getPort();
        aMessage = senderIP + ":" + senderPort +
                " : " + aMessage;
        mMessageQueue.add(aMessage);
        notify();
    }



/*
 * returns and deletes the next message from the message
 * queue. If there is no messages in the queue, falls in
 * sleep until notified by dispatchMessage method.
 */
    private synchronized String getNextMessageFromQueue() throws InterruptedException
    {
        while (mMessageQueue.size()==0)
            wait();
        String message = (String) mMessageQueue.get(0);
        mMessageQueue.removeElementAt(0);
        System.out.println(message);
        return message;

    }





    /*
     * Sends given message to all clients in the client list.
     * Actually the message is added to the client sender
     * thread's message queue and this client sender thread
     * is notified to process it.
     */
    private void sendMessageToAllClients(
            String aMessage) {
        for (int i=0; i<mClients.size(); i++) {
            User client = (User) mClients.get(i);
            client.mClientSender.sendMessage(aMessage);



        }
    }




/*
 * Infinitely reads messages from the queue and dispatches
 * them to all clients connected to the server.
 */
    public void run() {
        try {
            while (true) {
                String message = getNextMessageFromQueue();
                sendMessageToAllClients(message);

            }
        } catch (InterruptedException ie) {
            // Thread interrupted. Stop its execution
        }
    }


}
