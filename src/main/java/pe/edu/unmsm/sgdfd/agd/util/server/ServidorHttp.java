/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.StringEscapeUtils;
/**
 *
 * @author antony.almonacid
 */
public class ServidorHttp {
    
    public void arrancar() throws IOException{
        
        //InetSocketAddress addr = new InetSocketAddress("localhost",8080);
        //InetSocketAddress addr = new InetSocketAddress("172.16.242.19",8080);
        
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            InetSocketAddress addr = new InetSocketAddress(ip.getHostAddress(),8080);
            HttpServer server = HttpServer.create(addr, 0);

            server.createContext("/test", new Handler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            System.out.println("Server is listening on port: " + "8080" + " and ip: "+ ip.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
        
    }
    
    
    
}
