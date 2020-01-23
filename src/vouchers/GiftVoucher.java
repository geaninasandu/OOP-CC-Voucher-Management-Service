package vouchers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GiftVoucher extends Voucher {

    private float budget;

    public GiftVoucher(Integer voucherID, String voucherCode, Integer campaignID, String userEmail,
                       VoucherStatusType voucherStatus, float budget) {
        super(voucherID, voucherCode, campaignID, userEmail, voucherStatus);
        this.budget = budget;
    }

    /* determines the output of a voucher */
    public String toString() {
        String voucher = "[" + voucherID + ";" + voucherStatus + ";" + userEmail + ";" + budget +
                ";" + campaignID + ";";

        /* formats the date if it is not null, to avoid NullPointerException */
        if (usageDate != null) {
            voucher += DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(usageDate);
            return voucher + "]";
        }

        return voucher + null + "]";
    }

    public Integer getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(Integer voucherID) {
        this.voucherID = voucherID;
    }

    public Integer getCampaignID() {
        return campaignID;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUsageDate(LocalDateTime usageDate) {
        this.usageDate = usageDate;
    }

    public VoucherStatusType getVoucherStatus() {
        return voucherStatus;
    }

    public void setVoucherStatus(VoucherStatusType voucherStatus) {
        this.voucherStatus = voucherStatus;
    }
}
