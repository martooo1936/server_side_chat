import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

/*
 * ClientListener class listens for client messages and
 * forwards them to ServerDispatcher.
 */
public class ClientListener extends Thread
{
    private ChatThread mServerDispatcher;
    private User mClient;
    private BufferedReader mSocketReader;

    public ClientListener(User aClient, ChatThread
            aSrvDispatcher) throws IOException {
        mClient = aClient;
        mServerDispatcher = aSrvDispatcher;
        Socket socket = aClient.mSocket;
        socket.setSoTimeout(
                Server.CLIENT_READ_TIMEOUT);
        mSocketReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()) );
    }


    // Until interrupted, reads messages from the client forward them to the ChatThread and notifies it


    public void run() {

        try {

            while (!isInterrupted()) {

                try {
                    String message = mSocketReader.readLine();
                    if (message == null )
                        break;
                    mServerDispatcher.dispatchMessage(
                            mClient, message);
                    if(message.contains("JOIN"))
                    {


                    }

                } catch (SocketTimeoutException ste) {
                    mClient.mClientSender.sendKeepAlive();
                }
            }
        } catch (IOException ioex) {
            // Problem reading from socket (broken connection)
        }




        // Communication is broken. Interrupt both listener and
        // sender threads
        mClient.mClientSender.interrupt();
        mServerDispatcher.deleteClient(mClient);
    }

    // Control user input
























}

