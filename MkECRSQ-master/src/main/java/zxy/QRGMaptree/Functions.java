package zxy.QRGMaptree;

import rx.functions.Func1;
import zxy.QRGMaptree.geometry.HasGeometry;
import zxy.QRGMaptree.geometry.ListPair;
import zxy.QRGMaptree.geometry.Rectangle;

import java.util.List;

/**
 * Utility functions for making {@link Selector}s and {@link Splitter}s.
 */
public final class Functions {

    private Functions() {
        // prevent instantiation
    }

    public static final Func1<ListPair<? extends HasGeometry>, Double> overlapListPair = new Func1<ListPair<? extends HasGeometry>, Double>() {

        @Override
        public Double call(ListPair<? extends HasGeometry> pair) {
            return (double) pair.combination1().geometry().mbr()
                    .intersectionArea(pair.combination2().geometry().mbr());
        }
    };

    public static Func1<HasGeometry, Double> overlapArea(final Rectangle r,
                                                         final List<? extends HasGeometry> list) {
        return new Func1<HasGeometry, Double>() {

            @Override
            public Double call(HasGeometry g) {
                Rectangle gPlusR = g.geometry().mbr().add(r);
                double m = 0;
                for (HasGeometry other : list) {
                    if (other != g) {
                        m += gPlusR.intersectionArea(other.geometry().mbr());
                    }
                }
                return m;
            }
        };
    }

    public static Func1<HasGeometry, Double> areaIncrease(final Rectangle r) {
        return new Func1<HasGeometry, Double>() {
            @Override
            public Double call(HasGeometry g) {
                Rectangle gPlusR = g.geometry().mbr().add(r);
                return (double) (gPlusR.area() - g.geometry().mbr().area());
            }
        };
    }

}
