import gui.LoginScreen;
import noGUI.VMS;
import utils.Parser;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TestGUI {
    public static void main(String[] args) throws IOException {

        VMS vms = VMS.getInstance();

        File users = new File(args[0]);
        File campaigns = new File(args[1]);

        Parser parser = new Parser(users, campaigns, vms);
        parser.parseUserData();
        parser.parseCampaignData();

        JFrame appFrame = new JFrame("VMS - Voucher Management Service");
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setMinimumSize(new Dimension(1200, 840));
        appFrame.setLocationRelativeTo(null);

        appFrame.add(new LoginScreen(vms, appFrame).jSplitPane);

        appFrame.pack();
        appFrame.setVisible(true);
    }
}
