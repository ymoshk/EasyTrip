package application;

import com.google.maps.model.PlaceType;
import connection.DataEngine;
import container.PriceRange;
import model.attraction.Attraction;
import model.attraction.AttractionImage;
import model.attraction.restaurant.Restaurant;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;


public class Application {

    public static void main(String[] args) throws Exception {
        System.out.println("Application is starting...");
//        DataEngine data = new DataEngine();
//
//        List<Attraction> attractionList = data.getAttractions(PlaceType.RESTAURANT, "RAMAT GAN", new PriceRange());
//
//        Restaurant restaurant = (Restaurant) attractionList.get(0);
//        AttractionImage image = data.getAttractionImage(restaurant);
//        BufferedImage im = image.getImage();
//        File outputfile = new File("image.jpg");
//        ImageIO.write(im, "jpg", outputfile);
//        data.close();
    }
}

