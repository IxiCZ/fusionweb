package cz.ixi.fusionweb.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import cz.ixi.fusionweb.ejb.ProductBean;
import cz.ixi.fusionweb.entities.Product;

@WebServlet(urlPatterns = "/fileUpload")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final List<String> EXTENSIONS_ALLOWED = new ArrayList<String>();

    static {
        // images only
        EXTENSIONS_ALLOWED.add(".jpg");
        EXTENSIONS_ALLOWED.add(".bmp");
        EXTENSIONS_ALLOWED.add(".png");
        EXTENSIONS_ALLOWED.add(".gif");
    }
    Product product;
    @EJB
    ProductBean productBean;

    public FileUploadServlet() {
        super();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        InputStream is = null;

        try {
            if ((request.getParameter("productID") != null) && (request.getParts().size() > 0)) {
                for (Part part : request.getParts()) {

                    is = request.getPart(part.getName()).getInputStream();

                    int i = is.available();
                    byte[] b = new byte[i];
                    is.read(b);

                    String fileName = getFileName(part);

                    Integer id = Integer.parseInt(request.getParameter("productID"));

                    product = productBean.find(id);

                    // generate *unique* filename 
                    final String extension = fileName.substring(fileName.length() - 4);

                    if (!EXTENSIONS_ALLOWED.contains(extension)) {
                        response.sendRedirect("administrator/product/Create.xhtml?errMsg=Wrong extension");

                        return;
                    }

                    product.setImgSrc(b);
                    product.setImg(fileName);
                    productBean.edit(product);

                    response.sendRedirect("administrator/product/Confirm.xhtml");
                }
            } else {
                // no img case
                response.sendRedirect("administrator/product/Confirm.xhtml");
            }

            // very generic error threatment - just sample
        } catch (NumberFormatException nfe) {
            response.sendRedirect("administrator/product/Create.xhtml?errMsg=Error to process product information");
        } catch (IOException ioe) {
            response.sendRedirect("administrator/product/Create.xhtml?errMsg=Error during file upload");
        } catch (StringIndexOutOfBoundsException soe) {
            response.sendRedirect("administrator/product/Confirm.xhtml?errMsg=Proceeding without an image");
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }

        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
