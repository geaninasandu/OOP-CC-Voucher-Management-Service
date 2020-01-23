package vouchers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoyaltyVoucher extends Voucher {

    private float discount;

    public LoyaltyVoucher(Integer voucherID, String voucherCode, Integer campaignID,
                          String userEmail, VoucherStatusType voucherStatus, float discount) {
        super(voucherID, voucherCode, campaignID, userEmail, voucherStatus);
        this.discount = discount;
    }

    public String toString() {
        String voucher = "[" + voucherID + ";" + voucherStatus + ";" + userEmail + ";" + discount + ";" +
                campaignID + ";";
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
