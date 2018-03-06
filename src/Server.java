import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Server class is the entry point for the server.
 * It opens a server socket, starts the dispatcher thread and
 * infinitely accepts client connections, creates threads for
 * handling them and starts these threads.*/

public class Server {

    public static final int LISTENING_PORT = 4545;
    public static String KEEP_ALIVE = "!keep-alive";
    public static int CLIENT_READ_TIMEOUT = 60*1000;
    private static ServerSocket serverSocket ;



    private static ChatThread chatThread ;

    public static void main(String[] args)
    {
        //Start listening on the server sockets
        bindServerSocket();
        // Start the ChatThread thread

        chatThread = new ChatThread();
        chatThread.start();
        //Handles all the connections
        handleClientConnections ();

    }



    private static void bindServerSocket()
    {
        try
        {

            serverSocket = new ServerSocket(LISTENING_PORT);
            System.out.println("The server is running on port: " + LISTENING_PORT);
        } catch (IOException e) {
            System.err.println("Can not start listening on port " + LISTENING_PORT);
            e.printStackTrace();
            System.exit(-1);
        }

    }
    private static void handleClientConnections()
    {
        while (true)
        {
            try {
                Socket socket = serverSocket.accept();
                User user = new User();
                user.mSocket = socket;
                ClientListener clientListener = new ClientListener(user,chatThread);
                ClientSender clientSender = new ClientSender(user,chatThread);
                user.mClientListener = clientListener;
                clientListener.start();
                user.mClientSender = clientSender;
                clientSender.start();
                chatThread.addClient(user);
                System.out.println("A CLIENT JUST CONNECTED TO OUR SERVER ");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}