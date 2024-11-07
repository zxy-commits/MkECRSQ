package zxy.QRGMaptree;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import zxy.QRGMaptree.geometry.HasGeometry;
import zxy.QRGMaptree.geometry.ListPair;
import zxy.QRGMaptree.geometry.Rectangle;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public final class SplitterQuadratic implements Splitter {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HasGeometry> ListPair<T> split(List<T> items, int minSize) {
        Preconditions.checkArgument(items.size() >= 2);

        // according to
        // http://en.wikipedia.org/wiki/R-tree#Splitting_an_overflowing_node

        // find the worst combination pairwise in the list and use them to start
        // the two combinations
        final Pair<T> worstCombination = worstCombination(items);

        // worst combination to have in the same node is now e1,e2.

        // establish a combination around e1 and another combination around e2
        final List<T> combination1 = Lists.newArrayList(worstCombination.value1());
        final List<T> combination2 = Lists.newArrayList(worstCombination.value2());

        final List<T> remaining = new ArrayList<T>(items);
        remaining.remove(worstCombination.value1());
        remaining.remove(worstCombination.value2());

        final int minCombinationSize = items.size() / 2;

        // now add the remainder to the combinations using least mbr area increase
        // except in the case where minimumSize would be contradicted
        while (remaining.size() > 0) {
            assignRemaining(combination1, combination2, remaining, minCombinationSize);
        }
        return new ListPair<T>(combination1, combination2);
    }

    private <T extends HasGeometry> void assignRemaining(final List<T> combination1,
                                                         final List<T> combination2, final List<T> remaining, final int minCombinationSize) {
        final Rectangle mbr1 = Util.mbr(combination1);
        final Rectangle mbr2 = Util.mbr(combination2);
        final T item1 = getBestCandidateForCombination(remaining, combination1, mbr1);
        final T item2 = getBestCandidateForCombination(remaining, combination2, mbr2);
        final boolean area1LessThanArea2 = item1.geometry().mbr().add(mbr1).area() <= item2
                .geometry().mbr().add(mbr2).area();

        if (area1LessThanArea2 && (combination2.size() + remaining.size() - 1 >= minCombinationSize)
                || !area1LessThanArea2 && (combination1.size() + remaining.size() == minCombinationSize)) {
            combination1.add(item1);
            remaining.remove(item1);
        }
        else {
            combination2.add(item2);
            remaining.remove(item2);
        }
    }

    @VisibleForTesting
    static <T extends HasGeometry> T getBestCandidateForCombination(List<T> list, List<T> combination,
                                                              Rectangle combinationMbr) {
        Optional<T> minEntry = absent();
        Optional<Double> minArea = absent();
        for (final T entry : list) {
            final double area = combinationMbr.add(entry.geometry().mbr()).area();
            if (!minArea.isPresent() || area < minArea.get()) {
                minArea = of(area);
                minEntry = of(entry);
            }
        }
        return minEntry.get();
    }

    @VisibleForTesting
    static <T extends HasGeometry> Pair<T> worstCombination(List<T> items) {
        Optional<T> e1 = absent();
        Optional<T> e2 = absent();
        {
            Optional<Double> maxArea = absent();
            for (final T entry1 : items) {
                for (final T entry2 : items) {
                    if (entry1 != entry2) {
                        final double area = entry1.geometry().mbr().add(entry2.geometry().mbr())
                                .area();
                        if (!maxArea.isPresent() || area > maxArea.get()) {
                            e1 = of(entry1);
                            e2 = of(entry2);
                            maxArea = of(area);
                        }
                    }
                }
            }
        }
        if (e1.isPresent())
            return new Pair<T>(e1.get(), e2.get());
        else
            // all items are the same item
            return new Pair<T>(items.get(0), items.get(1));
    }
}
