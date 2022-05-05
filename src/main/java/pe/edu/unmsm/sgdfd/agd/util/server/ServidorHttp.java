/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util.server;

import com.sun.net.httpserver.HttpServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
/**
 *
 * @author antony.almonacid
 */
public class ServidorHttp {
    static Logger log = LogManager.getLogger(ServidorHttp.class.getName());

    public void arrancar() throws IOException{
        
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            InetSocketAddress addr = new InetSocketAddress(ip.getHostAddress(),8080);
            HttpServer server = HttpServer.create(addr, 0);

            server.createContext("/test", new Handler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            log.info("Server is listening on port: 8080 and ip: "+ ip.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
        
    }
    
    
    
}
