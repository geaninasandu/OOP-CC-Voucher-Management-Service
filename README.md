# VMS - Voucher Management Service

## Project description

The purpose of the homework is implementing a class hierarchy for designing a voucher management
application, consisting of both a raw file writting mechanism and a graphical user interface, built
using the legacy Swing toolkit.

## Running

#### Non-graphical version:

To run the program without a GUI, the main() method of the **Test** class in the noGUI package should
be run, having the arguments: *users.txt campaigns.txt events.txt output.txt* - specifying the input
files from which the data is retrieved, respectively the output file.

#### GUI version:

To run the GUI application, we run the main() method of the **TestGUI** class, with the arguments:
*usersGUI.txt campaignsGUI.txt*.

## Part 1 - No GUI

### Main classes:

* **Campaign** - Specifies a promotional campaign, containing details such as ID, name, description,
status, as well as an ArrayMap which maps the users who received vouchers in the given campaign to
the respective voucher IDs.
We can generate or redeem a voucher, which is then added to the collection of vouchers of the
given user, provided the campaign status is STARTED.
An admin can edit or cancel a campaign. Therefore, all the users who are subscribed to the
certain campaign should be notified. For that, we use the Observer Pattern: if a new user receives
a voucher within the campaign, he is added to an ArrayList of observers. When a change is made,
we call the notifyAllObservers method, to propagate a notification.


* **User** - Describes a user, by specifying the name, credentials, user type (ADMIN/GUEST) and a map
of the vouchers received in each campaign.
Furthermore, each user contains a collection of Notifications, which are received when a change
is made within a campaign.


* **Vouchers** - An abstract class, containing attributes such as ID, code, status (USED/UNUSED), user
email and campaign ID.
It can be a Gift Voucher or a Loyalty Voucher.


* **Notification** - A notification consists of a type, depending on the change issued by the admin
(EDIT/CANCEL), the date when it was issued and an ArrayList of the vouchers received by the user
within the given campaign.


* **VMS** - A singleton class, containing the collection of campaigns and users, which are added from
te input files.


* **Parser** - In order to test the functionality of the program, we design a Parser class, which reads
the input files and parses them, to obtain certain fields necessary to construct the campaigns or
users objects.
After adding the users and the campaigns, we parse the events.txt file, executing certain
instructions, depending on the action specified on each line. (eg. "generateVoucher" action calls
the method with the same name from the Campaign class).
We display the "get*" actions results in the output.txt file.

## Part 2 - GUI

Before opening the user interface, the program reads and parses the input in the campaignsGUI and
usersGUI files, and adds the users and campaigns to the VMS hierarchy.
The app opens the login window, where the user types in the credentials, which are then checked.


If the logged in user is an admin, the app creates a new panel, containing a table that displays
information about each campaign, each campaign containing 4 buttons (addVoucher, editCampaign,
campaignInfo and cancelCampaign).
Each of these buttons opens a dialog box, which require the user to insert certain details,
acknowledge an action or simply display some pieces of information.
To add a new campaign, we click the Add Campaign button on the top of the table (obviously) and
insert the required details. The table is then imediately updated with the new campaign added.
By clicking on a row of the table, the app opens a new dialog box, which contains the table of
vouchers generated within the campaign. We can search for a voucher using the search text field.
The header of te application contains a logout button, which re-opens the login panel.

If the user is a guest, the app opens a new panel, containing the table of campaigns to which the
user is subscribed.
By clicking on a campaign, the user can see the vouchers he received.
The header of the app contains a notification bell button, which opens a tabel of all the
notifications received by the user.
