package cz.cvut.fel.kinclma1;

import cz.cvut.fel.kinclma1.player.MusicStringPlayer;
import cz.cvut.fel.kinclma1.player.PlayerListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Created with IntelliJ IDEA.
 * User: void
 * Date: 31.1.13
 * Time: 18:18
 * To change this template use File | Settings | File Templates.
 */
public class TestListener1 extends JFrame implements PlayerListener {

    private MusicStringPlayer player;

    private JSlider slider;
    private JLabel label;
    private JButton play;

    public TestListener1() {
        setSize(300, 300);
        setLayout(new FlowLayout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                player.close();
            }
        });
    }

    @Override
    public void setPlayer(MusicStringPlayer player) {
        this.player = player;
    }

    @Override
    public void setSongLength(int length) {
        slider = new JSlider(0, length, 0);
        add(slider);
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                player.setPosition(slider.getValue());
            }
        });
        label = new JLabel();
        add(label);
        play = new JButton(">");
        add(play);
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (play.getText().equals("||")) {
                    player.pause();
                } else {
                    player.play();
                }
            }
        });
        play.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (play.getText().equals(">")) {
                    player.pause();
                } else {
                    player.play();
                }
            }
        });
        setVisible(true);
    }

    @Override
    public void setPlaying(boolean playing) {
        play.setText(playing ? "||" : ">");
    }

    @Override
    public void setPosition(int position) {
        slider.setValue(position);
        label.setText(String.valueOf(position));
    }

    public void close() {
        this.dispose();
    }
}
