package model;

public class flightOffer {
    private boolean oneWay;
    private Price price;

    public class Price{
        private String currency;
        private double total;

        protected Price() {
        }

        public String getCurrency() {
            return currency;
        }

        public double getTotal() {
            return total;
        }
    }

//    public class Segment{
//        private
//    }

    public class Itinerary{
        private String duration;

    }
}
