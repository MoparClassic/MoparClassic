package org.moparscape.msc.gs.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;

import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.util.Logger;

public class Captcha {

    private Font fonts[];
    private Random random;
    
    public byte[] generateCaptcha(Player p) {
        BufferedImage image = new BufferedImage(255, 40, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 255, 40);
        g.setColor(Color.white);
        String word = getWord();
        p.setSleepword(word);
        int lastx = 5;
        for(int i = 0; i < word.length(); i++) {
            Font font = fonts[random.nextInt(fonts.length)];
            char chr = (char)(int)(word.charAt(i) - 32);
            g.setFont(font);
            g.drawString(String.valueOf(chr), lastx, 35);
            lastx += g.getFontMetrics().charWidth(chr) + 5;
        }
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte bytes[] = null;
        try {
            ImageIO.write(image, "png", baos);
            bytes = baos.toByteArray();
        } catch(IOException ioe) {
            Logger.error("Error generating sleep word: " + ioe.getMessage());
            ioe.printStackTrace();
        }
        return bytes;
    }
    
    public void init() {
        random = new Random();
        loadFonts();
        Logger.println(fonts.length + " fonts loaded");
    }
    
    public void loadFonts() {
        fonts = new Font[4];
        try {
            fonts[0] = Font.createFont(Font.TRUETYPE_FONT, new File("conf/fonts", "Harakiri.ttf")).deriveFont(30f);
            fonts[1] = Font.createFont(Font.TRUETYPE_FONT, new File("conf/fonts", "IntellectaBodoned Trash.ttf")).deriveFont(30f);
            fonts[2] = Font.createFont(Font.TRUETYPE_FONT, new File("conf/fonts", "MiseryLovesCompanyDEMO.ttf")).deriveFont(20f);
            fonts[3] = Font.createFont(Font.TRUETYPE_FONT, new File("conf/fonts", "ADDSBP__.TTF")).deriveFont(20f);
        } catch(Exception e) {
            Logger.error("Font loading error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public String getWord() {
        try {
            int rowCount = count(new File("conf", "dict.txt"));
            int row = random.nextInt(rowCount);
            String ret = "null";
            int count = 0;
            BufferedReader in = new BufferedReader(new FileReader(new File("conf", "dict.txt")));
            String line;
            while((line = in.readLine()) != null)
                if((count++) == row)
                    ret = line;
            in.close();
            return ret;
        } catch(IOException ioe) {
            Logger.error("Unable to read dictionary!");
            ioe.printStackTrace();
        }
        return "null";
    }
    
    public int count(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
            }
            return count;
        } finally {
            is.close();
        }
    }
}
