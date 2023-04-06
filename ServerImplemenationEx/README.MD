// main, place main in Login class

```java

    //main method to run the gui as thread
    public static void main(String[] args) {
        //thread
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //	make object of Login class

                    Login frame = new Login();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

```


in the Log in frame.java

```java

//put this in COnstrcutor 
  try {
            socket = new Socket("localhost", 8989);
            ReceiverFromServer.socket = socket;
            new ListenServer(socket, this).start();
        } catch (UnknownHostException e1) {
            JOptionPane.showMessageDialog(
                    null, "Cannot connect to server", "Error", JOptionPane.ERROR_MESSAGE);
            //e1.printStackTrace();
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(
                    null, "Cannot connect to server", "Error", JOptionPane.ERROR_MESSAGE);
            //e1.printStackTrace();
        }




  public void actionPerformed(ActionEvent e) {


// if sign up
            if (e.getSource() == btn_singup){
               Signup obj = new Signup(socket);
                obj.setVisible(true);

                //close current gui
                dispose();
            }
            
            // When Log in is clicked Action of login button
            if (e.getSource() == btn_login) {
            
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                Matcher matcher = pattern.matcher(txt_username.getText());
                boolean specichar = matcher.find();

                //checks if the username is valid
                if (!specichar && !txt_username.getText().contains(" ")) {
                    verify();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    // makes sure valid username + password is entered
                    if (ReceiverFromServer.in) {
                        ReceiverFromServer.myUsername = txt_username.getText();
                        ReceiverFromServer.mypassword = String.valueOf(txt_password.getPassword());
                       [Main Frame Class Name]= new [Main Frame Class];

                         [Main Frame Class Name].setVisible(true);
                        //close current gui
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(
                                null, "Wrong password or username", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null, "Username should not contain any special character or space",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
          // verify function
          
          public void Verify(){
                  try {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println("in " + txt_username.getText() + " " + String.valueOf(txt_password.getPassword()));
            System.out.println(in);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
          
          }
            
            
           
```


In sign up class, is bacially the same as sign in but with this different 

```java
// use this as action listener
     @Override
        public void actionPerformed(ActionEvent e) {

            // Action of signup button
            if (e.getSource() == btn_signup) {
                Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                Matcher matcher = pattern.matcher(txt_username.getText());
                boolean specichar = matcher.find();

                //checks if the username is valid
                if (!specichar && !txt_username.getText().contains(" ")) {
                    signUp();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    if (ReceiverFromServer.in) {
                        ReceiverFromServer.myUsername = txt_username.getText();
                        ReceiverFromServer.mypassword = String.valueOf(txt_password.getPassword());

                        Messagelist obj = new Messagelist();
                        obj.setVisible(true);
                        //close current gui
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(
                                null, "Username already exists", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null, "Username should not contain any special character or space",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                //creating object of messagelist class
            }
            if (e.getSource() == btn_login) {

                Login obj = new Login();
                obj.setVisible(true);
                //close current gui
                dispose();
                //creating object of messagelist class
            }
            
            


 public void signUp() {
        try {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println("up " + txt_username.getText() + " " + String.valueOf(txt_password.getPassword()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

ReceiverFromServer.java


```java
 */
public class ReceiverFromServer {

    static String[] array;

    static Socket socket;
    static boolean in = false;
    static String myUsername = "";
    static String mypassword = "";
    static ArrayList<String> removedList = new ArrayList<>();
    static String errorLog = "";

    static public void assingSocket(Socket socket) {
        ReceiverFromUser.socket = socket;
    }


    static public Socket getSocket() {
        return socket;
    }

    static String[] getAllUsers() {

        return array;
    }

    static void importUserfromServer(String totalUser) {
        // adding dummy data to list
        totalUser = totalUser.replace("[", "");
        totalUser = totalUser.replace("]", "");
        System.out.println(totalUser);
        array = totalUser.split(",");
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }

    }
}

```

ListenServer.java

```java
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.JOptionPane;

public class ListenServer extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private Login client;

    public ListenServer(Socket socket, Login client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                System.out.println("Received conversation from server: " + response);
               // Chat.setSendMessage("[server]" + response);
                if (!response.substring(0, 2).equals("in") && !response.substring(0, 2).equals("up"))
                    if (response.length() >= 4)
                        if (isNumeric(response.substring(0, 4))) {
                            try {
                                Chat.setSendMessage(response);
                            } catch (Exception e) {
                                Messagelist.userlist.setForeground(Color.red);
                            }
                        }


                String td = response.substring(0, 1);
                if (td.equals("!")) {
                    //System.out.println("Wrong password");

                    //Login.in = (false);
                    ReceiverFromServer.in = (false);
                } else if (td.equals("$")) {
                    //Login.in =(true);
                    ReceiverFromUser.in = (true);

                    System.out.println("Correct password");
                    ReceiverFromServer.importUserfromServer(response.substring(response.indexOf("#") + 1));
                    System.out.println("\n" + response);
                } else if (td.equals("#")) {
                    ReceiverFromServer.importUserfromServer(response.substring(response.indexOf("#") + 1));
                    System.out.println("\n received users and group:" + response);
                } else if (td.equals("&")) {
                	System.out.println(response.substring(1));
                	ReceiverFromServer.errorLog = response.substring(1);
                }
            } catch (IOException ex) {
                //ex.printStackTrace();
                System.out.println("Disconnected");
                break;
            }
        }
    }
}
```

in main frame .java

```java
// put this in the constructor 

        DefaultListModel<String> model = new DefaultListModel<>();
        for (String s : (ReceiverFromServer.getAllUsers())) {

            model.addElement(s.replace("$", ""));

        }

        userlist = new JList(model);
        if (ReceiverFromServer.removedList.size() > 0)
            ReceiverFromServer.removedList.remove("____");
        else
            ReceiverFromServer.removedList.add("____");


        // creating list that will contain all users
        ActionListener animation = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                model.removeAllElements();
                for (String s : (ReceiverFromServer.getAllUsers())) {
                    if (!ReceiverFromServer.removedList.contains(s))
                        model.addElement(s);


                }

                if (!ReceiverFromServer.errorLog.equals("")) {
                    JOptionPane.showMessageDialog(
                            null, ReceiverFromServer.errorLog +
                                    "\nThe program will now exit", "Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                }

            }

        };
        Timer timer = new Timer(5000, animation);
        timer.start();

```


in Chat Frame. java


```java

// if a button is clicked
    if (e.getSource() == sendButton) {
                if (messagetext.getText().replaceAll(" ", "").equals("")) {
                    return;
                } //checking if the message is empty

                //time stamp
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                String time = dtf.format(now);



                //tidying the sent message




                //chatArea.append(time + " [" + ReceiverFromServer.myUsername + "]: " + messagetext.getText() + "\n");
                chatArea.append(time + " [" + ReceiverFromServer.myUsername + "]: guessed"+ "\n");
                OutputStream output;
                try {
                    output = ReceiverFromServer.socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);
                    writer.println(sendDm.trim() + " " + messagetext.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                messagetext.setText("");


// tell server to save data
    public void forceChange() {
        OutputStream output;
        try {
            output = ReceiverFromServer.socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            writer.println("**" + sendDm.trim() + "_" + chatArea.getText().replaceAll("[\\\t|\\\n|\\\r]",
                    "\\\\n"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }
    
// Put this in the constructor
 public Chat(String talktoUser) {
        sendDm = talktoUser;
        if (sendDm.substring(0, 1).equals("{")) {
            sendDm = sendDm.replace("{", "[");
            sendDm = sendDm.replace("}", "]");
            sendDm = sendDm.replace(";", ",");
        }
        if (sendDm.substring(0, 1).equals("[")) {
            sendDm = sendDm.replace("[", "");
            sendDm = sendDm.replace("]", "");
            System.out.println(sendDm);
            array = sendDm.split(",");
            sendDm = "";
            for (int i = 0; i < array.length; i++) {
                array[i] = array[i].trim();
                sendDm += "@" + array[i] + " ";
            }
        } else {
            sendDm = "@" + sendDm.trim();
        }
```
