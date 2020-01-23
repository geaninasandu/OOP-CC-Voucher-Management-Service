package utils;

import campaigns.Campaign;
import campaigns.CampaignStatusType;
import noGUI.VMS;
import notifications.Notification;
import notifications.NotificationType;
import users.User;
import users.UserType;
import vouchers.Voucher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Parser {

    /* parses the data in each input file, executing the required actions */
    private final Integer usersNumber, campaignsNumber;
    private final LocalDateTime currentDate;
    private Integer eventsNumber;
    private FileReader fileReader;
    private PrintWriter printWriter;
    private VMS vms;

    /* determines the format for the date */
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Parser(File users, File campaigns, File events, FileWriter fileWriter)
            throws IOException {
        /* calls the FileReader constructor, found in the utils package, and retrieves
         * information about the number of events, campaigns and users */
        this.fileReader = new FileReader(users, campaigns, events);
        this.usersNumber = fileReader.getUsersNumber();
        this.campaignsNumber = fileReader.getCampaignsNumber();
        this.currentDate = fileReader.getCurrentDate();
        this.eventsNumber = fileReader.getEventsNumber();

        /* instantiates the printer for the output file */
        this.printWriter = new PrintWriter(fileWriter);

        /* instance of the noGUI.VMS Singleton object, using the getInstance() lazy initialization */
        this.vms = VMS.getInstance();
    }

    public Parser(File users, File campaigns, VMS vms) throws IOException {
        this.fileReader = new FileReader(users, campaigns);
        this.usersNumber = fileReader.getUsersNumber();
        this.campaignsNumber = fileReader.getCampaignsNumber();
        this.currentDate = LocalDateTime.now();

        this.vms = vms;
    }

    /* parses user data from file and creates a new User object, then adds the new user to
    the noGUI.VMS array of users */
    public void parseUserData() {
        /* iterates over the lines of the user.txt file, adding data about each user
         * to an array of Strings */
        for (int i = 0; i < usersNumber; i++) {
            String[] userData = fileReader.userScanner.nextLine().split(";");

            User user;
            Integer userId = Integer.valueOf(userData[0]);
            String username = userData[1];
            String password = userData[2];
            String email = userData[3];
            UserType userType = UserType.valueOf(userData[4]);

            /* creates a new User object with the given properties, adding it to the noGUI.VMS */
            user = new User(userId, username, password, email, userType);
            vms.addUser(user);
        }
    }

    /* parses campaign data from file and creates a new object, then adds it to the noGUI.VMS array
    of campaigns */
    public void parseCampaignData() {
        /* reads the second line of the file */
        fileReader.campaignScanner.nextLine();

        /* iterates over the lines of the campaigns.txt file, adding data about each campaign
         * to an array of Strings, creating a new Campaign object, which is then added to the
         * noGUI.VMS database */
        for (int i = 0; i < campaignsNumber; i++) {
            Campaign campaign;
            String[] campaignData;

            /* adds each field of the campaign data to the array of Strings, using the
             * character ";" as the field delimiter */
            campaignData = fileReader.campaignScanner.nextLine().split(";");

            /* retrieves the fields corresponding to the Campaign member variables */
            Integer campaignId = Integer.valueOf(campaignData[0]);
            String campaignName = campaignData[1];
            String campaignDescription = campaignData[2];
            LocalDateTime startDate = LocalDateTime.parse(campaignData[3], formatter);
            LocalDateTime endDate = LocalDateTime.parse(campaignData[4], formatter);
            Integer distributableVouchers = Integer.valueOf(campaignData[5]);

            /* calls the constructor of the Campaign class, then adds the new object to noGUI.VMS */
            campaign = new Campaign(campaignId, campaignName, campaignDescription, startDate,
                    endDate, distributableVouchers, distributableVouchers);
            if (currentDate.isBefore(startDate)) {
                campaign.setCampaignStatus(CampaignStatusType.NEW);
            } else {
                campaign.setCampaignStatus(CampaignStatusType.STARTED);
            }
            vms.addCampaign(campaign);
        }
    }

    public void parseEventsData() {

        Integer campaignID, voucherID, availableVouchers, vouchersNumber;
        float value;
        String email, voucherType;
        LocalDateTime startDate, endDate;
        Campaign currentCampaign;
        Notification notification;

        /* iterates over the events.txt file, adding data to an array of Strings */
        for (int i = 0; i < eventsNumber; i++) {
            String[] eventData = fileReader.eventScanner.nextLine().split(";");
            Integer userID = Integer.parseInt(eventData[0]);
            String action = eventData[1];

            /* depending on the action displayed on the second field of each line, executes
             * certain instructions */
            switch (action) {

                case "generateVoucher":
                    /* generates a voucher with the given parameters */
                    campaignID = Integer.parseInt(eventData[2]);
                    email = eventData[3];
                    voucherType = eventData[4];
                    value = Float.parseFloat(eventData[5]);

                    currentCampaign = vms.getCampaign(campaignID);

                    /* gets the available vouchers of the current campaign*/
                    availableVouchers = currentCampaign.getAvailableVouchers();

                    /* if the number of available vouchers hasn't reached 0 and the user which
                     * generates the voucher is an admin, the available vouchers number of the
                     * campaign is decremented, the voucher is generated and added to the
                     * given user's map */
                    if ((availableVouchers != 0) && vms.getUser(userID).getUserType().
                            equals(UserType.ADMIN)) {
                        currentCampaign.setAvailableVouchers(--availableVouchers);

                        /* gets the user to whom the voucher is assigned*/
                        User givenUser = vms.getUserByEmail(email);

                        /* generates the voucher, setting its ID to the next consecutive value */
                        Voucher voucher = currentCampaign.generateVoucher(email,
                                voucherType, value);
                        voucher.setVoucherID(currentCampaign.campaignVouchers.size() + 1);

                        /* adds the voucher to the ArrayList of vouchers and to the ArrayMap
                         * of the campaign, respectively of the user */
                        currentCampaign.campaignVouchers.add(voucher);
                        currentCampaign.campaignVoucherMap.addVoucher(voucher);

                        givenUser.userVoucherMap.addVoucher(voucher);
                        givenUser.userVouchers.add(voucher);

                        /* adds the given user to the collection of observers of the current
                         * campaign (if not already a part of it) */
                        if (!currentCampaign.getObservers().contains(givenUser))
                            currentCampaign.addObserver(givenUser);
                    }
                    break;

                case "redeemVoucher":
                    /* self-explanatory - redeems a voucher, if the user doing that is an admin,
                     * calling the redeemVoucher() method of the campaign */
                    if (vms.getUser(userID).getUserType().equals(UserType.ADMIN)) {
                        campaignID = Integer.parseInt(eventData[2]);
                        voucherID = Integer.parseInt(eventData[3]);
                        LocalDateTime redeemDate = LocalDateTime.parse(eventData[4], formatter);

                        currentCampaign = vms.getCampaign(campaignID);
                        currentCampaign.redeemVoucher(currentCampaign.getVoucherByID(voucherID).
                                getVoucherCode(), redeemDate);
                    }
                    break;

                case "cancelCampaign":
                    /* cancels a campaign if the campaign is available and the user is an admin */
                    campaignID = Integer.parseInt(eventData[2]);
                    currentCampaign = vms.getCampaign(campaignID);

                    if (vms.getUser(userID).getUserType().equals(UserType.ADMIN) &&
                            !currentCampaign.getCampaignStatus().equals(CampaignStatusType.CANCELLED) &&
                            !currentCampaign.getCampaignStatus().equals(CampaignStatusType.EXPIRED)) {
                        vms.cancelCampaign(campaignID);

                        /* generates a cancel notification and sends it to all the observers */
                        notification = new Notification(campaignID, NotificationType.CANCEL,
                                currentDate);
                        vms.getCampaign(campaignID).notifyAllObservers(notification);
                    }
                    break;

                case "getNotifications":
                    User currentUser = vms.getUser(userID);

                    /* writes to the file all the notifications, if the user is a guest*/
                    if (vms.getUser(userID).getUserType().equals(UserType.GUEST)) {
                        /* for each notification, set the ArrayList of voucherIDs */
                        for (int j = 0; j < vms.getUser(userID).notifications.size(); j++) {

                            Notification currentNotification = currentUser.notifications.get(j);
                            ArrayList<Integer> voucherIDs = currentUser.
                                    getVoucherIDs(currentNotification.campaignID);

                            currentNotification.setVoucherIDs(voucherIDs);
                        }

                        /* write the notifications in the output file*/
                        printWriter.println(vms.getUser(userID).notifications.toString());
                    }
                    break;

                case "getVouchers":
                    /* prints the user's vouchers in the output file if he is a guest */
                    if (vms.getUser(userID).getUserType().equals(UserType.GUEST))
                        printWriter.println(vms.getUser(userID).userVouchers);
                    break;

                case "addCampaign":
                    /* creates a new campaign, if the user is an admin */
                    if (vms.getUser(userID).getUserType().equals(UserType.ADMIN)) {
                        campaignID = Integer.parseInt(eventData[2]);
                        startDate = LocalDateTime.parse(eventData[5], formatter);
                        endDate = LocalDateTime.parse(eventData[6], formatter);
                        vouchersNumber = Integer.valueOf(eventData[7]);

                        Campaign newCampaign = new Campaign(campaignID, eventData[3], eventData[4],
                                startDate, endDate, vouchersNumber, vouchersNumber);
                        if (currentDate.isBefore(startDate)) {
                            newCampaign.setCampaignStatus(CampaignStatusType.NEW);
                        } else {
                            newCampaign.setCampaignStatus(CampaignStatusType.STARTED);
                        }
                        vms.addCampaign(newCampaign);
                    }
                    break;

                case "editCampaign":
                    /* edits data about a campaign and sends a notification to the observers */
                    if (vms.getUser(userID).getUserType().equals(UserType.ADMIN)) {
                        campaignID = Integer.parseInt(eventData[2]);
                        startDate = LocalDateTime.parse(eventData[5], formatter);
                        endDate = LocalDateTime.parse(eventData[6], formatter);
                        vouchersNumber = Integer.valueOf(eventData[7]);

                        Campaign updated = new Campaign(campaignID, eventData[3], eventData[4],
                                startDate, endDate, vouchersNumber, vouchersNumber);
                        vms.updateCampaign(campaignID, updated);

                        notification = new Notification(campaignID, NotificationType.EDIT,
                                currentDate);
                        vms.getCampaign(campaignID).notifyAllObservers(notification);
                    }
                    break;

                case "getObservers":
                    /* if the user is an admin, writes to the file the observers of the given
                     * campaign */
                    campaignID = Integer.parseInt(eventData[2]);
                    if (vms.getUser(userID).getUserType().equals(UserType.ADMIN)) {
                        ArrayList<User> observers = vms.getCampaign(campaignID).getObservers();

                        printWriter.println(observers.toString());
                    }
                    break;

                default:
                    break;
            }
        }

        /* closes the output file */
        printWriter.close();
    }

}
