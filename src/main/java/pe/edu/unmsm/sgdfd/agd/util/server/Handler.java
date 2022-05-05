/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
/**
 *
 * @author antony.almonacid
 */
public class Handler implements HttpHandler{
    static Logger log = LogManager.getLogger(Handler.class.getName());
    public void handle(HttpExchange exchange) throws IOException {
        log.info("CONEXION ESTABLECIDA");
        //Envío de respuesta a la petición solicitada
        String response = "Operacion finalizada con exito";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    
    }
}
