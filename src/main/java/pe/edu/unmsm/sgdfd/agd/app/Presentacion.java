/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.app;

import javax.swing.WindowConstants;

import pe.edu.unmsm.sgdfd.agd.view.GeneradorGUIView;

import java.io.File;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pe.edu.unmsm.sgdfd.agd.Servidor;
import com.threerings.getdown.util.LaunchUtil;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Toolkit;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.AWTException;
import java.awt.Image;

/**
 *
 * @author antony.almonacid
 */
    
public class Presentacion {
    
    static Logger log = LogManager.getLogger(Presentacion.class.getName());
    private static Image loadImage(String name) {
        URL url = Presentacion.class.getClassLoader().getResource(name);
        return Toolkit.getDefaultToolkit().getImage(url);
    }
    public static void main(String[] args) throws Exception{
        JustOneLock ua = new JustOneLock("JustOneId");
        if (ua.isAppActive()) {
            log.info("Esta activo el programa");
        System.exit(1);
        } else {
            log.info("No esta activo el programa");
        }
        
        if (args.length > 0) {
            final File appdir = new File(args[0]);
            new Thread() {
                @Override
                public void run() {
                    LaunchUtil.upgradeGetdown(new File(appdir, "getdown-old.jar"),
                                                new File(appdir, "getdown.jar"),
                                                new File(appdir, "getdown-new.jar"));
                }
            }.start();
        }
        
        String path = System.getProperty("user.dir");
        System.setProperty("jacob.dll.path", path + "/jacob-1.18-x64.dll" );

        GeneradorGUIView view = new GeneradorGUIView();
        view.setVisible(false);
        new Servidor(5050).start();

        if (SystemTray.isSupported()) {
            view.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        }
        SystemTray systemTray = SystemTray.getSystemTray();
        
        Image image = loadImage("images/myappsmall.png");

        TrayIcon trayIcon = new TrayIcon( image, "ACW");
        trayIcon.setImageAutoSize(true);
        PopupMenu popMenu = new PopupMenu();
        MenuItem show = new MenuItem("Show");
        show.addActionListener(e -> view.setVisible(true));

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));

        popMenu.add(show);
        popMenu.add(exit);
        trayIcon.setPopupMenu(popMenu);
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }

}