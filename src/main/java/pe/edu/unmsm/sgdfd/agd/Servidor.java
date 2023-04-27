/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd;

import com.google.gson.Gson;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.enterprise.context.ApplicationScoped;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import pe.edu.unmsm.sgdfd.agd.to.DataGeneracionMasivaTO;
import pe.edu.unmsm.sgdfd.agd.to.DocumentoDummyTO;
import pe.edu.unmsm.sgdfd.agd.to.DocumentoTO;
import pe.edu.unmsm.sgdfd.agd.to.SolicitudGuardarDocumentoTO;
import pe.edu.unmsm.sgdfd.agd.to.SolicitudMasivaDocumentoTO;
import pe.edu.unmsm.sgdfd.agd.util.conexion.ConexionHttpClient;
import pe.edu.unmsm.sgdfd.agd.util.generacion.GeneradorDocumento;

/**
 *
 * @author usuario
 */
//@ApplicationScoped
//@ServerEndpoint("/progress")
public class Servidor extends WebSocketServer{

    public Servidor(int puerto){
        super(new InetSocketAddress(puerto));
        System.out.println("Recibiendo peticiones en el puerto " + puerto);
    }
    
    @Override
    public void onOpen(WebSocket websocket, ClientHandshake arg1) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        websocket.send("Conexión establecida con éxito.");
        System.out.println("Se ha iniciado una nueva conexion");
    }

    @Override
    public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onMessage(WebSocket websocket, String mensaje) {
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //Conversión de String to SolicitudMasivaDocumentoTO
        Gson g = new Gson();
        SolicitudMasivaDocumentoTO solicitud = g.fromJson(mensaje, SolicitudMasivaDocumentoTO.class);
        
        //Invoca orquestador de servicios web - protocolo http
        ConexionHttpClient conexion = new ConexionHttpClient();
        DataGeneracionMasivaTO data = new DataGeneracionMasivaTO();
        websocket.send("Recopilando datos para la generación.....");
        
        if(solicitud.getIdPlantilla() != null){
            //Recopilar data necesaria para la generación de documento dummy
            data = conexion.recopilarDataDummy(SolicitudMasivaDocumentoTO.builder()
                .generarDocx(false)
                .generarPdf(true)
                .idPlantilla(solicitud.getIdPlantilla())
                .idLocal(solicitud.getIdLocal())
                .usuario(solicitud.getUsuario())
                .token(solicitud.getToken())
                .build(), websocket);
        }else{
            //Recopilar data necesaria para la generación de documentos
            data = conexion.recopilarData(SolicitudMasivaDocumentoTO.builder()
                .generarDocx(false)
                .generarPdf(true)
                .idEventoEjecucion(solicitud.getIdEventoEjecucion())
                .idTipoParticipante(solicitud.getIdTipoParticipante())
                .participantes(solicitud.getParticipantes())
                .usuario(solicitud.getUsuario())
                .token(solicitud.getToken())
                .build(), websocket);
        }
       
        websocket.send("Iniciando generación de documentos.....");
        //Proceso de generación de documentos
        GeneradorDocumento generador = new GeneradorDocumento();
        try {
            List<DocumentoTO> documentos = generador.generacionMasivaDocumentos(data,websocket);
            if(solicitud.getIdPlantilla() != null){
                websocket.send("Subiendo documento dummy.....");
                DocumentoDummyTO docDummy = conexion.guardarDocumentosDummy(DocumentoDummyTO.builder()
                                            .idDocumento(null)
                                            .archivo(documentos.get(0).getArchivoPdf())
                                            .idPlantilla(solicitud.getIdPlantilla())
                                            .idLocal(solicitud.getIdLocal())
                                            .usuario(solicitud.getUsuario())
                                            .token(solicitud.getToken())
                                            .build(), websocket);
                websocket.send("Documento Nº "+docDummy.getIdDocumento()+" listo para su previsualizacion.");
                
            }else{
                websocket.send("Subiendo documentos.....");
                conexion.guardarDocumentos(SolicitudGuardarDocumentoTO.builder()
                     .documentos(documentos)
                     .modo("OPERACION_MGD")
                     .usuario(solicitud.getUsuario())
                     .token(solicitud.getToken())
                     .build(), websocket);
                websocket.send("Proceso finalizado con éxito.");
            }
            
            
        } catch (Exception ex) {
             websocket.send("Error: "+ ex.getMessage());
             System.out.println(ex.getMessage());     
        }        
    }

    @Override
    public void onError(WebSocket arg0, Exception arg1) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println(arg1.getMessage());
    }

    
}
