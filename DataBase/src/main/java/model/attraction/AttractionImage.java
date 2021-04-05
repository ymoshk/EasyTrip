package model.attraction;

import log.LogsManager;
import model.Model;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import util.google.Keys;

import javax.imageio.ImageIO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Entity
@Table(indexes = @Index(columnList = "placeId", unique = true, name = "placeId_Index"))
public class AttractionImage extends Model {
    @Column(length = 5 * 1024, nullable = false)
    private byte[] imageBytes;
    @Column(nullable = false)
    private String placeId;

    public AttractionImage() {
    }

    public AttractionImage(Attraction attraction) {
        this.placeId = attraction.getPlaceId();
        setImageBytes(extractImage(attraction.getPhotoReference()));
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public BufferedImage getImage() {
        BufferedImage res = null;
        InputStream stream = new ByteArrayInputStream(this.imageBytes);
        try {
            res = ImageIO.read(stream);
        } catch (IOException e) {
            LogsManager.logException(e);
        }

        return res;
    }

    public void setImageBytes(BufferedImage image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "JPG", out);
            this.imageBytes = out.toByteArray();
        } catch (IOException e) {
            LogsManager.logException(e);
        }
    }

    /**
     * @param photoReference reference that's received from the google API.
     * @return the image that was received from the given photo reference.
     */
    private BufferedImage extractImage(String photoReference) {
        String imageUrl = getPhotoUrl(photoReference);
        Request request = new Request.Builder().url(imageUrl).build();
        Response response;
        OkHttpClient client = new OkHttpClient();
        BufferedImage res = null;

        try {
            response = client.newCall(request).execute();
            res = extractImageFromResponse(response);
        } catch (IOException ignored) {
        }

        return res;
    }

    /**
     * @param response a http response that contains an image in it's body.
     * @return the image from the response as a BufferedImage.
     */
    private BufferedImage extractImageFromResponse(Response response) {
        BufferedImage result = null;

        if (response.body() != null) {
            // TODO - make sure the JavaFx is installed in the server.
            try {
                result = ImageIO.read(response.body().byteStream());
            } catch (IOException e) {
                LogsManager.logException(e);
            }
        }
        return result;
    }

    /**
     * @param photoReference reference that's received from the google API.
     * @return the real temporary URL of the desired photo.
     */
    private String getPhotoUrl(String photoReference) {
        String resultPhotoUrl = null;

        try {
            String reqUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" +
                    photoReference + "&key=" + Keys.getKey();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(reqUrl).build();
            Response response = client.newCall(request).execute();
            resultPhotoUrl = response.request().url().toString();
        } catch (Exception e) {
            LogsManager.logException(e);
        }

        return resultPhotoUrl;
    }
}
