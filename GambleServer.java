import java.io.*;
import java.net.*;

import java.util.*;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class GambleServer {

    private static Set<String> userNames = new HashSet<>();
    private static ArrayList<Users> userData = new ArrayList<>();
    private int port;
    private static Set<UserThread> userThreads = new HashSet<>();
    static double thenumber=0;
    TimeConnect timec = new TimeConnect();
    //start a port
    public ChatServer(int port) {
        this.port = port;
    }

    //default port is 8989
    public static void main(String[] args) throws IOException {
        // read data from userData if the user data exists and add that to the array
        File ff = new File("userData.txt");
        if (ff.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(ff))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] split = line.split(" ");
                    userNames.add(split[0]);
                    Users u = new Users(split[0], split[1], Integer.valueOf(split[2]));
                    String che = split[3].replace("[", "");
                    che = che.replace("]", "");
                    String[] s = che.split(",");
                    ArrayList<String> hidden = new ArrayList<>();
                    for (int i = 0; i < s.length; i++) {
                        s[i] = s[i].trim();
                        hidden.add(s[i]);
                    }
                    u.setHiddenUsers(hidden);
                    userData.add(u);
                }
            }
        }

        Runnable helloRunnable = new Runnable() {
            public void run() {
                for (int i = 0; i < userData.size(); i++) userData.get(i).addCoins(10);

                for (UserThread user : userThreads) {
                    user.addCoins(10);
                    System.out.println(user.getUsername() + ": " + user.getCoins());
                }
                writeAllUserData();

            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 1000, TimeUnit.SECONDS);
        int port = 8989;
        ChatServer server = new ChatServer(port);
        server.execute();


    }

    // write all data of users in userData and group data arraylist to make sure all the data present when rebooted
    public static void writeAllUserData() {
        // write userData.txt to the text file
        File ff = new File("userData.txt");
        ff.delete();
        for (int i = 0; i < userData.size(); i++) {
            try (FileWriter fw = new FileWriter("userData.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                // the data here is formatted like usernam password hiddenuserArraylists
                out.println(userData.get(i).getUsername() + " " + userData.get(i).getPsswd() + " " + userData.get(i).getCoins()+" " + userData.get(i).getHiddenUser());

            } catch (IOException e) {
                giveUserErrorMessage("Server failure");
                e.printStackTrace();
            }
        }


    }

    //sign up by create new users and add users to the arraylist of users with username and password
    void signUp(String username, String password, int coins) {
        userData.add(new Users(username, password, coins));
    }

    // execute the server

    public void execute() {
        try {
            @SuppressWarnings("resource")
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Chat Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");
                // create and start a new server thread
                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    // loop through the array list to see if the username exist before
    boolean checkUserNameDuplication(String username) {
        for (int i = 0; i < userData.size(); i++) {
            System.out.println("check dups " + username + " " + userData.get(i).getUsername());
            if (username.equals(userData.get(i).getUsername())) {
                System.out.println(" dups");
                return true;
            }

        }
        return false;
    }

    // check the username and password to see if it matches anything on the server
    boolean checkUserNameAndPassword(String username, String password) {
        for (int i = 0; i < userData.size(); i++) {

            if (username.equals(userData.get(i).getUsername()) && password.equals(userData.get(i).getPsswd())) {
                return true;
            }
        }
        return false;
    }


    void setUsetDontAppear(String sender, String listUsers) {
        sender = sender.replace("[", "");
        sender = sender.replace("]", "");
        for (int i = 0; i < userData.size(); i++) {
            if (sender.equals(userData.get(i).getUsername())) {
                userData.get(i).addHiddenUsers(listUsers);
            }
        }
    }

    // give a list of users to  clients

    void giveListOfUsers(String message, UserThread excludeUser) {
        for (UserThread user : userThreads) {
			/*
			if (user == excludeUser) {
				user.sendMessage(message);
			}
			 */
            for (int i = 0; i < userData.size(); i++) {
                if (excludeUser.getUsername().equals(userData.get(i).getUsername())) {
                    userData.get(i).addHiddenUsers(excludeUser.getUsername());
                }
            }
                // String toSend = message + ", " + getAllGroup(user);
                String toSend = message + ", " + "[]";

                toSend = toSend.replace("[", "");
                toSend = toSend.replace("]", "");
                toSend = toSend.replace("#", "");

                String[] split = toSend.split(",");
                for (int i = 0; i < userData.size(); i++) {
                    if (user.getUsername().equals(userData.get(i).getUsername())) {
                        for (int j = 0; j < split.length; j++) {
                            split[j] = split[j].trim();
                           // if (split[j].equals(excludeUser.getUsername())) split[j] = "";
                            if (userData.get(i).getHiddenUser().contains(split[j])) {
                                split[j] = "";
                            }
                        }
                    }
                }
                user.sendMessage("#" + Arrays.toString(split));

        }

    }
    // tell the clients that the password is wrong () send to the clients some thing like !
    void wrongPass(String message, UserThread excludeUser) {
        for (UserThread user : userThreads) {
            if (user == excludeUser) {
                user.sendMessage("!" + message);
            }
        }
    }

    // tell the clients that the password is correct send something like $
    void correctPass(String message, UserThread excludeUser) {
        for (UserThread user : userThreads) {
            if (user == excludeUser) {
                user.sendMessage("$" + message);
            }
        }
    }

    //change the user name of a client
    void changeUserName(String oldName, String newName, UserThread excludeUser) {
        userNames.remove(oldName);
        userNames.add(newName);

        for (int i = 0; i < userData.size(); i++) {
            System.out.println("CHANGE username " + oldName + " " + userData.get(i).getUsername());
            if (oldName.equals(userData.get(i).getUsername())) {
                userData.get(i).setUsername(newName);
            }
        }
    }

    // change the password of a client
    void changePassword(String newPass, String username) {
        for (int i = 0; i < userData.size(); i++) {
            System.out.println("CHANGE password " + username + " " + userData.get(i).getUsername());
            if (username.equals(userData.get(i).getUsername())) {
                userData.get(i).setPsswd(newPass);
            }
        }
    }

    // send message

    void broadcast(String message, UserThread excludeUser) {

        // private conversation will have a form of @username message here

        String time = timec.getTine();
        time = "20"+time.substring(5,23).trim().replaceAll("-", "/");
        System.out.println(time);

        String toUser = "";
        String meee = message;
        String whosent = "";
        meee = meee.substring(meee.indexOf(":") + 2);
        whosent = message.substring(0, message.indexOf(":"));


        // send to dm
            if (meee.substring(0, 1).equals("@")) {
                toUser = meee.replace("@", "");
                String[] splited = toUser.split("\\s+");
                for (UserThread user : userThreads) {

                    if (user.getUsername().equals(splited[0])) {

                        //loadUserData(whosent, user.getUsername(), user);

                        String c = "";
                        for (int ii = 1; ii < splited.length; ii++) {
                            c += splited[ii] + " ";
                        }

                        String[] getbet = c.split("\\s+");

                        double d = Double.valueOf(getbet[0]);

                        if (user.ifGuessed()) {
                            double user1 = user.getNumberGuessed();
                            if ( Math.abs(d-thenumber) <  Math.abs(user1-thenumber)) {
                                user.sendMessage(time + " " + whosent + ": guessed ~ " + Math.round(Math.abs(d-thenumber)) + " of the actual value (" +thenumber +"), you lose "+ getbet[1]+" coins!!");
                                excludeUser.sendMessage(time + " [Server] You win " + getbet[1]+ " coins!!, the number is "+ thenumber);
                                excludeUser.addCoins(Integer.valueOf(getbet[1]));
                                user.lossCoins((Integer.valueOf(getbet[1])));
                                for (int i = 0; i < userData.size(); i++) {
                                    if (excludeUser.getUsername().equals(userData.get(i).getUsername())) {
                                        userData.get(i).addCoins(Integer.valueOf(getbet[1]));
                                    }
                                    if (user.getUsername().equals(userData.get(i).getUsername())) {
                                        userData.get(i).lossCoins(Integer.valueOf(getbet[1]));
                                    }
                                }
                                System.out.println(excludeUser.getCoins());

                            } else if (( Math.abs(d-thenumber) == Math.abs(user1-thenumber))) {
                                user.sendMessage(time +"[Server] You guys guessed the same number! guess again!");
                                excludeUser.sendMessage(time +"[Server] You guys guessed the same number! guess again!");
                            }

                            else {
                                user.sendMessage(time + " " + whosent + ": guessed ~ " +Math.round(Math.abs(d-thenumber)) + " of the actual value (" +thenumber +"), you win "+ getbet[1]+ " coins!!");
                                excludeUser.sendMessage(time + " [Server] You lose " + getbet[1] +" coins!, the number is " +thenumber);
                                for (int i = 0; i < userData.size(); i++) {
                                    if (excludeUser.getUsername().equals(userData.get(i).getUsername())) {
                                        userData.get(i).lossCoins(Integer.valueOf(getbet[1]));
                                    }
                                    if (user.getUsername().equals(userData.get(i).getUsername())) {
                                        userData.get(i).addCoins(Integer.valueOf(getbet[1]));
                                    }
                                }
                                excludeUser.lossCoins((Integer.valueOf(getbet[1])));
                                user.addCoins((Integer.valueOf(getbet[1])));

                                System.out.println(excludeUser.getCoins());

                            }
                            user.setGuessed(false);

                        } else {
                            excludeUser.setNumberGuessed(d);
                            excludeUser.setGuessed(true);
                            System.out.println(c);
                            user.sendMessage(time + " " + whosent + ": guessed ~ " + Math.round(Math.abs(d - thenumber)) + " of the actual value and bet " + getbet[1] + " coins!");
                        }
                        // write data dm to file
                        //writeToFile(whosent, user.getUsername(), time + " " + whosent + ": " + c);
                        writeToFileForceChange(whosent, "@"+user.getUsername() +"_" + time + " " + whosent + ": guessed ~ " + Math.round(Math.abs(d - thenumber)) + " of the actual value", user);
                    }
                }
            }




		/*
        //broadcast to the entire server and whoever connected to the server.
        if (!privateConverse && !groupconversation)
        for (UserThread user : userThreads) {
            if (user != excludeUser) {
                user.sendMessage(time+" "+message);
            }
        }
		 */
    }


    // load user data from a dms
    void loadUserData(String sender, String receiver, UserThread user) {
        sender = sender.replace("[", "");
        sender = sender.replace("]", "");
       // File readfrom1 = new File(sender + "->" + receiver + ".txt");
        //File readfrom2 = new File(receiver + "->" + sender + ".txt");
        File forcechange = new File(sender + "-" + receiver + ".txt");

        try {
            if (forcechange.exists()) {

                FileInputStream fstream = new FileInputStream(forcechange);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                String strLine;

                //Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    user.sendMessage(strLine);
                }
                //Close the input stream
                fstream.close();
            }

        } catch (Exception e) {
            giveUserErrorMessage(user,  "can't previous user data !");

        }

    }

    // wrie all users data to make sure when server rebooted, the data still there
    void writeToFileForceChange(String sender, String receiver, UserThread user) {
        sender = sender.replace("[", "");
        sender = sender.replace("]", "");
        String[] arr = receiver.split("_");
        //System.out.println(sender + " "+ receiver);

            File ff = new File(sender + "-" + arr[0].replace("@", "").trim() + ".txt");
            ff.delete();

            try (FileWriter fw = new FileWriter(sender + "-" + arr[0].replace("@", "").trim() + ".txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                String[] no = arr[1].split("\\\\n");
                for (int i = 0; i < no.length; i++)
                    out.println(no[i]);
                //more code
            } catch (IOException e) {
                giveUserErrorMessage(user, "Can't save chat log");
            }

    }


    // give the specific client a error message
    void giveUserErrorMessage(UserThread userd, String message) {
        for (UserThread user : userThreads) {
            if (user == userd) {
                user.sendMessage("&" + message);
            }
        }
    }

    // give the every client a error message
    static void giveUserErrorMessage(String message) {
        for (UserThread user : userThreads) {
                user.sendMessage("&" + message);
        }
    }

    // delete an account
    void deleteAccount(String username) {
        userNames.remove(username);
        for (int i = 0; i < userData.size(); i++) {
            System.out.println("delete username " + username + " " + userData.get(i).getUsername());
            if (username.equals(userData.get(i).getUsername())) {
                userData.remove(i);
                break;
            }
        }
    }


    void getTotalCoins(UserThread userd) {


        for (UserThread user : userThreads) {
            if (user == userd) {
                for (int i = 0; i < userData.size(); i++) {
                    if (userd.getUsername().equals(userData.get(i).getUsername()))
                        user.sendMessage("*" + userData.get(i).getCoins());
                }
            }
        }
    }
    // get user name
    Set<String> getUserNames() {
        return this.userNames;
    }


    public  int[] getIN(String str) {
        String[] splited = str.split("\\s+");
        int[] numbers = new int[splited.length];
        for(int i = 0;i < splited.length;i++)
        {

            numbers[i] = Integer.parseInt(splited[i]);
        }

        for (int i =0; i < numbers.length; i++)
        {  System.out.println(numbers[i]);
        }
        return numbers;
    }


    String getRandomNumberRadioDec(UserThread user) {
        RadioActiveDecay rd = new RadioActiveDecay();
        try {
            String c = rd.getDataFromServer("5", "RB1k1Q0fjmvsKC68x7kNw1ozHXf");
            String d = rd.parseJSON(c);
            int[] truerandom = getIN(d);
            int average = 0;
            for (int i = 0; i < truerandom.length; i++) {
                average += truerandom[i];
            }
            thenumber = average/5;
            return d;
        } catch (Exception e) {
            e.printStackTrace();
            giveUserErrorMessage(user,  "Too many requests, slow down!");

        }
        return null;
    }

    String getRandomNumberRD(UserThread user) {
        orgGet getRandomNUm = new orgGet("dinhd@purdue.edu");
        try {
            String c =getRandomNUm.getRandomNumber(5, 0, 20, 10);

            System.out.println(c);
            int[] truerandom = getIN(c);
            int average = 0;
            for (int i = 0; i < truerandom.length; i++) {
                average += truerandom[i];
            }
            thenumber = average/5;
            return c;
        } catch(Exception e) {
            giveUserErrorMessage(user,  "Too many requests, slow down!");
            e.printStackTrace();
        }
        return null;
    }


    // add user name
    boolean addUserName(String userName) {
        for (String one : userNames) {
            if (one.equals(userName)) {
                return false;
            }
        }
        userNames.add(userName);
        return true;
    }

}
