/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.app;

import com.google.gson.Gson;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.StringEscapeUtils;
import pe.edu.unmsm.sgdfd.agd.Servidor;
import pe.edu.unmsm.sgdfd.agd.util.server.ServidorHttp;
/**
 *
 * @author antony.almonacid
 */
public class Presentacion {
    
     public static void main(String[] args) throws IOException {
        
        GeneradorGUIView view = new GeneradorGUIView();
        view.setVisible(true);
        new Servidor(5050).start();
        
     }
     
}
