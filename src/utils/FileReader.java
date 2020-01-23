package utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

class FileReader {

    Scanner userScanner, campaignScanner, eventScanner;

    /* reads data from the input files and returns certain fields */
    FileReader(File users, File campaigns, File events) throws IOException {
        this.userScanner = new Scanner(users);
        this.campaignScanner = new Scanner(campaigns);
        this.eventScanner = new Scanner(events);
    }

    FileReader(File users, File campaigns) throws IOException {
        this.userScanner = new Scanner(users);
        this.campaignScanner = new Scanner(campaigns);
    }

    /* returns the user number */
    Integer getUsersNumber() {
        return Integer.valueOf(userScanner.nextLine());
    }

    /* returns the campaigns number */
    Integer getCampaignsNumber() {
        return Integer.valueOf(campaignScanner.nextLine());
    }

    /* returns events number */
    Integer getEventsNumber() {
        return Integer.valueOf(eventScanner.nextLine());
    }

    /* returns the current date */
    LocalDateTime getCurrentDate() {
        /* converts the String containing the current date to a LocalDateTime object, using
        a formatter to match the format specified in the file */
        String date = eventScanner.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(date, formatter);
    }
}
