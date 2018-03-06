import java.net.Socket;

//contains an information about a user who is connected to the sever
public class User {

    public Socket mSocket = null;
    public ClientListener mClientListener = null;
    public ClientSender mClientSender = null;

    private String username;




    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
