/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util.generacion;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 *
 * @author antony.almonacid
 */
public class ConvertDocxToPdf {
    
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
				System.out.println("Error al iniciar ConvertDocxToPdf");
				throw new RuntimeException("Ocurrio un error al generar el documento");
			}
		}
	}

	public void finalizar() {
		try {
			if (app != null) {
				app.invoke("Quit", new Variant[] {});
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
			//System.out.println("=====================================");
			docxFile.deleteOnExit();
			String docxPath = docxFile.getAbsolutePath();
			pdfFileTmp.deleteOnExit();

			FileUtils.writeByteArrayToFile(docxFile, docx);

			//long start = System.currentTimeMillis();

			Dispatch document = Dispatch.call(documents, "Open", docxFile.getAbsolutePath(), false, true).toDispatch();
			if (pdfFileTmp.exists()) {
				pdfFileTmp.delete();
			}
			//	System.out.println("rutaPDF=" + pdfFileTmp.getAbsolutePath());
			Dispatch.call(document, "SaveAs", pdfFileTmp.getAbsolutePath(), 17);
			Dispatch.call(document, "Close", false);
			//long end = System.currentTimeMillis();
			//System.out.println("Convertido docxFilePath en " + (end - start) + "ms");
			return FileUtils.readFileToByteArray(pdfFileTmp);
		} catch (com.jacob.com.ComFailException ex) {
			throw new RuntimeException("Versión incompatible de Office");
		} catch (Exception e) {
			throw new RuntimeException("Error al convertir en PDF");
		} finally {
			try {
				//System.out.println("Borrando: " + docxFile.getAbsolutePath());
				//System.out.println("Borrando: " + pdfFileTmp.getAbsolutePath());
				if (docxFile!= null && docxFile.exists()) {
					docxFile.delete();
				}
				if (pdfFileTmp!= null && pdfFileTmp.exists()) {
					pdfFileTmp.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    
}
