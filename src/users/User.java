package users;

import notifications.Notification;
import vouchers.Voucher;

import java.util.ArrayList;

public class User {

    /* a User object contains information about its ID, type ,credentials, as well as a map
     * which links the campaign ID to the vouchers distributed from it and a collection of
     * notifications*/

    public UserVoucherMap userVoucherMap;
    public ArrayList<Voucher> userVouchers;
    public ArrayList<Notification> notifications;
    private Integer userID;
    private UserType userType;
    private String username, email, password;

    public User(Integer userID, String username, String password, String email, UserType userType) {
        this.userVouchers = new ArrayList<>();
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.userVoucherMap = new UserVoucherMap();
        this.notifications = new ArrayList<>();
    }

    /* implementing the Observer design pattern, adds the notification to the user's ArrayList of
     * notifications */
    public void update(Notification notification) {
        notifications.add(notification);
    }

    /* returns the IDs of the vouchers distributed in a certain campaign */
    public ArrayList<Integer> getVoucherIDs(Integer campaignID) {
        ArrayList<Integer> voucherIDs = new ArrayList<>();
        ArrayList<Voucher> vouchers = userVoucherMap.userVoucherMap.get(campaignID);

        for (Voucher voucher : vouchers) {
            voucherIDs.add(voucher.getVoucherID());
        }

        return voucherIDs;
    }

    public String toString() {
        return "[" + userID + ";" + username + ";" + email + ";" + userType + "]";
    }

    public Integer getUserID() {
        return userID;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
