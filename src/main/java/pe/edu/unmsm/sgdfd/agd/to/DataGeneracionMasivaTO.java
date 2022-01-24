/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author antony.almonacid
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataGeneracionMasivaTO {
    
    private Integer idPlantilla;
    private byte[] archivoPlantilla;
    private List<ImagenTO> lsImagenes;
    private Boolean generarDocx;
    private Boolean generarPdf;
    private List<DataParticipanteTO> registros;
    
}
