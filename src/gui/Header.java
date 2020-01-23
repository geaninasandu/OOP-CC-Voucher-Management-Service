package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class Header {

    JPanel header;
    JLabel logoLabel;
    private Path imagesDirectory = Paths.get("");
    private String imagesPath = imagesDirectory.toAbsolutePath().toString() + "\\images\\";
    private SpringLayout springLayout = new SpringLayout();

    Header() throws IOException {
        header = new JPanel();
        header.setPreferredSize(new Dimension(1250, 170));
        header.setBackground(Color.WHITE);
        header.setVisible(true);
        header.setLayout(springLayout);

        addLogo();
    }

    private void addLogo() throws IOException {
        String logoPath = imagesPath + "logo.png";
        BufferedImage logo = ImageIO.read(new File(logoPath));
        logoLabel = new JLabel(new ImageIcon(logo));


        springLayout.putConstraint(SpringLayout.NORTH, logoLabel, 36,
                SpringLayout.NORTH, header);
        springLayout.putConstraint(SpringLayout.WEST, logoLabel, 70,
                SpringLayout.WEST, header);

        header.add(logoLabel);
    }
}
