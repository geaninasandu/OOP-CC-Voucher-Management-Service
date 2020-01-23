package notifications;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Notification {

    /* a Notification object contains information about its type, date, the campaign that was
     * modified, and a list of the voucher IDs that the user received within the campaign */

    public NotificationType notificationType;
    public LocalDateTime notificationDate;
    public Integer campaignID;
    private ArrayList<Integer> voucherIDs;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Notification(Integer campaignID, NotificationType notificationType,
                        LocalDateTime notificationDate) {
        this.campaignID = campaignID;
        this.notificationDate = notificationDate;
        this.notificationType = notificationType;
    }

    public void setVoucherIDs(ArrayList<Integer> voucherIDs) {
        this.voucherIDs = new ArrayList<>();
        this.voucherIDs.addAll(voucherIDs);
    }

    public String toString() {
        return campaignID + ";" + voucherIDs + ";" + notificationDate.format(formatter) + ";" +
                notificationType;
    }
}
