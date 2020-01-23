package gui;

import campaigns.Campaign;
import noGUI.VMS;
import notifications.Notification;
import users.User;
import users.UserType;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LoginScreen {

    public JSplitPane jSplitPane;
    private JPanel contentPanel, header;
    private Path imagesDirectory = Paths.get("");
    private String imagesPath = imagesDirectory.toAbsolutePath().toString() + "\\images\\";
    private SpringLayout springLayout = new SpringLayout();
    private VMS vms;

    public LoginScreen(VMS vms, JFrame appFrame) throws IOException {
        this.vms = vms;

        contentPanel = initLoginPanel();

        jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        header = new Header().header;

        jSplitPane.setTopComponent(header);
        jSplitPane.setBottomComponent(contentPanel);

        jSplitPane.setEnabled(false);
        jSplitPane.setDividerSize(0);
        appFrame.getContentPane().add(jSplitPane);

    }

    private JPanel initLoginPanel() throws IOException {
        contentPanel = new JPanel();

        contentPanel.setPreferredSize(new Dimension(1200, 670));
        contentPanel.setBackground(Color.decode("#f6f5fa"));

        JLabel welcome = new JLabel("Welcome!");
        welcome.setFont(new Font("Tahoma", Font.PLAIN, 42));
        welcome.setVisible(true);
        contentPanel.add(welcome);

        springLayout.putConstraint(SpringLayout.NORTH, welcome, 183,
                SpringLayout.NORTH, contentPanel);
        springLayout.putConstraint(SpringLayout.WEST, welcome, 512,
                SpringLayout.WEST, contentPanel);

        JLabel incorrect = addIncorrectLabel(contentPanel);
        contentPanel.add(incorrect);
        addTicket(contentPanel);
        addLoginScreen(contentPanel, incorrect);
        addLoginContainer(contentPanel);

        return contentPanel;
    }

    private void addLoginContainer(JPanel contentPanel) {
        JPanel loginContainer = new JPanel();
        loginContainer.setBackground(Color.decode("#d4def1"));
        loginContainer.setPreferredSize(new Dimension(462, 462));

        contentPanel.setLayout(springLayout);

        springLayout.putConstraint(SpringLayout.WEST, loginContainer, 368,
                SpringLayout.WEST, contentPanel);

        springLayout.putConstraint(SpringLayout.NORTH, loginContainer, 94,
                SpringLayout.NORTH, contentPanel);

        contentPanel.add(loginContainer);
    }

    private void addTicket(JPanel contentPanel) throws IOException {
        String ticketPath = imagesPath + "ticket.png";
        BufferedImage ticket = ImageIO.read(new File(ticketPath));
        JLabel ticketLabel = new JLabel(new ImageIcon(ticket));

        springLayout.putConstraint(SpringLayout.NORTH, ticketLabel, 54,
                SpringLayout.NORTH, contentPanel);
        springLayout.putConstraint(SpringLayout.WEST, ticketLabel, 522,
                SpringLayout.WEST, contentPanel);

        contentPanel.add(ticketLabel);
        ticketLabel.setVisible(true);
    }

    private void addLoginScreen(JPanel contentPanel, JLabel incorrect) {
        JTextField username = new JTextField(12);
        TextPrompt usernamePrompt = new TextPrompt(" Username", username);

        username.setFont(new Font("Tahoma", Font.PLAIN, 35));
        springLayout.putConstraint(SpringLayout.NORTH, username, 280,
                SpringLayout.NORTH, contentPanel);
        springLayout.putConstraint(SpringLayout.WEST, username, 423,
                SpringLayout.WEST, contentPanel);

        usernamePrompt.setForeground(Color.GRAY);
        usernamePrompt.setFont(new Font("Arial", Font.PLAIN, 24));

        contentPanel.add(username);

        JPasswordField password = new JPasswordField(12);
        TextPrompt passwordPrompt = new TextPrompt(" Password", password);

        password.setEchoChar('*');
        password.setFont(new Font("Tahoma", Font.PLAIN, 35));
        springLayout.putConstraint(SpringLayout.NORTH, password, 350,
                SpringLayout.NORTH, contentPanel);
        springLayout.putConstraint(SpringLayout.WEST, password, 423,
                SpringLayout.WEST, contentPanel);

        passwordPrompt.setForeground(Color.GRAY);
        passwordPrompt.setFont(new Font("Arial", Font.PLAIN, 24));

        contentPanel.add(password);

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(265, 62));
        loginButton.setVisible(true);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.decode("#52a9bd"));
        loginButton.setFont(new Font("Arial", Font.PLAIN, 35));
        loginButton.setFocusable(false);
        loginButton.setBorder(null);

        springLayout.putConstraint(SpringLayout.NORTH, loginButton, 440,
                SpringLayout.NORTH, contentPanel);
        springLayout.putConstraint(SpringLayout.WEST, loginButton, 467,
                SpringLayout.WEST, contentPanel);

        loginButton.addActionListener(e -> {
            String uname = username.getText().toUpperCase();
            char[] pass1 = password.getPassword();
            String passwrd = null;

            if (loginButton.getText().equals("Login")) {
                for (User user1 : vms.users) {
                    if (user1.getUsername().toUpperCase().equals(uname)) {
                        passwrd = user1.getPassword();
                        if (passwrd.equals(String.valueOf(pass1))) {
                            contentPanel.removeAll();
                            contentPanel.updateUI();
                            header.removeAll();
                            header.updateUI();

                            if (user1.getUserType().equals(UserType.ADMIN)) {
                                try {
                                    addAdminScreen(vms, user1);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                try {
                                    addGuestScreen(vms, user1);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        } else {
                            incorrect.setVisible(true);
                        }
                    }
                }
                if (passwrd == null) {
                    incorrect.setVisible(true);
                }
            }
        });

        contentPanel.add(loginButton);
    }

    private void addGuestScreen(VMS vms, User guest) throws IOException {
        addLogoutHeader(guest);

        GuestScreen guestScreen = new GuestScreen(vms, guest);
        contentPanel.add(guestScreen.guestPanel);
    }

    private void addAdminScreen(VMS vms, User user) throws IOException {
        AdminScreen adminScreen = new AdminScreen(vms);
        contentPanel.add(adminScreen.adminPanel);

        addLogoutHeader(user);
    }

    private void addLogoutHeader(User user) throws IOException {
        Header headerPane = new Header();
        header = headerPane.header;
        header.setLayout(springLayout);

        JButton logout = new JButton();

        String imagePath = imagesPath + "logout.png";

        BufferedImage logoutImage = ImageIO.read(new File(imagePath));
        logout.setIcon(new ImageIcon(logoutImage));
        logout.setBackground(Color.WHITE);
        logout.setBorder(null);
        logout.setFocusable(false);

        springLayout.putConstraint(SpringLayout.WEST, logout, 1150,
                SpringLayout.WEST, header);
        springLayout.putConstraint(SpringLayout.NORTH, logout, 70,
                SpringLayout.NORTH, header);

        springLayout.putConstraint(SpringLayout.NORTH, headerPane.logoLabel, 36,
                SpringLayout.NORTH, header);
        springLayout.putConstraint(SpringLayout.WEST, headerPane.logoLabel, 70,
                SpringLayout.WEST, header);

        logout.addActionListener(e -> {
            try {

                jSplitPane.setTopComponent(new Header().header);
                jSplitPane.setBottomComponent(initLoginPanel());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        header.add(logout);

        if (user.getUserType().equals(UserType.GUEST)) {
            JButton notification = new JButton();

            String notificationPath = imagesPath + "notification.png";

            BufferedImage notificationImage = ImageIO.read(new File(notificationPath));
            notification.setIcon(new ImageIcon(notificationImage));
            notification.setBackground(Color.WHITE);
            notification.setBorder(null);
            notification.setFocusable(false);

            springLayout.putConstraint(SpringLayout.WEST, notification, 1070,
                    SpringLayout.WEST, header);
            springLayout.putConstraint(SpringLayout.NORTH, notification, 70,
                    SpringLayout.NORTH, header);

            notification.addActionListener(e -> addNotificationTab(user));

            header.add(notification);
        }

        jSplitPane.setTopComponent(header);
    }

    private void addNotificationTab(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        JPanel notificationsTab = new JPanel();
        notificationsTab.setLayout(springLayout);
        notificationsTab.setBackground(Color.WHITE);

        ArrayList<Notification> notifications = new ArrayList<>(user.notifications);

        int notificationNumber = notifications.size();

        Object[][] notificationData = new Object[notificationNumber][5];

        for (int i = 0; i < notificationNumber; i++) {
            Campaign currentCampaign = vms.getCampaign(notifications.get(i).campaignID);

            notificationData[i][0] = String.valueOf(notifications.get(i).campaignID);
            notificationData[i][1] = String.valueOf(currentCampaign.getCampaignName());
            notificationData[i][2] = notifications.get(i).notificationDate.format(formatter);
            notificationData[i][3] = notifications.get(i).notificationType;
        }

        String[] columnNames = {"ID", "Name", "Date", "Type"};

        DefaultTableModel model = new DefaultTableModel(notificationData, columnNames);

        JTable notificationTable = new JTable(model) {

            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component returnComp = super.prepareRenderer(renderer, row, column);
                Color alternateColor = Color.decode("#f6f5fa");
                Color whiteColor = Color.WHITE;
                if (!returnComp.getBackground().equals(getSelectionBackground())) {
                    Color bg = (row % 2 != 0 ? alternateColor : whiteColor);
                    returnComp.setBackground(bg);
                }
                return returnComp;
            }
        };

        JTableHeader tableHeader = notificationTable.getTableHeader();
        tableHeader.setBackground(Color.decode("#d4def1"));
        tableHeader.setFont(new Font("Arial", Font.PLAIN, 18));

        notificationTable.setShowHorizontalLines(false);


        JScrollPane jScrollPane = new JScrollPane(notificationTable);
        jScrollPane.setPreferredSize(new Dimension(580, 450));
        jScrollPane.setHorizontalScrollBar(null);
        jScrollPane.getViewport().setBackground(Color.WHITE);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < 4; i++) {
            notificationTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        notificationTable.setFocusable(false);
        notificationTable.setAutoCreateRowSorter(true);

        notificationTable.setRowHeight(30);
        notificationTable.setFont(new Font("Arial", Font.PLAIN, 15));

        springLayout.putConstraint(SpringLayout.NORTH, jScrollPane, 0,
                SpringLayout.NORTH, notificationsTab);
        springLayout.putConstraint(SpringLayout.WEST, jScrollPane, 0,
                SpringLayout.WEST, notificationsTab);

        notificationsTab.add(jScrollPane);

        UIManager.put("OptionPane.minimumSize", new Dimension(600, 550));
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("OptionPane.messagebackground", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        JOptionPane.showConfirmDialog(null, notificationsTab,
                "  Notifications", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    private JLabel addIncorrectLabel(JPanel contentPanel) {
        JLabel incorrect = new JLabel("Your password is incorrect!");
        incorrect.setFont(new Font("Arial", Font.PLAIN, 14));
        incorrect.setForeground(Color.RED);
        incorrect.setVisible(false);

        springLayout.putConstraint(SpringLayout.NORTH, incorrect, 410,
                SpringLayout.NORTH, contentPanel);
        springLayout.putConstraint(SpringLayout.WEST, incorrect, 510,
                SpringLayout.WEST, contentPanel);

        return incorrect;
    }
}