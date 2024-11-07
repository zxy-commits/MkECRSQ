package zxy.QRGMaptree.geometry;

import java.util.List;

/**
 * Not thread safe.
 *
 * @param <T> list type
 */
public final class ListPair<T extends HasGeometry> {
    private final Combination<T> combination1;
    private final Combination<T> combination2;
    // these non-final variable mean that this class is not thread-safe
    // because access to them is not synchronized
    private Float areaSum = null;
    private final Float marginSum;

    public ListPair(List<T> list1, List<T> list2) {
        this.combination1 = new Combination<T>(list1);
        this.combination2 = new Combination<T>(list2);
        this.marginSum = combination1.geometry().mbr().perimeter() + combination2.geometry().mbr().perimeter();
    }

    public Combination<T> combination1() {
        return combination1;
    }

    public Combination<T> combination2() {
        return combination2;
    }

    public float areaSum() {
        if (areaSum == null)
            areaSum = combination1.geometry().mbr().area() + combination2.geometry().mbr().area();
        return areaSum;
    }

    public float marginSum() {
        return marginSum;
    }

}
