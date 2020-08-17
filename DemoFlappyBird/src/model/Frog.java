/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author conqu
 */
public class Frog extends JLabel {

    private final int fallSpeed = 1;
    private final int jumpSpeed = 10;

    public int getFallSpeed() {
        return fallSpeed;
    }

    public int getJumpSpeed() {
        return jumpSpeed;
    }

    private ImageIcon scaleImage(ImageIcon icon, int w, int h) {
        int nw = icon.getIconWidth();
        int nh = icon.getIconHeight();

        if (icon.getIconWidth() > w) {
            nw = w;
            nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
        }

        if (nh > h) {
            nh = h;
            nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
        }

        return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_DEFAULT));
    }

    public Frog() {

        ImageIcon icon = new ImageIcon(getClass().getResource("/image/frog_crop.png"));
        this.setIcon(scaleImage(icon, 779 * 15 / 100, 471 * 15 / 100));
    }

    public Rectangle getCollider() {
        Rectangle bound = super.getBounds();

        int width = bound.width / 3 * 2;
        int height = bound.height / 3 * 2;

        bound.setSize(width, height);
        bound.setLocation(bound.x + 10, bound.y + 10);

        return bound;
    }

    @Override
    public String toString() {
        return this.getX() + "," + this.getY();
    }
}
