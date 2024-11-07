package zxy.QRGMaptree.geometry;

import zxy.QRGMaptree.Util;

import java.util.List;

public class Combination<T extends HasGeometry> implements HasGeometry {

    private final List<T> list;
    private final Rectangle mbr;

    public Combination(List<T> list) {
        this.list = list;
        this.mbr = Util.mbr(list);
    }

    public List<T> list() {
        return list;
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

}
