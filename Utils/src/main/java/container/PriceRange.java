package container;

import com.google.maps.model.PriceLevel;

import java.io.Serializable;


public class PriceRange implements Serializable {
    private static final long serialVersionUID = -8267770336855182514L;

    private final PriceLevel min;
    private final PriceLevel max;

    public PriceRange() {
        this.min = PriceLevel.FREE;
        this.max = PriceLevel.VERY_EXPENSIVE;
    }

    public PriceRange(int number) {
        this.min = PriceLevel.FREE;
        this.max = PriceLevel.values()[number];
    }

    public PriceRange(PriceLevel min, PriceLevel max) {

        if (min == null) {
            this.min = PriceLevel.FREE;
        } else {
            this.min = min;
        }

        if (max == null) {
            this.max = PriceLevel.VERY_EXPENSIVE;
        } else {
            this.max = max;
        }
    }

    public boolean containedIn(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PriceRange that = (PriceRange) o;

        return that.min.ordinal() <= this.min.ordinal() &&
                that.max.ordinal() >= this.max.ordinal();
    }

    public PriceLevel getMin() {
        return min;
    }

    public PriceLevel getMax() {
        return max;
    }
}
