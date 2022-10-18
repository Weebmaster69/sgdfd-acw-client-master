/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util.generacion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.scriptlet4docx.docx.DocxTemplater;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
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
        List<ImagenTO> lsImagenesLocal = new ArrayList<>();
        lsImagenesLocal = data.getLsImagenes();
        int total = 0;
        
        try {
            total = data.getRegistros().size();
        
            FileUtil.crearDirectorio(RUTA_ORIGEN);
            for(int i=0; i<data.getRegistros().size();i++) {
                
                try { //borrando archivos temporales de generaciones anteriores
                    FileUtil.eliminarDirectorio(new File(System.getProperty("java.io.tmpdir") + "scriptlet4docx"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                websocket.send("Generando documento " + (i+1) + " de "+ total);
                
                DataParticipanteTO generadorDocxRequest = data.getRegistros().get(i);
                //Generar plantilla word solo con imágenes
                isDocx = new ByteArrayInputStream(data.getArchivoPlantilla());
                
                //Agregar QR a la lista de imágenes comunes
	    	    lsImagenesLocal.add(generadorDocxRequest.getCodigoQR());

		        //Agregar duplicado de QR adicional a la lista de imágenes comunes
                if(generadorDocxRequest.getPropiedad().isRenderizar()) {     
                    lsImagenesLocal.add(ImagenTO.builder()
                                        .imagen(generadorDocxRequest.getCodigoQR().getImagen())
                                        .alto(generadorDocxRequest.getPropiedad().getAlto())
                                        .ancho(generadorDocxRequest.getPropiedad().getAncho())
                                        .numeroPagina(generadorDocxRequest.getPropiedad().getNumeroPagina())
                                        .x(generadorDocxRequest.getPropiedad().getX())
                                        .y(generadorDocxRequest.getPropiedad().getY())
                                        .build());
            }		

            osDocx = InsertarImagen.insertarImagen(isDocx,lsImagenesLocal);

            lsImagenesLocal.remove(lsImagenesLocal.size()-1);	
		
		    //Remover QR duplicado de lista de imágenes comunes
            if(generadorDocxRequest.getPropiedad().isRenderizar()) {
                lsImagenesLocal.remove(lsImagenesLocal.size()-1);
            }
                
            //Guardar plantilla word temporalmente
            docInStream = new ByteArrayInputStream(osDocx.toByteArray());
            Document outDoc = new Document(docInStream);
            outDoc.save("C:\\MCC_TMP\\prueba.docx");
		
        
            //Leer plantilla word
            Document inDoc = new Document("C:\\MCC_TMP\\prueba.docx");
            docOutStream = new ByteArrayOutputStream();
            inDoc.save(docOutStream, SaveFormat.DOCX);
            
            //Generar plantilla word con parametros dinámicos
            docxTemplater = new DocxTemplater(new ByteArrayInputStream(docOutStream.toByteArray()),data.getIdPlantilla().toString());
            isDocx = docxTemplater.processAndReturnInputStream(generadorDocxRequest.getParametros());//isDocx2
            
            //Remover marca de agua de documento generado
            docxByteArray = InsertarImagen.removeMarca(IOUtils.toByteArray(isDocx)).toByteArray();//isDocx2
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
                    // websocket.send("Documento " + (i+1) + " de "+ total + " generado con éxito." );
                    
        }
        return documentos;
        } catch (Exception e) {
            e.printStackTrace();
            websocket.send(e.toString());
            throw e;
        } finally {	
            FileUtil.limpiarTemporales();
			
            if(osDocx != null) {
                osDocx.close();// muy importante no dejar abierto los stream
            }
			
            if(isDocx != null) {
                isDocx.close();// muy importante no dejar abierto los stream
            }
        }
        
    }
    
}