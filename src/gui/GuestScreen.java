package gui;

import campaigns.Campaign;
import noGUI.VMS;
import users.User;
import vouchers.Voucher;
import vouchers.VoucherStatusType;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class GuestScreen {
    JPanel guestPanel;
    private VMS vms;
    private JTable campaignTable, vouchersTable;
    private SpringLayout springLayout = new SpringLayout();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    GuestScreen(VMS vms, User user) {
        this.vms = vms;

        initGuestPanel();
        guestPanel.setLayout(springLayout);

        addTable(user);
    }

    private void initGuestPanel() {
        guestPanel = new JPanel();
        guestPanel.setBackground(Color.decode("#f6f5fa"));
        guestPanel.setPreferredSize(new Dimension(1200, 670));
        guestPanel.setVisible(true);
    }

    private void addTable(User guest) {
        ArrayList<Campaign> campaigns = new ArrayList<>();
        for (Campaign campaign : vms.getCampaigns()) {
            if (campaign.observers.contains(guest))
                campaigns.add(campaign);
        }

        int campaignNumber = campaigns.size();

        Object[][] campaignData = new Object[campaignNumber][7];

        for (int i = 0; i < campaignNumber; i++) {
            Campaign currentCampaign = campaigns.get(i);

            AdminScreen.setCampaignData(campaignData, i, currentCampaign, formatter);
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

        springLayout.putConstraint(SpringLayout.NORTH, jScrollPane, 70,
                SpringLayout.NORTH, guestPanel);
        springLayout.putConstraint(SpringLayout.WEST, jScrollPane, 235,
                SpringLayout.WEST, guestPanel);

        campaignTable.getSelectionModel().addListSelectionListener(e ->
                showVouchersPanel(campaignTable.getSelectedRow(), guest));

        campaignTable.setVisible(true);
        guestPanel.add(jScrollPane);
    }

    private void showVouchersPanel(int row, User guest) {
        JPanel vouchersPanel = new JPanel();
        vouchersPanel.setBackground(Color.WHITE);
        vouchersPanel.setLayout(springLayout);

        Integer campaignID = Integer.valueOf((String) campaignTable.getModel().getValueAt(row, 0));

        ArrayList<Voucher> vouchers = new ArrayList<>(guest.userVoucherMap.userVoucherMap.
                get(campaignID));

        int vouchersNumber = vouchers.size();

        Object[][] voucherData = new Object[vouchersNumber][6];

        for (int i = 0; i < vouchersNumber; i++) {
            voucherData[i][0] = vouchers.get(i).getVoucherID();
            voucherData[i][1] = vouchers.get(i).getVoucherCode();
            voucherData[i][2] = vouchers.get(i).getUserEmail();
            voucherData[i][3] = vouchers.get(i).getVoucherStatus();
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

        vouchersTable.getSelectionModel().addListSelectionListener(e -> markVoucherUsed(row,
                vouchersTable.getSelectedRow()));

        UIManager.put("OptionPane.minimumSize", new Dimension(600, 600));
        UIManager.put("OptionPane.messagebackground", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        JOptionPane.showConfirmDialog(null, vouchersPanel,
                "  Vouchers", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
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

}
