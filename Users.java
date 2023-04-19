import java.util.ArrayList;


public class Users {

    ArrayList<String> hiddenUsers = new ArrayList<>();
    private String username;
    private String psswd;
    int coins;
    // constructor
    public Users(String username, String psswd, int coins) {
        this.username = username;
        this.psswd = psswd;
        this.coins = coins;
        hiddenUsers.add("__");
    }

    // add hidden
    public void addHiddenUsers(String hide) {
        hiddenUsers.add(hide);
    }

    // set removed user
    public void setHiddenUsers(ArrayList<String> hiddenUsers) {
        this.hiddenUsers = hiddenUsers;
    }

    // hidden users list if users decided to remove another user from a list
    public ArrayList<String> getHiddenUser() {
        return hiddenUsers;
    }

    public void addCoins(int amount){
        coins = coins + amount;
    }

    public int getCoins(){
        return coins;
    }
    public void lossCoins(int amount) {
        coins = coins - amount;
    }
    // get user username
    public String getUsername() {
        return username;
    }

    // set user username
    public void setUsername(String username) {
        this.username = username;
    }

    // get user password
    public String getPsswd() {
        return psswd;
    }

    // set user password
    public void setPsswd(String psswd) {
        this.psswd = psswd;
    }

}