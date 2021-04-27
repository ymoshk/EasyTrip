package application;

import connection.DataEngine;

public class Application {

    public static void main(String[] args) throws Exception {
        System.out.println("Application is starting...");
        DataEngine data = new DataEngine();
        data.getCity("");
        data.close();

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

