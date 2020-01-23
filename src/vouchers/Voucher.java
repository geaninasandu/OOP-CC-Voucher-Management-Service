package vouchers;

import java.time.LocalDateTime;

public abstract class Voucher {

    /* an abstract class which shapes the model of a voucher */
    /* a voucher contains information about its ID, code, usage date, type and status, as well as
     * information about the user and campaign */

    Integer voucherID, campaignID;
    String voucherCode, userEmail;
    LocalDateTime usageDate;
    VoucherStatusType voucherStatus;

    public Voucher(Integer voucherID, String voucherCode, Integer campaignID, String userEmail,
                   VoucherStatusType voucherStatus) {
        this.voucherID = voucherID;
        this.voucherCode = voucherCode;
        this.voucherStatus = voucherStatus;
        this.userEmail = userEmail;
        this.campaignID = campaignID;
    }

    public abstract String toString();

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
