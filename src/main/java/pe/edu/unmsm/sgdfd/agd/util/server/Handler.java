/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pe.edu.unmsm.sgdfd.agd.app.Presentacion;
import pe.edu.unmsm.sgdfd.agd.to.DataGeneracionMasivaTO;
import pe.edu.unmsm.sgdfd.agd.to.DocumentoTO;
import pe.edu.unmsm.sgdfd.agd.to.SolicitudGuardarDocumentoTO;
import pe.edu.unmsm.sgdfd.agd.to.SolicitudMasivaDocumentoTO;
import pe.edu.unmsm.sgdfd.agd.util.conexion.ConexionHttpClient;
import pe.edu.unmsm.sgdfd.agd.util.generacion.GeneradorDocumento;
import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 *
 * @author antony.almonacid
 */
public class Handler implements HttpHandler{
    
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("CONEXION ESTABLECIDA");
        /*
        //Recepción de parámetros enviados en el requestbody
        StringBuilder sb = new StringBuilder();
        InputStream ios = exchange.getRequestBody();
        int i;
        while ((i = ios.read()) != -1) {
            sb.append((char) i);
        }
    
        //Conversión string to object SolicitudMasivaDocumentoTO
        Gson g = new Gson();
        SolicitudMasivaDocumentoTO solicitud = g.fromJson(sb.toString(), SolicitudMasivaDocumentoTO.class);
        System.out.println(solicitud);
        
        //Lógica de negocio asociada a la generación de documentos
        ConexionHttpClient conexion = new ConexionHttpClient();
        DataGeneracionMasivaTO data = conexion.recopilarData(solicitud);
        
        GeneradorDocumento generador = new GeneradorDocumento();
         try {
             List<DocumentoTO> documentos = generador.generacionMasivaDocumentos(data);
             conexion.guardarDocumentos(SolicitudGuardarDocumentoTO.builder()
                     .documentos(documentos)
                     .modo("OPERACION_MGD")
                     .usuario(solicitud.getUsuario())
                     .build());
             //System.exit(0);
         } catch (Exception ex) {
             System.out.println(ex.getMessage());
             Logger.getLogger(Presentacion.class.getName()).log(Level.SEVERE, null, ex);
         }
        */
        
        //Envío de respuesta a la petición solicitada
        String response = "Operacion finalizada con exito";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    
  }
}
