package noGUI;

import campaigns.Campaign;
import campaigns.CampaignStatusType;
import users.User;

import java.util.ArrayList;

public class VMS {

    /* a Singleton class, containing the collections of users and campaigns */
    private static VMS instance = null;
    public ArrayList<Campaign> campaigns;
    public ArrayList<User> users;

    private VMS() {
        this.campaigns = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    /* when called, the static method instantiates and returns the instance of the noGUI.VMS object,
     * implementing the lazy initialization */
    public static VMS getInstance() {
        if (instance == null)
            instance = new VMS();
        return instance;
    }

    /* adds a campaign to the campaign ArrayList */
    public void addCampaign(Campaign campaign) {
        this.campaigns.add(campaign);
    }

    /* returns all the campaigns */
    public ArrayList<Campaign> getCampaigns() {
        return campaigns;
    }

    /* returns the campaign with the given id */
    public Campaign getCampaign(Integer id) {
        for (Campaign campaign : campaigns) {
            if (campaign.getCampaignID().equals(id))
                return campaign;
        }
        return null;
    }

    /* updates a campaign if its status is NEW or STARTED */
    public void updateCampaign(Integer id, Campaign campaign) {
        Campaign updated = getCampaign(id);

        if (getCampaign(id).getCampaignStatus().equals(CampaignStatusType.CANCELLED) ||
                getCampaign(id).getCampaignStatus().equals(CampaignStatusType.EXPIRED))
            return;

        if (getCampaign(id).getCampaignStatus().equals(CampaignStatusType.NEW)) {
            updated.setCampaignDescription(campaign.getCampaignDescription());
            updated.setAvailableVouchers(campaign.getAvailableVouchers());
            updated.setStartDate(campaign.getStartDate());
        }

        updated.setEndDate(campaign.getEndDate());
        updated.setDistributableVouchers(campaign.getDistributableVouchers());
    }

    /* cancels a campaign if the action is possible */
    public void cancelCampaign(Integer id) {
        Campaign campaign = getCampaign(id);
        if (campaign.getCampaignStatus().equals(CampaignStatusType.EXPIRED) ||
                campaign.getCampaignStatus().equals(CampaignStatusType.CANCELLED))
            return;
        campaign.setCampaignStatus(CampaignStatusType.CANCELLED);
    }

    /* adds a user to the ArrayList */
    public void addUser(User user) {
        users.add(user);
    }

    /* returns the user with the given id */
    public User getUser(Integer userId) {
        for (User user : users) {
            if (user.getUserID().equals(userId))
                return user;
        }
        return null;
    }

    /* returns the user with the given email */
    public User getUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email))
                return user;
        }
        return null;
    }

    /* returns all the users */
    public ArrayList<User> getUsers() {
        return users;
    }
}
