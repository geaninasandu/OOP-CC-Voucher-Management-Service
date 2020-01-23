package noGUI;

import utils.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Test {

    /* implements the test for the no-GUI program */
    public static void main(String[] args) throws IOException {

        /* opens the corresponding files and parses their content
        args[0] = users.txt; args[1] = campaigns.txt; args[2] = events.txt; args[3] = output.txt*/

        File users = new File(args[0]);
        File campaigns = new File(args[1]);
        File events = new File(args[2]);
        FileWriter fileWriter = new FileWriter(args[3]);

        /* instantiates a parser object, which reads the input files and executes the actions
         * required */
        Parser parser = new Parser(users, campaigns, events, fileWriter);
        parser.parseUserData();
        parser.parseCampaignData();
        parser.parseEventsData();
    }
}
