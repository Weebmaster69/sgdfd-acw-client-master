/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util;

import java.io.File;
import java.nio.file.Files;
/**
 *
 * @author antony.almonacid
 */
public class FileUtil {
    
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
        file.delete();
    }
    
    public static void limpiarTemporales() {
	String ruta = System.getProperty("java.io.tmpdir") + "/scriptlet4docx";
	System.out.println("Borrando: " + ruta);
	FileUtil.eliminarDirectorio(new File(ruta));
        //FileUtil.eliminarDirectorio(new File(System.getProperty("java.io.tmpdir") + "tmp"));
    }
}
