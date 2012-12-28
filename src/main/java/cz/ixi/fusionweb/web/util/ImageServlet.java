package cz.ixi.fusionweb.web.util;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.xml.messaging.saaj.util.ByteInputStream;

import cz.ixi.fusionweb.ejb.ProductBean;
import cz.ixi.fusionweb.entities.Product;

@WebServlet(urlPatterns = "/image/*")
public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ImageServlet.class.getCanonicalName());
    private static final int DEFAULT_BUFFER_SIZE = 10240;

    @EJB
    ProductBean productBean;

    public void init() throws ServletException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	// Get requested image by path info.
	String requestedImage = request.getParameter("id"); // request.getPathInfo();

	// Check if file name is actually supplied to the request URI.
	if (requestedImage == null) {
	    // Do your thing if the image is not supplied to the request URI.
	    // Throw an exception, or send 404, or show default/warning image,
	    // or just ignore it.
	    response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.

	    return;
	}

	Product p = productBean.find(Integer.parseInt(requestedImage));

	if ((p == null) || (p.getImgSrc() == null)) {
	    response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.

	    return;
	} else {
	    // Init servlet response.
	    response.reset();
	    response.setBufferSize(DEFAULT_BUFFER_SIZE);
	    // response.setContentType("image/jpg");
	    response.setHeader("Content-Length", String.valueOf(p.getImgSrc().length));
	    response.setHeader("Content-Disposition", "inline; filename=\"" + p.getName() + "\"");

	    // Prepare streams.
	    ByteInputStream byteInputStream = new ByteInputStream();
	    BufferedOutputStream output = null;

	    try {
		// Open streams.
		byteInputStream.setBuf(p.getImgSrc());
		output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

		// // Write file contents to response.
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int length;

		while ((length = byteInputStream.read(buffer)) > 0) {
		    output.write(buffer, 0, length);
		}
	    } finally {
		// Gently close streams.
		close(output);
		close(byteInputStream);
	    }
	}
    }

    private static void close(Closeable resource) {
	if (resource != null) {
	    try {
		resource.close();
	    } catch (IOException e) {
		logger.severe("Problems during image resource manipulation. " + e.getMessage());
	    }
	}
    }
}
