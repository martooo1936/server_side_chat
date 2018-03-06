import java.util.ArrayList;

public class Protocol {

    public static String J_OK ()
    {
        return "J_OK";
    }

    public static String J_ER (int err_code , String err_msg)
    {
        return "Error ! " + err_code + err_msg ;
    }
    public static String DATA (String user_name ,String text)
    {
        return "DATA"  + user_name + text;
    }
    public static String LIST (ArrayList<User> users)
    {
        return "Active users : " + users;
    }

}
