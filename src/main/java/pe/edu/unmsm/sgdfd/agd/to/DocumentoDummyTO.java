/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentoDummyTO {
    
    private Integer idDocumento;
    private String idLocal;
    private String nombreLocal;
    private Integer idPlantilla;
    private String nombrePlantilla;
    private byte[] archivo;
    private Boolean tieneArchivo;
    private String usuario;
    private String token;
    
}
