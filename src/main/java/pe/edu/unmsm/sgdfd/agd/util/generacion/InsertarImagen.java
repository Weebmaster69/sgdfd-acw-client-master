/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.edu.unmsm.sgdfd.agd.util.generacion;

import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.LayoutCollector;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.PageSetup;
import com.aspose.words.Paragraph;
import com.aspose.words.SaveFormat;
import com.aspose.words.Shape;
import com.aspose.words.WrapType;
import pe.edu.unmsm.sgdfd.agd.to.ImagenTO;

/**
 *
 * @author antony.almonacid
 */
public class InsertarImagen {

    private InsertarImagen (){
        throw new IllegalStateException("Insertar Imagen Utility class");
    }
    public static ByteArrayOutputStream insertarImagen(InputStream isDocx, List<ImagenTO> lsImagenes) throws Exception {
        Document doc = new Document(isDocx);
        DocumentBuilder builder = new DocumentBuilder(doc);
        for (var img : lsImagenes) {
            if (img.getImagen() != null) {

                LayoutCollector collector = new LayoutCollector(doc);
                Paragraph anchorPara = null;
                NodeCollection<?> paragraphs = doc.getChildNodes(NodeType.PARAGRAPH, true);

                for (Object para : paragraphs) {
                    if (collector.getStartPageIndex((Paragraph) para) == img.getNumeroPagina()) {
                        anchorPara = (Paragraph) para;
                        break;
                    }
                }
                PageSetup ps = builder.getPageSetup();
                builder.moveToParagraph(paragraphs.indexOf(anchorPara), 0);

                Shape shape = builder.insertImage(img.getImagen());
                shape.setWidth(img.getAncho()); // ancho
                shape.setHeight(img.getAlto());// alto
                shape.setWrapType(WrapType.NONE);// detras del texto
                shape.setLeft(img.getX() + 1 - ps.getLeftMargin()); // posicion x
                shape.setTop(img.getY() - 160 - ps.getTopMargin()); // posicion y
            }
        }

        ByteArrayOutputStream osDocx = new ByteArrayOutputStream();
        builder.getDocument().save(osDocx, SaveFormat.DOCX);
        osDocx.close();
        return removeMarca(osDocx.toByteArray());
    }

    public static ByteArrayOutputStream removeMarca(byte[] docx) throws IOException {
        XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(docx));
        int pNumber = doc.getParagraphs().size() - 1;
        while (pNumber >= 0) {
            XWPFParagraph p = doc.getParagraphs().get(pNumber);
            if (p.getParagraphText().contains("Evaluation Only")) {
                deleteParagraph(p);
                break;
            }
            pNumber--;
        }

        ByteArrayOutputStream osDocx = new ByteArrayOutputStream();
        doc.write(osDocx);
        doc.close();
        return osDocx;
    }

    private static void deleteParagraph(XWPFParagraph p) {
        XWPFDocument doc = p.getDocument();
        int pPos = doc.getPosOfParagraph(p);
        doc.removeBodyElement(pPos);
    }

}
