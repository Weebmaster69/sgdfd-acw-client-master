/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util.generacion;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;

/**
 *
 * @author antony.almonacid
 */
public class ConvertDocxToPdf {
    static Logger log = LogManager.getLogger(ConvertDocxToPdf.class.getName());
    private ActiveXComponent app = null;
	private Dispatch documents = null;

	public ConvertDocxToPdf() {
		try {
			inicializar();
		} catch (java.lang.UnsatisfiedLinkError e) {
			throw new RuntimeException("Ocurrio un error al generar el documento");
		}
	}

	public final void inicializar() {
		if (app == null) {
			try {
				ComThread.InitMTA(true);

				app = new ActiveXComponent("Word.Application");

				documents = app.getProperty("Documents").toDispatch();
			} catch (java.lang.UnsatisfiedLinkError | java.lang.NoClassDefFoundError e) {
				e.printStackTrace();
				log.error("Error al iniciar ConvertDocxToPdf");
				throw new RuntimeException("Ocurrio un error al generar el documento");
			}
		}
	}

	public void finalizar() {
		try {
			if (app != null) {
				app.invoke("Quit");
				ComThread.Release();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] convertDocxToPdf(byte[] docx) throws IOException {
		File docxFile = null;
		File pdfFileTmp = null;
		try {
			docxFile = File.createTempFile("src/main/resources/temp/tmp/doc", ".docx");
			pdfFileTmp = File.createTempFile("src/main/resources/temp/tmp/pdf", ".pdf");
			docxFile.deleteOnExit();
			pdfFileTmp.deleteOnExit();

			FileUtils.writeByteArrayToFile(docxFile, docx);

			Dispatch document = Dispatch.call(documents, "Open", docxFile.getAbsolutePath(), false, true).toDispatch();
			if (pdfFileTmp.exists()) {
				if(!pdfFileTmp.delete()){
					log.error("No se pudo borrar el archivo pdfFileTmp");
				}
			}
			Dispatch.call(document, "SaveAs", pdfFileTmp.getAbsolutePath(), 17);
			Dispatch.call(document, "Close", false);
			return FileUtils.readFileToByteArray(pdfFileTmp);
		} catch (com.jacob.com.ComFailException ex) {
			throw new RuntimeException("Versión incompatible de Office");
		} catch (Exception e) {
			throw new RuntimeException("Error al convertir en PDF");
		} finally {
			try {
				if (docxFile!= null && docxFile.exists()) {
					if(!docxFile.delete()){
						log.error("No se pudo borrar el archivo docxFile");
					}
				}
				if (pdfFileTmp!= null && pdfFileTmp.exists()) {
					if(!pdfFileTmp.delete()){
						log.error("No se pudo borrar el archivo pdfFileTmp");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    
}
