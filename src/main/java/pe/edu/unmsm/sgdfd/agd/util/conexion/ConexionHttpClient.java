/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util.conexion;

import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pe.edu.unmsm.sgdfd.agd.to.DataGeneracionMasivaTO;
import pe.edu.unmsm.sgdfd.agd.to.SolicitudGuardarDocumentoTO;
import pe.edu.unmsm.sgdfd.agd.to.SolicitudMasivaDocumentoTO;

/**
 *
 * @author antony.almonacid
 */
public class ConexionHttpClient {
    
    static Logger log = LogManager.getLogger(ConexionHttpClient.class.getName());
    
    private static HttpComponentsClientHttpRequestFactory hcchrf = new HttpComponentsClientHttpRequestFactory();
    protected static RestTemplate restTemplate = new RestTemplate(hcchrf);
    protected static RestTemplate restTemplateSinTimeOut = new RestTemplate();

    protected static String url;
    private static String MENSAJE_LOG = "Error al realizar consulta " + url;
    protected static final String MENSAJE = "Error conectar al módulo generador de documentos";
    
    public static void inicializar() {
        hcchrf.setConnectionRequestTimeout(60000); // Este es el tiempo de espera en milis para obtener la conexión de connectionManager 6000
        hcchrf.setConnectTimeout(70000); // Este es el tiempo de espera en milisegundos para establecer la conexión entre el origen y el destino 7000
        hcchrf.setReadTimeout(100000);// Este es el tiempo de espera en milisegundos para establecer la conexión entre el origen y el destino 10000
    }
    
    public DataGeneracionMasivaTO recopilarData(SolicitudMasivaDocumentoTO solicitud, WebSocket websocket){
       
         try {
             
            HttpHeaders headers = new HttpHeaders();
            // set `content-type` header
            headers.setContentType(MediaType.APPLICATION_JSON);
		
            // set `accept` header
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.add("Authorization", "Bearer " + solicitud.getToken());
	        //headers.set("Authorization", "Bearer "+solicitud.getToken());
            // create request
            HttpEntity<?> request = new HttpEntity<>(solicitud,headers);
                
            // DataGeneracionMasivaTO response = restTemplate.postForObject("http://localhost:8081/sgdfd/mcc/backend" + "/documentos/preprocesar/generacion", request, DataGeneracionMasivaTO.class);
            //DataGeneracionMasivaTO response = restTemplate.postForObject("http://desarrollocp.unmsm.edu.pe:8080/sgdfd/mcc/backend" + "/documentos/preprocesar/generacion", request, DataGeneracionMasivaTO.class);
            return restTemplate.postForObject("https://servicioonline.unmsm.edu.pe/sgdfd/mcc/backend" + "/documentos/preprocesar/generacion", request, DataGeneracionMasivaTO.class);
        } catch (ResourceAccessException | HttpClientErrorException | HttpServerErrorException e) {
            log.fatal(MENSAJE_LOG, e);
            websocket.send("Error: "+ "No se pudo recopilar la información necesaria para la generación de los documentos, por favor consulte con su administrador de base de datos.");
            throw new RuntimeException(MENSAJE + "\n");
        }
         
    }
    
    public void guardarDocumentos(SolicitudGuardarDocumentoTO solicitud, WebSocket websocket){
        try {
            
            HttpHeaders headers = new HttpHeaders();
            // set `content-type` header
            headers.setContentType(MediaType.APPLICATION_JSON);
		
            // set `accept` header
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            headers.add("Authorization", "Bearer " + solicitud.getToken());
            //headers.set("Authorization", "Bearer "+solicitud.getToken());
            // create request
            HttpEntity<?> request = new HttpEntity<>(solicitud,headers);
                
            //restTemplate.postForObject("http://localhost:8081/sgdfd/mcc/backend" + "/documentos/guardar", request, SolicitudGuardarDocumentoTO.class);
            //restTemplate.postForObject("http://desarrollocp.unmsm.edu.pe:8080/sgdfd/mcc/backend" + "/documentos/guardar", request, SolicitudGuardarDocumentoTO.class);
            restTemplate.postForObject("https://servicioonline.unmsm.edu.pe/sgdfd/mcc/backend" + "/documentos/guardar", request, SolicitudGuardarDocumentoTO.class);
            
        } catch (ResourceAccessException | HttpClientErrorException | HttpServerErrorException e) {
            log.fatal(MENSAJE_LOG, e);
            websocket.send("Error: "+ "No se pudo subir los documentos generados, por favor consulte con su administrador de base de datos.");
            throw new RuntimeException(MENSAJE + "\n");
        }
    }
    
}