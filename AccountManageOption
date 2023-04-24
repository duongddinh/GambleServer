import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class MyActionListener implements ActionListener {

    
    public void actionPerformed(ActionEvent e) {

        //listener for the edit button for changing the username
        if (e.getSource() == editusername) {
            OutputStream output;
            try {
                output = ReceiverFromServer.socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println("!#" + usernametext.getText());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            String olduser = ReceiverFromServer.myUsername;
            ReceiverFromServer.myUsername = usernametext.getText();
            for (int i = 0; i < ReceiverFromServer.array.length; i++) {
                if (olduser.equals(ReceiverFromServer.array[i])) {
                    ReceiverFromServer.array[i] = ReceiverFromServer.myUsername;
                }
            }
        }

        //listener for the delete account button
        if (e.getSource() == deleteAccount) {
            OutputStream output;
            try {
                output = ReceiverFromServer.socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println("!!");
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Login obj = new Login();

            ReceiverFromServer.in = false;
            obj.setVisible(true);

            //close currrent GUi
            dispose();
        }

        //listener for the edit button for changing the password
        if (e.getSource() == password) {
            if (optext.getText().equals(ReceiverFromServer.mypassword)) {
                OutputStream output;
                try {
                    output = ReceiverFromServer.socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);
                    writer.println("$#" + nptext.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                ReceiverFromServer.mypassword = nptext.getText();
            } else {
                JOptionPane.showMessageDialog(
                        null, "Wrong password", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }
    }
}
