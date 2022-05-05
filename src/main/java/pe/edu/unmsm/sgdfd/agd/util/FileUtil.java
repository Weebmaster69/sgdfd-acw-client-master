/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util;

import java.io.File;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author antony.almonacid
 */
public class FileUtil {
    static Logger log = LogManager.getLogger(FileUtil.class.getName());
    
    private FileUtil (){
        throw new IllegalStateException("FileUtility class");
    }
    public static void crearDirectorio(String rutaDirectorio) {
        File file = new File(rutaDirectorio);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    
    public static void eliminarDirectorio(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    eliminarDirectorio(f);
                }
            }
        }
        log.info(file.getAbsolutePath());
        if(!file.delete()){
            log.warn("No se pudo borrar el archivo file");
        }
    }
    
    public static void limpiarTemporales() {
	String ruta = System.getProperty("java.io.tmpdir") + "/scriptlet4docx";
    log.info("Borrando: " + ruta);
	FileUtil.eliminarDirectorio(new File(ruta));
    }
}
