package servlet.data.provider;

import com.google.gson.Gson;
import connection.DataEngine;
import constant.Constants;
import model.attraction.AttractionImage;
import util.Utils;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.HashMap;

@WebServlet("/api/getAttractionImage")
public class GetAttractionImage extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HashMap<String, String> reqData = Utils.parsePostData(req);

        DataEngine dataEngine = (DataEngine) req.getServletContext().getAttribute(Constants.DATA_ENGINE);
        model.attraction.Attraction attractionModel = dataEngine.getAttractionById(reqData.get("id")).orElse(null);

        if (attractionModel != null) {
            AttractionImage image = dataEngine.getAttractionImage(attractionModel);
            Gson gson = new Gson();
            ImageContainer container = new ImageContainer(image, true, attractionModel.getPhotoReference());

            try (PrintWriter out = resp.getWriter()) {
                out.println(gson.toJson(container));
            }
        } else {
            resp.setStatus(500);
        }
    }

    public static class ImageContainer {
        public String base64;
        public int height;
        public int width;

        public ImageContainer(AttractionImage image, boolean attachImage, String photoRef) {
            this.height = image.getImage().getHeight();
            this.width = image.getImage().getWidth();
            this.base64 = photoRef;

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (attachImage) {
                try {
                    ImageIO.write(image.getImage(), "jpg", byteArrayOutputStream);
                    //                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    this.base64 = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
                } catch (IOException ignore) {
                }
            }
        }
    }

}