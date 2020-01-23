package campaigns;

import notifications.Notification;
import users.User;
import utils.CodeGenerator;
import vouchers.GiftVoucher;
import vouchers.LoyaltyVoucher;
import vouchers.Voucher;
import vouchers.VoucherStatusType;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Campaign {

    /* a Campaign object contains information about the ID, name, description and other stuff,
     * a collection of observers and a map of the users that received vouchers within the campaign */

    public CampaignVoucherMap campaignVoucherMap;
    public ArrayList<Voucher> campaignVouchers;
    public ArrayList<User> observers;
    private Integer campaignID, distributableVouchers, availableVouchers;
    private String campaignName, campaignDescription;
    private LocalDateTime startDate, endDate;
    private CampaignStatusType campaignStatus;

    public Campaign(Integer campaignID, String campaignName, String campaignDescription,
                    LocalDateTime startDate, LocalDateTime endDate, Integer distributableVouchers,
                    Integer availableVouchers) {
        this.campaignVoucherMap = new CampaignVoucherMap();
        this.observers = new ArrayList<>();
        this.campaignVouchers = new ArrayList<>();
        this.campaignDescription = campaignDescription;
        this.campaignID = campaignID;
        this.campaignName = campaignName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.distributableVouchers = distributableVouchers;
        this.availableVouchers = availableVouchers;
    }

    /* returns all the vouchers distributed within the campaign */
    public ArrayList<Voucher> getVouchers() {
        return campaignVouchers;
    }

    /* returns the voucher with the specified code */
    private Voucher getVoucher(String code) {
        for (Voucher voucher : campaignVouchers) {
            if (voucher.getVoucherCode().equals(code))
                return voucher;
        }
        return null;
    }

    /* generates and returns a voucher with the given information */
    public Voucher generateVoucher(String email, String voucherType, float value) {
        Voucher newVoucher;

        /* generates a random code */
        String code = generateCode();

        /* instantiates a new voucher, depending on the voucherType specified */
        if (voucherType.equals("GiftVoucher")) {
            newVoucher = new GiftVoucher(1, code, this.campaignID, email,
                    VoucherStatusType.UNUSED, value);
        } else {
            newVoucher = new LoyaltyVoucher(1, code, this.campaignID, email,
                    VoucherStatusType.UNUSED, value);
        }

        return newVoucher;
    }

    /* generates a random code of 5 digits, using the CodeGenerator class in the utils package */
    private String generateCode() {
        String code = new CodeGenerator().getCode();
        if (uniqueCode(code))
            return code;
        else
            return generateCode();
    }

    /* checks whether the code is unique */
    private boolean uniqueCode(String code) {
        for (Voucher voucher : campaignVouchers) {
            if (voucher.getVoucherCode().equals(code))
                return false;
        }
        return true;
    }

    /* returns the voucher with the given ID */
    public Voucher getVoucherByID(Integer voucherId) {
        for (Voucher voucher : campaignVouchers) {
            if (voucher.getVoucherID().equals(voucherId))
                return voucher;
        }
        return null;
    }

    /* sets a voucher's status to USED if the action is possible */
    public void redeemVoucher(String code, LocalDateTime date) {
        Voucher voucher = getVoucher(code);
        assert voucher != null;
        if (date.isBefore(endDate) && date.isAfter(startDate)) {
            voucher.setVoucherStatus(VoucherStatusType.USED);
            voucher.setUsageDate(date);
        }
    }

    /* adds an user to the collection of observers */
    public void addObserver(User user) {
        this.observers.add(user);
    }

    /* returns all the observers */
    public ArrayList<User> getObservers() {
        return this.observers;
    }

    /* sends a notification to all the campaign's observers */
    public void notifyAllObservers(Notification notification) {
        for (User observer : observers) {
            observer.update(notification);
        }
    }

    public Integer getCampaignID() {
        return campaignID;
    }

    public Integer getDistributableVouchers() {
        return distributableVouchers;
    }

    public void setDistributableVouchers(Integer distributableVouchers) {
        this.distributableVouchers = distributableVouchers;
    }

    public Integer getAvailableVouchers() {
        return availableVouchers;
    }

    public void setAvailableVouchers(Integer availableVouchers) {
        this.availableVouchers = availableVouchers;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public String getCampaignDescription() {
        return campaignDescription;
    }

    public void setCampaignDescription(String campaignDescription) {
        this.campaignDescription = campaignDescription;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public CampaignStatusType getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(CampaignStatusType campaignStatus) {
        this.campaignStatus = campaignStatus;
    }
}
