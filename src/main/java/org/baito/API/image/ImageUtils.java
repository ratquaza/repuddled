package org.baito.API.image;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    private static File imageFolder = new File("C:/Users/Admin/Documents/Repuddled/IMAGES");

    public static BufferedImage getImage(String name) {
        File f = new File(imageFolder + "/" + name);
        try {
            return f.exists() ? ImageIO.read(f) : null;
        } catch (IOException e) {

        }
        return null;
    }

    public static BufferedImage getImageOutside(String location) {
        File f = new File("C:/Users/Admin/Documents/Repuddled/" + location);
        try {
            return f.exists() ? ImageIO.read(f) : null;
        } catch (IOException e) {

        }
        return null;
    }

    public static BufferedImage draw(BufferedImage base, BufferedImage added, int x, ImageOffset xOffset, int y, ImageOffset yOffset) {
        Graphics2D g = (Graphics2D) base.getGraphics();
        if (added != null) {
            x -= xOffset == ImageOffset.HALF ? added.getWidth() / 2 : xOffset == ImageOffset.FULL ? added.getWidth() : 0;
            y -= yOffset == ImageOffset.HALF ? added.getHeight() / 2 : yOffset == ImageOffset.FULL ? added.getHeight() : 0;
            g.drawImage(added, x, y, null);
        }
        g.dispose();
        return base;
    }

    public static BufferedImage resizeConstrain(BufferedImage base, double widthMult, double heightMult) {
        int newX = (int)(base.getWidth() * widthMult);
        int newY = (int)(base.getHeight() * heightMult);
        BufferedImage dimg = new BufferedImage(newX, newY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(base, 0, 0, newX, newY, null);
        g2d.dispose();
        return dimg;
    }

    public static BufferedImage drawText(BufferedImage img, Font f, Color color, String text, int x, int y) {
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setColor(color);
        g.setFont(f);
        g.drawString(text, x, y);
        g.dispose();
        return img;
    }

    public static void sendImage(MessageChannel channel, BufferedImage image, String format, String name) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ImageIO.write(image, format, bytes);
            channel.sendFile(bytes.toByteArray(), name + "." + format).queue();
            bytes.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void embedImage(MessageChannel channel, BufferedImage image, EmbedBuilder builder, boolean thumbnail, String name, String format) {
        if (image != null) {
            try {
                if (thumbnail) {
                    builder.setThumbnail("attachment://" + name + "." + format);
                } else {
                    builder.setImage("attachment://" + name + "." + format);
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                try {
                    ImageIO.write(image, format, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                channel.sendFile(bytes.toByteArray(), name + "." + format).embed(builder.build()).queue();
                bytes.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            channel.sendMessage(builder.build()).queue();
        }
    }

}
