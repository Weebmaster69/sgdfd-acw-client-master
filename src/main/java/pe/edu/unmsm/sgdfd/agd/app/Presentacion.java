/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.app;

import com.google.gson.Gson;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

import pe.edu.unmsm.sgdfd.agd.to.DataGeneracionMasivaTO;
import pe.edu.unmsm.sgdfd.agd.to.DocumentoTO;
import pe.edu.unmsm.sgdfd.agd.to.SolicitudGuardarDocumentoTO;
import pe.edu.unmsm.sgdfd.agd.to.SolicitudMasivaDocumentoTO;
import pe.edu.unmsm.sgdfd.agd.util.conexion.ConexionHttpClient;
import pe.edu.unmsm.sgdfd.agd.util.generacion.GeneradorDocumento;
import pe.edu.unmsm.sgdfd.agd.view.GeneradorGUIView;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pe.edu.unmsm.sgdfd.agd.Servidor;
import pe.edu.unmsm.sgdfd.agd.util.server.ServidorHttp;
import com.threerings.getdown.util.LaunchUtil;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.AWTException;
import java.awt.Image;

/**
 *
 * @author antony.almonacid
 */
class JustOneLock {
    private String appName;
  
    FileLock lock;
  
    FileChannel channel;
  
    public JustOneLock(String appName) {
      this.appName = appName;
    }
  
    public boolean isAppActive() throws Exception{
      File file = new File(System.getProperty("user.home"), appName + ".tmp");
      channel = new RandomAccessFile(file, "rw").getChannel();
  
      lock = channel.tryLock();
      if (lock == null) {
        lock.release();
        channel.close();
        return true;
      }
      Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          try {
            lock.release();
            channel.close();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
      return false;
    }
  }
public class Presentacion {
    
    static Logger log = LogManager.getLogger(Presentacion.class.getName());
    private static Image loadImage(String name) {
        URL url = Presentacion.class.getClassLoader().getResource(name);
        return Toolkit.getDefaultToolkit().getImage(url);
    }
    public static void main(String[] args) throws Exception{
        JustOneLock ua = new JustOneLock("JustOneId");
        if (ua.isAppActive()) {
        System.out.println("Already active.");
        System.exit(1);
        } else {
        System.out.println("NOT already active.");
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
        }
        
        
        String path = System.getProperty("user.dir");
        System.out.println(path + "/jacob-1.18-x64.dll");
        System.setProperty("jacob.dll.path", path + "/jacob-1.18-x64.dll" );
        
        

        GeneradorGUIView view = new GeneradorGUIView();
        view.setVisible(false);
        new Servidor(5050).start();



        if (SystemTray.isSupported() == true) {
            view.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        }
        SystemTray systemTray = SystemTray.getSystemTray();
        
        Image image = loadImage("images/myappsmall.png");
        // URL url = Presentacion.class.getResource("myappsmall.png");
        // Image image = Toolkit.getDefaultToolkit().getImage("src\\main\\java\\pe\\edu\\unmsm\\sgdfd\\agd\\app\\myappsmall.png");


        // Image image = Toolkit.getDefaultToolkit().getImage(url);
        TrayIcon trayIcon = new TrayIcon( image, "ACW");
        trayIcon.setImageAutoSize(true);
        PopupMenu popMenu = new PopupMenu();
        MenuItem show = new MenuItem("Show");
        show.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.setVisible(true);

            }
        });
        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);

            }

        });
        popMenu.add(show);
        popMenu.add(exit);

        trayIcon.setPopupMenu(popMenu);
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

}