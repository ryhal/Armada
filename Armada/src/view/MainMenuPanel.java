package view;

import av.visual.ImageLoader;
import game.ApplicationManager;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.*;
import java.io.*;
import game.PreloadImageList;
import av.visual.ImageLoader;

import javax.imageio.ImageIO;

/**
A class representing the main menu of the system.
*/
public class MainMenuPanel extends JPanel implements ActionListener{
    private boolean launchingGame = false;
    ApplicationManager am;
    JButton startButton = new JButton("Start Game Old Way");
    JButton startButton2 = new JButton("Single Player");
    JButton exitButton = new JButton("Quit Game");
    JButton networkedGameHostButton = new JButton("New Multiplayer Host");
    JButton networkedGameClientButton = new JButton("Connect to Multiplayer Host");
    private String message = "";
    
    BufferedImage backgroundImage;
    static final String IMAGE_NAME = "ArmadaBackground";
    
    public MainMenuPanel(ApplicationManager am) {
        this.am = am;
        
        // Setup the buttons
        startButton.addActionListener(this);
        exitButton.addActionListener(this);
        startButton2.addActionListener(this);
        networkedGameHostButton.addActionListener(this);
        networkedGameClientButton.addActionListener(this);
        JPanel buttonsPanel = new JPanel(new GridLayout(4,1));
        buttonsPanel.setPreferredSize(new Dimension(50, 500));
        this.setLayout(new BorderLayout());
        buttonsPanel.add(startButton2);
        buttonsPanel.add(networkedGameHostButton);
        buttonsPanel.add(networkedGameClientButton);
        buttonsPanel.add(exitButton);
        //buttonsPanel.add(startButton);
        buttonsPanel.setBorder(new EmptyBorder(100,400,250,10));
        buttonsPanel.setOpaque(false);
        this.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Load the background image
        File f = new File(IMAGE_NAME+ ".jpg");
        
        backgroundImage = ImageLoader.getInstance().getImage(IMAGE_NAME+".jpg");
        if (backgroundImage == null) System.out.println("Failed to load main menu image");
        /*
        backgroundImage = loadImage(f);
        if (backgroundImage == null) {
            backgroundImage = loadImage(new File("src/"+IMAGE_NAME+".jpg"));
        }
        */
    }
    
    // Respond to button clicks here.
    public void actionPerformed(ActionEvent evt) {
        if (launchingGame) return;
        if (evt.getSource() == startButton) {
            
                am.startGame();
        } else if (evt.getSource() == startButton2) {
            launchingGame = true;
            message = "Loading Images...";
            startButton2.setLabel(message);
            Thread resourceLoaderThread = new Thread(new ResourceLoader());
            resourceLoaderThread.start();
            
        } else if (evt.getSource() == networkedGameHostButton) {
            am.startNetworkedGame();
        } else if (evt.getSource() == networkedGameClientButton) {
            am.connectToNetworkedGame();
        } else if (evt.getSource() == exitButton) {
            System.exit(0);
        }
    }
    
    // Draws the background image.
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform at = g2d.getTransform();
        if (backgroundImage != null) {
            int imageWidth = backgroundImage.getWidth();
            int panelWidth = this.getWidth();
            
            double scale = panelWidth / (imageWidth * 1.0);
            g2d.scale(scale, scale);
            g2d.drawImage(backgroundImage, 0, 0, null);
            
        } else {
            System.out.println("Repainting");
            //repaint();
        }
        g2d.setTransform(at);
    }
    
    private static BufferedImage loadImage(File f) {
        BufferedImage bi = null;
        
        /*
        try {
            bi = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println("Failed to load main menu image");
        }
        */
        return bi;
        
    }
    
    private class ResourceLoader implements Runnable {
        public void run() {
        String[] list = PreloadImageList.getList();
            BufferedImage img = null;
            for (int i = 0; i < list.length; i++) {
            
                System.out.println("Loading image: " + (i+1) + " of " + list.length);
                ImageLoader.getInstance().preloadImageAsync(list[i]);
                
                boolean quit = false;
                
                int count = 0;
                while(!quit) {
                    count++;
                    if (count > 29) {
                        System.out.println("Preloading an image timed out.");
                        quit = true;
                    }
                    try {
                        Thread.sleep(10);
                        if (ImageLoader.getInstance().imageIsLoaded(list[i])) quit = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        quit = true;
                    }
                }
            }
            am.startGameNewWay(); 
        }
         
    }
}