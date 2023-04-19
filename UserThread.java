import java.io.*;
import java.net.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserThread extends Thread {
    String userName;
    String userNameAndPassword;
    String password;
    String up;
    int coins = 500;
    double numberGuessed = 0.0;
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;
    boolean guessed = false;
    // constructor
    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    // get user name
    public String getUsername() {
        return userName;
    }

    //set user name
    public void setUsername(String name) {
        userName = name;
    }

    // login and check credentials
    public void login(BufferedReader reader) throws Exception {

        while (up.equals("in") && !server.checkUserNameAndPassword(userName, toHexString(getSHA(password)))) {
            userNameAndPassword = reader.readLine();
            String[] splited = userNameAndPassword.split("\\s+");
            // parse string sent from client
            up = splited[0];
            userName = splited[1];
            password = splited[2];
            server.wrongPass("Wrong pass", this);

        }
        // if the user decided to sign up
        if (up.equals("up")) {
            signup(reader);
        } else {
            server.correctPass("Loading...", this); // prints out the loading msg if things are slow
            String serverMessage = "#" + server.getUserNames();
            //Thread.sleep(2000);
            server.giveListOfUsers(serverMessage, this);
        }
    }


    public  byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public  String toHexString(byte[] hash)
    {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    //sing up
    public void signup(BufferedReader reader) throws Exception {
        System.out.println("sign up");
        while (server.checkUserNameDuplication(userName) && up.equals("up")) {
            userNameAndPassword = reader.readLine();
            String[] splited = userNameAndPassword.split("\\s+");
            up = splited[0];
            userName = splited[1];

            password =splited[2];

            System.out.println("Duplicates");
            server.wrongPass("Duplicate username", this);
        }
        // if user decides ro sign in
        if (up.equals("in")) {
            login(reader);
        } else {
            server.correctPass("Loading...", this);
            server.signUp(userName, toHexString(getSHA(password)), 500);
            String serverMessage = "#" + server.getUserNames();
            //hread.sleep(2000);
            server.giveListOfUsers(serverMessage, this);
        }

    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            userNameAndPassword = reader.readLine();
            String[] splited;
            if (userNameAndPassword == null) {
                userName = "";
            } else {

                splited = userNameAndPassword.split("\\s+");

                up = splited[0];

                if (splited.length < 3) {
                    password = "";
                    userName = "";
                } else {
                    password = splited[2];
                    userName = splited[1];
                }


            }
            // once check for sign up and sign in procceed to add username to the list
            if (!(up == null) && up.equals("in") && !password.equals("")) {
                login(reader);
            } else if (!(up == null) && up.equals("up") && !password.equals("")) {
                signup(reader);
            }
            // add username and start listening to clients
            server.addUserName(userName);
            String serverMessage = "#" + server.getUserNames();
            server.giveListOfUsers(serverMessage, this);


            String clientMessage;

            // listen to the client
            do {
                clientMessage = reader.readLine();

                if (clientMessage == null)
                    break;
                else {
                    // if client request changing username
                    if (clientMessage.substring(0, 2).equals("!#")) {
                        String oldName = userName;
                        setUsername(clientMessage.substring(2));
                        server.changeUserName(oldName, clientMessage.substring(2), this);
                    }
                    // if client request changing password
                    else if (clientMessage.substring(0, 2).equals("$#")) {
                        server.changePassword(clientMessage.substring(2), userName);
                    } else if (clientMessage.substring(0, 2).equals("##")) {


                        /*
                        if (clientMessage.substring(2, 3).equals("[")) {
                            server.loadUserData2(userName, clientMessage.substring(2), this);
                            System.out.println("Gotta do");
                        } else*/
                            server.loadUserData(userName, clientMessage.substring(2), this);
                        // if user request delete an account
                    } else if (clientMessage.substring(0, 2).equals("!!")) {
                        server.deleteAccount(userName);
                        // write data
                    } else if (clientMessage.substring(0, 2).equals("**")) {
                        server.writeToFileForceChange(userName, clientMessage.substring(2), this);
                        // set the hidden user is removed from liists
                    } else if (clientMessage.substring(0, 2).equals("*!")) {
                        server.setUsetDontAppear(userName, clientMessage.substring(2));
                    }
                    else if (clientMessage.substring(0, 2).equals("__")) {
                        server.getRandomNumberRD(this);
                    }
                    else if (clientMessage.substring(0, 2).equals("_!")) {
                        server.getRandomNumberRadioDec(this);
                    }   else if (clientMessage.substring(0, 2).equals("_*")) {
                        server.getTotalCoins(this);
                    }
                    else {
                        // if client does not request anything, just send nad broadcast message
                        serverMessage = "[" + userName + "]: " + clientMessage;
                        server.broadcast(serverMessage, this);
                    }
                    server.writeAllUserData();
                }

            } while (true);

            socket.close();

        } catch (Exception ex) {
            System.out.println("user exited");
            // send to client if there is an error from server or data sent from clients
            server.giveUserErrorMessage(this, "Could not load or parse data send from client");
            ex.printStackTrace();

        }
    }

    public void setCoins(int amount) {
        coins = amount;
    }
    public void addCoins(int amount){
        coins = coins + amount;
    }

    public double getNumberGuessed() {
        return numberGuessed;
    }
    public boolean ifGuessed() {
        return guessed;
    }

    public void setGuessed(boolean guessed ) {
        this.guessed = guessed;
    }

    public void setNumberGuessed(double numberGuessed) {
        this.numberGuessed = numberGuessed;
    }
    public void lossCoins(int amount) {
        coins = coins - amount;
    }
    public int getCoins() {
        return coins;
    }


    // send message to client
    void sendMessage(String message) {
        writer.println(message);
    }
}