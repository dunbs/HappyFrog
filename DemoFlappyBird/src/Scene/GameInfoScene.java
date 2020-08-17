/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scene;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author conqu
 */
public class GameInfoScene extends JPanel {

    public GameInfoScene(Dimension dimension) {
        this.setSize(dimension);
        this.setLayout(new CardLayout());
        
        Point centerScreen = new Point(0, 0);
        ImageIcon icon = new ImageIcon(getClass().getResource("/image/START_GAME.png"));
        JLabel gameName = new JLabel(icon);
        gameName.setBounds(new Rectangle(centerScreen, this.getSize()));
        this.add(gameName);
    }

}
