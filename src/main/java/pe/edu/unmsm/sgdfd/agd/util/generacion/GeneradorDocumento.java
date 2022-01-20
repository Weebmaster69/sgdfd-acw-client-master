/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util.generacion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;
import org.scriptlet4docx.docx.DocxTemplater;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.java_websocket.WebSocket;
import pe.edu.unmsm.sgdfd.agd.to.DataGeneracionMasivaTO;
import pe.edu.unmsm.sgdfd.agd.to.DataParticipanteTO;
import pe.edu.unmsm.sgdfd.agd.to.DocumentoTO;
import pe.edu.unmsm.sgdfd.agd.to.ImagenTO;
import pe.edu.unmsm.sgdfd.agd.util.FileUtil;
/**
 *
 * @author antony.almonacid
 */
public class GeneradorDocumento {
    
    private DocxTemplater docxTemplater;
    private byte[] docxByteArray;
    private byte[] pdfByteArray;
    private static final String RUTA_ORIGEN = "C:\\MCC_TMP";
    
    public List<DocumentoTO> generacionMasivaDocumentos(DataGeneracionMasivaTO data, WebSocket websocket) throws Exception{
        List<DocumentoTO> documentos = new ArrayList<DocumentoTO>();
    	ConvertDocxToPdf convertDocxToPdf = null;
        InputStream isDocx = null;
	ByteArrayOutputStream osDocx = null;
        
        InputStream docInStream = null;
	ByteArrayOutputStream docOutStream = null;
        int total = 0;
        
        try {
            total = data.getRegistros().size();
            
	    for(int i=0; i<data.getRegistros().size();i++) {
                
                websocket.send("Generando documento " + (i+1) + " de "+ total);
                
                DataParticipanteTO generadorDocxRequest = data.getRegistros().get(i);
                docxTemplater = new DocxTemplater(new ByteArrayInputStream(data.getArchivoPlantilla()),data.getIdPlantilla().toString());
		
	    	//Función para generar docx
                isDocx = docxTemplater.processAndReturnInputStream(generadorDocxRequest.getParametros());
	    		
	    	//Agregar QR a la lista de imágenes comunes
	    	data.getLsImagenes().add(generadorDocxRequest.getCodigoQR());	
	    		
                //Agregar duplicado de QR adicional a la lista de imágenes comunes
                if(generadorDocxRequest.getPropiedad().isRenderizar()) {     
                    data.getLsImagenes().add(ImagenTO.builder()
                                    .imagen(generadorDocxRequest.getCodigoQR().getImagen())
                                    .alto(generadorDocxRequest.getPropiedad().getAlto())
                                    .ancho(generadorDocxRequest.getPropiedad().getAncho())
                                    .numeroPagina(generadorDocxRequest.getPropiedad().getNumeroPagina())
                                    .x(generadorDocxRequest.getPropiedad().getX())
                                    .y(generadorDocxRequest.getPropiedad().getY())
                                    .build());
	    	}
                
	    	//Función para agregar imágenes
                osDocx = InsertarImagen.insertarImagen(isDocx,data.getLsImagenes());
	    		
                //Remover QR de lista de imágenes comunes
	    	data.getLsImagenes().remove(data.getLsImagenes().size()-1);
	    	
                //Remover QR duplicado de lista de imágenes comunes
                if(generadorDocxRequest.getPropiedad().isRenderizar()) {data.getLsImagenes().remove(data.getLsImagenes().size()-1);};
                
	    	docxByteArray = osDocx.toByteArray();
	    	convertDocxToPdf = new ConvertDocxToPdf();
                
	    	if (data.getGenerarPdf()) {
                    //Conversión docx - pdf
                    pdfByteArray = convertDocxToPdf.convertDocxToPdf(docxByteArray);
                    convertDocxToPdf.finalizar();
	    	}
	    			
		documentos.add(DocumentoTO.builder()
						.generarDocx(data.getGenerarDocx())
						.generarPdf(data.getGenerarPdf())
						.archivoDocx(generadorDocxRequest.getGenerarDocx()?docxByteArray:null)
						.archivoPdf(generadorDocxRequest.getGenerarPdf()?pdfByteArray:null)
						.numeroDocumento(generadorDocxRequest.getParametros().get("NUMERO_DOCUMENTO").toString())
                                                .codigoVerificacion(generadorDocxRequest.getParametros().get("CODIGO_VERIFICACION").toString())
						.build());
                //websocket.send("Documento " + (i+1) + " de "+ total + " generado con éxito." );
                
	    }
	    
	    return documentos;
    	
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
	} finally {	
            FileUtil.limpiarTemporales();
	
            /*if(convertDocxToPdf != null) {
		convertDocxToPdf.finalizar();
            }*/
			
            if(osDocx != null) {
		osDocx.close();// muy importante no dejar abierto los stream
            }
			
            if(isDocx != null) {
                isDocx.close();// muy importante no dejar abierto los stream
            }
	}
        
    }
    
}
