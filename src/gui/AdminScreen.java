package gui;

import campaigns.Campaign;
import campaigns.CampaignStatusType;
import noGUI.VMS;
import notifications.Notification;
import notifications.NotificationType;
import users.User;
import vouchers.Voucher;
import vouchers.VoucherStatusType;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class AdminScreen {

    JPanel adminPanel;
    private VMS vms;
    private JTable campaignTable, vouchersTable;
    private SpringLayout springLayout = new SpringLayout();
    private Path imagesDirectory = Paths.get("");
    private String imagesPath = imagesDirectory.toAbsolutePath().toString() + "\\images\\";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    AdminScreen(VMS vms) throws IOException {

        this.vms = vms;

        initAdminPanel();

        adminPanel.setLayout(springLayout);

        addTable();
        addNewCampaignButton();

        for (int i = 0; i < vms.campaigns.size(); i++) {
            addAddVoucherButton(i);
            addEditButton(i);
            addInfoButton(i);
            addCancelButton(i);
        }
    }

    static void setCampaignData(Object[][] campaignData, int i, Campaign currentCampaign, DateTimeFormatter formatter) {
        CampaignStatusType campaignStatus;
        campaignData[i][0] = String.valueOf(currentCampaign.getCampaignID());
        campaignData[i][1] = String.valueOf(currentCampaign.getCampaignName());
        campaignData[i][3] = currentCampaign.getStartDate().format(formatter);
        campaignData[i][4] = currentCampaign.getEndDate().format(formatter);

        if (LocalDateTime.now().isBefore(currentCampaign.getStartDate()))
            campaignStatus = CampaignStatusType.NEW;
        else if (LocalDateTime.now().isAfter(currentCampaign.getStartDate()) &&
                LocalDateTime.now().isBefore(currentCampaign.getEndDate()))
            campaignStatus = CampaignStatusType.STARTED;
        else
            campaignStatus = CampaignStatusType.EXPIRED;

        currentCampaign.setCampaignStatus(campaignStatus);
        campaignData[i][2] = String.valueOf(currentCampaign.getCampaignStatus());
    }

    private void initAdminPanel() {
        adminPanel = new JPanel();
        adminPanel.setBackground(Color.decode("#f6f5fa"));
        adminPanel.setPreferredSize(new Dimension(1200, 670));
        adminPanel.setVisible(true);
    }

    private void addTable() {
        int campaignNumber = vms.campaigns.size();

        Object[][] campaignData = new Object[campaignNumber][7];

        for (int i = 0; i < campaignNumber; i++) {
            Campaign currentCampaign = vms.getCampaign(i + 1);

            setCampaignData(campaignData, i, currentCampaign, formatter);
        }

        String[] columnNames = {"ID", "Name", "Status", "Start date", "End date"};

        DefaultTableModel model = new DefaultTableModel(campaignData, columnNames);

        campaignTable = new JTable(model) {

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

        JTableHeader tableHeader = campaignTable.getTableHeader();
        tableHeader.setBackground(Color.decode("#d4def1"));
        tableHeader.setFont(new Font("Arial", Font.PLAIN, 18));

        campaignTable.setShowHorizontalLines(false);


        JScrollPane jScrollPane = new JScrollPane(campaignTable);
        jScrollPane.setPreferredSize(new Dimension(750, 480));
        jScrollPane.setHorizontalScrollBar(null);
        jScrollPane.getViewport().setBackground(Color.WHITE);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < 5; i++) {
            campaignTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        campaignTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        campaignTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        campaignTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        campaignTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        campaignTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        campaignTable.getColumnModel().getColumn(4).setPreferredWidth(202);

        campaignTable.setFocusable(false);
        campaignTable.setAutoCreateRowSorter(true);

        campaignTable.setRowHeight(30);
        campaignTable.setFont(new Font("Arial", Font.PLAIN, 15));

        springLayout.putConstraint(SpringLayout.NORTH, jScrollPane, 110,
                SpringLayout.NORTH, adminPanel);
        springLayout.putConstraint(SpringLayout.WEST, jScrollPane, 235,
                SpringLayout.WEST, adminPanel);

        campaignTable.getSelectionModel().addListSelectionListener(e ->
                showVouchersPanel(campaignTable.getSelectedRow()));

        campaignTable.setVisible(true);

        adminPanel.add(jScrollPane);
    }

    private void showVouchersPanel(int row) {
        JPanel vouchersPanel = new JPanel();
        vouchersPanel.setBackground(Color.WHITE);
        vouchersPanel.setLayout(springLayout);

        Campaign currentCampaign = vms.getCampaign(row + 1);
        int vouchersNumber = currentCampaign.campaignVouchers.size();
        Object[][] voucherData = new Object[vouchersNumber][6];

        for (int i = 0; i < vouchersNumber; i++) {
            voucherData[i][0] = currentCampaign.campaignVouchers.get(i).getVoucherID();
            voucherData[i][1] = currentCampaign.campaignVouchers.get(i).getVoucherCode();
            voucherData[i][2] = currentCampaign.campaignVouchers.get(i).getUserEmail();
            voucherData[i][3] = currentCampaign.campaignVouchers.get(i).getVoucherStatus();
        }

        String[] columnNames = {"ID", "Code", "User email", "Status"};

        DefaultTableModel model = new DefaultTableModel(voucherData, columnNames);

        vouchersTable = new JTable(model) {

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

        JTableHeader tableHeader = vouchersTable.getTableHeader();
        tableHeader.setBackground(Color.decode("#d4def1"));

        vouchersTable.setShowHorizontalLines(false);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < 4; i++) {
            vouchersTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane vouchersPane = new JScrollPane(vouchersTable);
        vouchersPane.getViewport().setBackground(Color.WHITE);
        vouchersPane.setPreferredSize(new Dimension(580, 450));

        UIManager.put("OptionPane.background", Color.WHITE);

        springLayout.putConstraint(SpringLayout.NORTH, vouchersPane, 60,
                SpringLayout.NORTH, vouchersPanel);

        vouchersPanel.add(vouchersPane);
        addSearchVoucher(vouchersPanel);

        vouchersTable.getSelectionModel().addListSelectionListener(e ->
                markVoucherUsed(row, vouchersTable.getSelectedRow()));

        UIManager.put("OptionPane.minimumSize", new Dimension(600, 600));
        UIManager.put("OptionPane.messagebackground", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        JOptionPane.showConfirmDialog(null, vouchersPanel,
                "  Vouchers", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    private void markVoucherUsed(int campaignID, int voucherID) {
        JPanel voucherUsed = new JPanel();

        voucherUsed.setLayout(springLayout);
        voucherUsed.setBackground(Color.WHITE);
        Campaign currentCampaign = vms.getCampaign(campaignID + 1);

        JLabel informationLabel = new JLabel("Mark voucher " + currentCampaign.
                campaignVouchers.get(voucherID).getVoucherCode() + " as used?");

        UIManager.put("OptionPane.minimumSize", new Dimension(400, 110));
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("OptionPane.messagebackground", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);

        springLayout.putConstraint(SpringLayout.NORTH, informationLabel, 3,
                SpringLayout.NORTH, voucherUsed);
        springLayout.putConstraint(SpringLayout.WEST, informationLabel, 0,
                SpringLayout.WEST, voucherUsed);

        voucherUsed.add(informationLabel);

        int input = JOptionPane.showConfirmDialog(null, voucherUsed,
                "  Use voucher", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (input == JOptionPane.OK_OPTION) {
            vms.getCampaign(campaignID + 1).campaignVouchers.get(voucherID).
                    setVoucherStatus(VoucherStatusType.USED);

            vouchersTable.getModel().setValueAt(VoucherStatusType.USED, voucherID, 3);
        }
    }

    private void addSearchVoucher(JPanel vouchersPanel) {
        JTextField searchCode;
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        searchCode = RowFilterUtil.createRowFilter(vouchersTable);

        springLayout.putConstraint(SpringLayout.NORTH, searchLabel, 22,
                SpringLayout.NORTH, vouchersPanel);
        springLayout.putConstraint(SpringLayout.WEST, searchLabel, 10,
                SpringLayout.WEST, vouchersPanel);

        springLayout.putConstraint(SpringLayout.NORTH, searchCode, 20,
                SpringLayout.NORTH, vouchersPanel);
        springLayout.putConstraint(SpringLayout.WEST, searchCode, 60,
                SpringLayout.WEST, vouchersPanel);

        vouchersPanel.add(searchLabel);
        vouchersPanel.add(searchCode);

    }

    private void addNewCampaignButton() throws IOException {
        JButton addCampaign = new JButton("  Add campaign");

        String imagePath = imagesPath + "add_campaign.png";

        BufferedImage addCampaignImg = ImageIO.read(new File(imagePath));
        addCampaign.setIcon(new ImageIcon(addCampaignImg));
        addCampaign.setVisible(true);
        addCampaign.setBorder(null);
        addCampaign.setBackground(null);
        addCampaign.setFocusable(false);

        springLayout.putConstraint(SpringLayout.NORTH, addCampaign, 37,
                SpringLayout.NORTH, adminPanel);
        springLayout.putConstraint(SpringLayout.WEST, addCampaign, 235,
                SpringLayout.WEST, adminPanel);

        addCampaign.setFont(new Font("Arial", Font.PLAIN, 25));

        addCampaign.addActionListener(e -> {
            try {
                addNewCampaignDialog();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        adminPanel.add(addCampaign);
    }

    private void addNewCampaignDialog() throws IOException {
        JPanel campaignFields = new JPanel();
        campaignFields.setLayout(springLayout);
        campaignFields.setBackground(Color.WHITE);

        Font font = new Font("Arial", Font.PLAIN, 15);

        JLabel informationLabel = new JLabel("Insert new campaign details:");
        informationLabel.setFont(new Font("Arial", Font.PLAIN, 17));

        JLabel nameLabel = new JLabel("Name: ");
        JTextField campaignName = new JTextField(20);
        JLabel descriptionLabel = new JLabel("Description: ");
        JTextField campaignDescription = new JTextField(20);
        JLabel startDateLabel = new JLabel("Start date: ");
        JTextField startDate = new JTextField(20);
        JLabel endDateLabel = new JLabel("End date: ");
        JTextField endDate = new JTextField(20);
        JLabel vouchersLabel = new JLabel("Vouchers nr: ");
        JTextField vouchers = new JTextField(20);

        nameLabel.setFont(font);
        descriptionLabel.setFont(font);
        startDateLabel.setFont(font);
        endDateLabel.setFont(font);
        vouchersLabel.setFont(font);

        springLayout.putConstraint(SpringLayout.NORTH, informationLabel, 4,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, informationLabel, 23,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, nameLabel, 50,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, nameLabel, 0,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, descriptionLabel, 80,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, descriptionLabel, 0,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, startDateLabel, 110,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, startDateLabel, 0,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, endDateLabel, 140,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, endDateLabel, 0,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, vouchersLabel, 170,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, vouchersLabel, 0,
                SpringLayout.WEST, campaignFields);

        campaignFields.add(informationLabel);
        campaignFields.add(nameLabel);
        campaignFields.add(descriptionLabel);
        campaignFields.add(startDateLabel);
        campaignFields.add(endDateLabel);
        campaignFields.add(vouchersLabel);

        springLayout.putConstraint(SpringLayout.NORTH, campaignName, 50,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, campaignName, 95,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, campaignDescription, 80,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, campaignDescription, 95,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, startDate, 110,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, startDate, 95,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, endDate, 140,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, endDate, 95,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, vouchers, 170,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, vouchers, 95,
                SpringLayout.WEST, campaignFields);

        campaignFields.add(campaignName);
        campaignFields.add(campaignDescription);
        campaignFields.add(startDate);
        campaignFields.add(endDate);
        campaignFields.add(vouchers);

        UIManager.put("OptionPane.minimumSize", new Dimension(400, 280));
        UIManager.put("OptionPane.background", Color.WHITE);

        UIManager.put("OptionPane.messagebackground", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        int input = JOptionPane.showConfirmDialog(null, campaignFields,
                "New campaign", JOptionPane.OK_CANCEL_OPTION);

        if (input == JOptionPane.OK_OPTION) {
            String newName = campaignName.getText();
            String newDescription = campaignDescription.getText();
            LocalDateTime newStartDate = LocalDateTime.parse(startDate.getText(), formatter);
            LocalDateTime newEndDate = LocalDateTime.parse(endDate.getText(), formatter);
            Integer newVouchers = Integer.valueOf(vouchers.getText());
            CampaignStatusType newStatus;

            if (LocalDateTime.now().isAfter(newEndDate))
                newStatus = CampaignStatusType.EXPIRED;
            else
                newStatus = CampaignStatusType.STARTED;

            Campaign newCampaign = new Campaign(vms.campaigns.size() + 1, newName,
                    newDescription, newStartDate, newEndDate, newVouchers, newVouchers);
            vms.addCampaign(newCampaign);

            newCampaign.setCampaignStatus(newStatus);

            DefaultTableModel model = (DefaultTableModel) campaignTable.getModel();
            model.addRow(new Object[]{vms.campaigns.size(), newName, newStatus,
                    newStartDate.format(formatter), newEndDate.format(formatter)});

            addAddVoucherButton(vms.campaigns.size() - 1);
            addEditButton(vms.campaigns.size() - 1);
            addInfoButton(vms.campaigns.size() - 1);
            addCancelButton(vms.campaigns.size() - 1);

            adminPanel.updateUI();
        }
    }

    private void addAddVoucherButton(int i) throws IOException {
        JButton addVoucherButton = new JButton();
        String addVoucherPath = imagesPath + "add_voucher.png";

        BufferedImage addVoucher = ImageIO.read(new File(addVoucherPath));
        addVoucherButton.setIcon(new ImageIcon(addVoucher));
        addVoucherButton.setVisible(true);
        addVoucherButton.setPreferredSize(new Dimension(25, 25));
        addVoucherButton.setBackground(null);
        addVoucherButton.setBorder(null);
        addVoucherButton.setFocusable(false);

        springLayout.putConstraint(SpringLayout.NORTH, addVoucherButton, 140 + i * 30,
                SpringLayout.NORTH, adminPanel);
        springLayout.putConstraint(SpringLayout.WEST, addVoucherButton, 990,
                SpringLayout.WEST, adminPanel);

        addVoucherButton.addActionListener(e -> {
            if (vms.getCampaign(i + 1).getAvailableVouchers() != 0 &&
                    vms.getCampaign(i + 1).getCampaignStatus().equals(CampaignStatusType.
                            STARTED))
                addVoucherDialog(i);
            else {
                JLabel noVouchers = new JLabel("Campaign is unavailable!");
                noVouchers.setForeground(Color.RED);
                noVouchers.setVisible(true);
                springLayout.putConstraint(SpringLayout.NORTH, noVouchers, 600,
                        SpringLayout.NORTH, adminPanel);
                springLayout.putConstraint(SpringLayout.WEST, noVouchers, 515,
                        SpringLayout.WEST, adminPanel);
                adminPanel.add(noVouchers);
                adminPanel.updateUI();
            }
        });

        adminPanel.add(addVoucherButton);
    }

    private void addVoucherDialog(int i) {
        JPanel voucherFields = new JPanel();
        voucherFields.setLayout(springLayout);
        voucherFields.setBackground(Color.WHITE);

        Font font = new Font("Arial", Font.PLAIN, 15);

        JLabel informationLabel = new JLabel("Insert voucher details:");
        informationLabel.setFont(new Font("Arial", Font.PLAIN, 17));

        JRadioButton giftVoucher = new JRadioButton("Gift Voucher");
        JRadioButton loyaltyVoucher = new JRadioButton("Loyalty Voucher");

        JLabel valueLabel = new JLabel("Value: ");
        JTextField value = new JTextField(20);
        JLabel emailLabel = new JLabel("Send to: ");
        JTextField email = new JTextField(20);

        valueLabel.setFont(font);
        emailLabel.setFont(font);

        giftVoucher.setBackground(null);
        loyaltyVoucher.setBackground(null);

        giftVoucher.setFont(font);
        loyaltyVoucher.setFont(font);

        springLayout.putConstraint(SpringLayout.NORTH, informationLabel, 4,
                SpringLayout.NORTH, voucherFields);
        springLayout.putConstraint(SpringLayout.WEST, informationLabel, 23,
                SpringLayout.WEST, voucherFields);

        springLayout.putConstraint(SpringLayout.NORTH, giftVoucher, 50,
                SpringLayout.NORTH, voucherFields);
        springLayout.putConstraint(SpringLayout.WEST, giftVoucher, 10,
                SpringLayout.WEST, voucherFields);

        springLayout.putConstraint(SpringLayout.NORTH, loyaltyVoucher, 50,
                SpringLayout.NORTH, voucherFields);
        springLayout.putConstraint(SpringLayout.WEST, loyaltyVoucher, 150,
                SpringLayout.WEST, voucherFields);

        voucherFields.add(informationLabel);
        voucherFields.add(giftVoucher);
        voucherFields.add(loyaltyVoucher);

        springLayout.putConstraint(SpringLayout.NORTH, valueLabel, 90,
                SpringLayout.NORTH, voucherFields);
        springLayout.putConstraint(SpringLayout.WEST, valueLabel, 0,
                SpringLayout.WEST, voucherFields);

        springLayout.putConstraint(SpringLayout.NORTH, emailLabel, 120,
                SpringLayout.NORTH, voucherFields);
        springLayout.putConstraint(SpringLayout.WEST, emailLabel, 0,
                SpringLayout.WEST, voucherFields);

        voucherFields.add(valueLabel);
        voucherFields.add(emailLabel);

        springLayout.putConstraint(SpringLayout.NORTH, value, 90,
                SpringLayout.NORTH, voucherFields);
        springLayout.putConstraint(SpringLayout.WEST, value, 95,
                SpringLayout.WEST, voucherFields);

        springLayout.putConstraint(SpringLayout.NORTH, email, 120,
                SpringLayout.NORTH, voucherFields);
        springLayout.putConstraint(SpringLayout.WEST, email, 95,
                SpringLayout.WEST, voucherFields);

        voucherFields.add(email);
        voucherFields.add(value);

        UIManager.put("OptionPane.minimumSize", new Dimension(400, 250));
        UIManager.put("OptionPane.background", Color.WHITE);

        UIManager.put("OptionPane.messagebackground", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        int input = JOptionPane.showConfirmDialog(null, voucherFields,
                "New voucher", JOptionPane.OK_CANCEL_OPTION);

        Voucher newVoucher;

        if (input == JOptionPane.OK_OPTION) {
            Campaign currentCampaign = vms.getCampaign(i + 1);
            String userEmail = email.getText().toUpperCase();
            int voucherValue = Integer.parseInt(value.getText());

            if (giftVoucher.isSelected()) {
                newVoucher = currentCampaign.generateVoucher(userEmail, "GiftVoucher",
                        voucherValue);
            } else {
                newVoucher = currentCampaign.generateVoucher(userEmail, "LoyaltyVoucher",
                        voucherValue);
            }

            newVoucher.setVoucherID(currentCampaign.campaignVouchers.size() + 1);
            User givenUser = vms.getUserByEmail(userEmail);

            currentCampaign.campaignVouchers.add(newVoucher);
            currentCampaign.campaignVoucherMap.addVoucher(newVoucher);

            givenUser.userVoucherMap.addVoucher(newVoucher);
            givenUser.userVouchers.add(newVoucher);

            if (!currentCampaign.getObservers().contains(givenUser))
                currentCampaign.addObserver(givenUser);

            currentCampaign.setAvailableVouchers(currentCampaign.getAvailableVouchers() - 1);
        }
    }

    private void addEditButton(int i) throws IOException {
        JButton editButton = new JButton();
        String editPath = imagesPath + "edit.png";

        BufferedImage edit = ImageIO.read(new File(editPath));
        editButton.setIcon(new ImageIcon(edit));
        editButton.setVisible(true);
        editButton.setPreferredSize(new Dimension(25, 25));
        editButton.setBackground(null);
        editButton.setBorder(null);
        editButton.setFocusable(false);

        springLayout.putConstraint(SpringLayout.NORTH, editButton, 140 + i * 30,
                SpringLayout.NORTH, adminPanel);
        springLayout.putConstraint(SpringLayout.WEST, editButton, 1015,
                SpringLayout.WEST, adminPanel);

        editButton.addActionListener(e -> editCampaignDialog(i));

        adminPanel.add(editButton);
    }

    private void editCampaignDialog(int i) {
        JPanel campaignFields = new JPanel();
        campaignFields.setLayout(springLayout);
        campaignFields.setBackground(Color.WHITE);

        Font font = new Font("Arial", Font.PLAIN, 15);

        JLabel informationLabel = new JLabel("Update campaign details:");
        informationLabel.setFont(new Font("Arial", Font.PLAIN, 17));

        JLabel nameLabel = new JLabel("Name: ");
        JTextField campaignName = new JTextField(20);
        JLabel descriptionLabel = new JLabel("Description: ");
        JTextField campaignDescription = new JTextField(20);
        JLabel startDateLabel = new JLabel("Start date: ");
        JTextField startDate = new JTextField(20);
        JLabel endDateLabel = new JLabel("End date: ");
        JTextField endDate = new JTextField(20);
        JLabel vouchersLabel = new JLabel("Vouchers nr: ");
        JTextField vouchers = new JTextField(20);

        nameLabel.setFont(font);
        descriptionLabel.setFont(font);
        startDateLabel.setFont(font);
        endDateLabel.setFont(font);
        vouchersLabel.setFont(font);

        springLayout.putConstraint(SpringLayout.NORTH, informationLabel, 4,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, informationLabel, 23,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, nameLabel, 50,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, nameLabel, 0,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, descriptionLabel, 80,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, descriptionLabel, 0,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, startDateLabel, 110,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, startDateLabel, 0,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, endDateLabel, 140,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, endDateLabel, 0,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, vouchersLabel, 170,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, vouchersLabel, 0,
                SpringLayout.WEST, campaignFields);

        campaignFields.add(informationLabel);
        campaignFields.add(nameLabel);
        campaignFields.add(descriptionLabel);
        campaignFields.add(startDateLabel);
        campaignFields.add(endDateLabel);
        campaignFields.add(vouchersLabel);

        springLayout.putConstraint(SpringLayout.NORTH, campaignName, 50,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, campaignName, 95,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, campaignDescription, 80,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, campaignDescription, 95,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, startDate, 110,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, startDate, 95,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, endDate, 140,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, endDate, 95,
                SpringLayout.WEST, campaignFields);

        springLayout.putConstraint(SpringLayout.NORTH, vouchers, 170,
                SpringLayout.NORTH, campaignFields);
        springLayout.putConstraint(SpringLayout.WEST, vouchers, 95,
                SpringLayout.WEST, campaignFields);

        campaignFields.add(campaignName);
        campaignFields.add(campaignDescription);
        campaignFields.add(startDate);
        campaignFields.add(endDate);
        campaignFields.add(vouchers);

        UIManager.put("OptionPane.minimumSize", new Dimension(400, 280));
        UIManager.put("OptionPane.background", Color.WHITE);

        UIManager.put("OptionPane.messagebackground", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        int input = JOptionPane.showConfirmDialog(null, campaignFields,
                "New campaign", JOptionPane.OK_CANCEL_OPTION);

        if (input == JOptionPane.OK_OPTION) {
            String newName = campaignName.getText();
            String newDescription = campaignDescription.getText();
            LocalDateTime newStartDate = LocalDateTime.parse(startDate.getText(), formatter);
            LocalDateTime newEndDate = LocalDateTime.parse(endDate.getText(), formatter);
            Integer newVouchers = Integer.valueOf(vouchers.getText());
            CampaignStatusType newStatus;

            if (LocalDateTime.now().isAfter(newEndDate))
                newStatus = CampaignStatusType.EXPIRED;
            else
                newStatus = CampaignStatusType.STARTED;

            Campaign newCampaign = new Campaign(vms.campaigns.size() + 1, newName,
                    newDescription, newStartDate, newEndDate, newVouchers, newVouchers);

            vms.updateCampaign(i + 1, newCampaign);

            vms.getCampaign(i + 1).setCampaignStatus(newStatus);

            DefaultTableModel model = (DefaultTableModel) campaignTable.getModel();

            model.setValueAt(newName, i, 1);
            model.setValueAt(newStatus, i, 2);
            model.setValueAt(newStartDate.format(formatter), i, 3);
            model.setValueAt(newEndDate.format(formatter), i, 4);

            Notification notification = new Notification(i + 1, NotificationType.EDIT,
                    LocalDateTime.now());
            vms.getCampaign(i + 1).notifyAllObservers(notification);
        }
    }

    private void addInfoButton(int i) throws IOException {
        JButton infoButton = new JButton();
        String infoPath = imagesPath + "info.png";

        BufferedImage info = ImageIO.read(new File(infoPath));
        infoButton.setIcon(new ImageIcon(info));
        infoButton.setVisible(true);
        infoButton.setPreferredSize(new Dimension(25, 25));
        infoButton.setBackground(null);
        infoButton.setBorder(null);
        infoButton.setFocusable(false);

        springLayout.putConstraint(SpringLayout.NORTH, infoButton, 140 + i * 30,
                SpringLayout.NORTH, adminPanel);
        springLayout.putConstraint(SpringLayout.WEST, infoButton, 1040,
                SpringLayout.WEST, adminPanel);

        infoButton.addActionListener(e -> addInfoDialog(i));

        adminPanel.add(infoButton);
    }

    private void addInfoDialog(int campaignID) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(springLayout);
        infoPanel.setBackground(Color.WHITE);

        Campaign currentCampaign = vms.getCampaign(campaignID + 1);

        Font font1 = new Font("Arial", Font.BOLD, 15);
        Font font2 = new Font("Arial", Font.PLAIN, 15);

        JLabel nameLabel = new JLabel("Name:");
        JLabel name = new JLabel(currentCampaign.getCampaignName());
        JLabel descriptionLabel = new JLabel("Description:");
        JLabel description = new JLabel(currentCampaign.getCampaignDescription());
        JLabel startLabel = new JLabel("Start date:");
        JLabel startDate = new JLabel(currentCampaign.getStartDate().format(formatter));
        JLabel endLabel = new JLabel("End date:");
        JLabel endDate = new JLabel(currentCampaign.getEndDate().format(formatter));
        JLabel totalVouchersLabel = new JLabel("Total vouchers:");
        JLabel totalVouchers = new JLabel(String.valueOf(currentCampaign.getDistributableVouchers()));
        JLabel availableVouchersLabel = new JLabel("Available vouchers:");
        JLabel availableVouchers = new JLabel(String.valueOf(currentCampaign.getAvailableVouchers()));

        nameLabel.setFont(font1);
        descriptionLabel.setFont(font1);
        startLabel.setFont(font1);
        endLabel.setFont(font1);
        totalVouchersLabel.setFont(font1);
        availableVouchersLabel.setFont(font1);

        name.setFont(font2);
        description.setFont(font2);
        startDate.setFont(font2);
        endDate.setFont(font2);
        totalVouchers.setFont(font2);
        availableVouchers.setFont(font2);

        springLayout.putConstraint(SpringLayout.NORTH, nameLabel, 20,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, nameLabel, 0,
                SpringLayout.WEST, infoPanel);

        springLayout.putConstraint(SpringLayout.NORTH, descriptionLabel, 20,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, descriptionLabel, 240,
                SpringLayout.WEST, infoPanel);

        springLayout.putConstraint(SpringLayout.NORTH, startLabel, 50,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, startLabel, 0,
                SpringLayout.WEST, infoPanel);

        springLayout.putConstraint(SpringLayout.NORTH, endLabel, 50,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, endLabel, 240,
                SpringLayout.WEST, infoPanel);

        springLayout.putConstraint(SpringLayout.NORTH, totalVouchersLabel, 80,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, totalVouchersLabel, 0,
                SpringLayout.WEST, infoPanel);

        springLayout.putConstraint(SpringLayout.NORTH, availableVouchersLabel, 80,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, availableVouchersLabel, 240,
                SpringLayout.WEST, infoPanel);

        springLayout.putConstraint(SpringLayout.NORTH, name, 20,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, name, 60,
                SpringLayout.WEST, infoPanel);

        springLayout.putConstraint(SpringLayout.NORTH, description, 20,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, description, 340,
                SpringLayout.WEST, infoPanel);

        springLayout.putConstraint(SpringLayout.NORTH, startDate, 50,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, startDate, 90,
                SpringLayout.WEST, infoPanel);

        springLayout.putConstraint(SpringLayout.NORTH, endDate, 50,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, endDate, 330,
                SpringLayout.WEST, infoPanel);

        springLayout.putConstraint(SpringLayout.NORTH, totalVouchers, 80,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, totalVouchers, 125,
                SpringLayout.WEST, infoPanel);

        springLayout.putConstraint(SpringLayout.NORTH, availableVouchers, 80,
                SpringLayout.NORTH, infoPanel);
        springLayout.putConstraint(SpringLayout.WEST, availableVouchers, 400,
                SpringLayout.WEST, infoPanel);

        infoPanel.add(name);
        infoPanel.add(nameLabel);
        infoPanel.add(description);
        infoPanel.add(descriptionLabel);
        infoPanel.add(startDate);
        infoPanel.add(startLabel);
        infoPanel.add(endDate);
        infoPanel.add(endLabel);
        infoPanel.add(totalVouchers);
        infoPanel.add(totalVouchersLabel);
        infoPanel.add(availableVouchers);
        infoPanel.add(availableVouchersLabel);

        UIManager.put("OptionPane.minimumSize", new Dimension(500, 200));
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("OptionPane.messagebackground", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        JOptionPane.showConfirmDialog(null, infoPanel,
                "  Campaign info", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    private void addCancelButton(int i) throws IOException {
        JButton cancelButton = new JButton();
        String cancelPath = imagesPath + "cancel.png";

        BufferedImage cancel = ImageIO.read(new File(cancelPath));
        cancelButton.setIcon(new ImageIcon(cancel));
        cancelButton.setVisible(true);
        cancelButton.setPreferredSize(new Dimension(25, 25));
        cancelButton.setBackground(null);
        cancelButton.setBorder(null);
        cancelButton.setFocusable(false);

        springLayout.putConstraint(SpringLayout.NORTH, cancelButton, 140 + i * 30,
                SpringLayout.NORTH, adminPanel);
        springLayout.putConstraint(SpringLayout.WEST, cancelButton, 1065,
                SpringLayout.WEST, adminPanel);

        cancelButton.addActionListener(e -> cancelCampaignDialog(i));

        adminPanel.add(cancelButton);
    }

    private void cancelCampaignDialog(int i) {
        JPanel cancelPanel = new JPanel();
        cancelPanel.setLayout(springLayout);
        cancelPanel.setBackground(Color.WHITE);
        Campaign currentCampaign = vms.getCampaign(i + 1);

        JLabel informationLabel1 = new JLabel("Campaign " + currentCampaign.getCampaignName() +
                " will be canceled.");
        informationLabel1.setFont(new Font("Arial", Font.PLAIN, 17));
        JLabel informationLabel2 = new JLabel("Are you sure you want to continue?");
        informationLabel2.setFont(new Font("Arial", Font.PLAIN, 17));

        UIManager.put("OptionPane.minimumSize", new Dimension(400, 110));
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("OptionPane.messagebackground", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);

        springLayout.putConstraint(SpringLayout.NORTH, informationLabel1, 3,
                SpringLayout.NORTH, cancelPanel);
        springLayout.putConstraint(SpringLayout.WEST, informationLabel1, 0,
                SpringLayout.WEST, cancelPanel);

        springLayout.putConstraint(SpringLayout.NORTH, informationLabel2, 27,
                SpringLayout.NORTH, cancelPanel);
        springLayout.putConstraint(SpringLayout.WEST, informationLabel2, 20,
                SpringLayout.WEST, cancelPanel);

        cancelPanel.add(informationLabel1);
        cancelPanel.add(informationLabel2);

        int input = JOptionPane.showConfirmDialog(null, cancelPanel,
                "  Cancel campaign", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (input == JOptionPane.OK_OPTION) {
            vms.cancelCampaign(i + 1);

            Notification notification = new Notification(i + 1, NotificationType.CANCEL,
                    LocalDateTime.now());
            currentCampaign.notifyAllObservers(notification);

            campaignTable.getModel().setValueAt(CampaignStatusType.CANCELLED, i, 2);
        }
    }
}
