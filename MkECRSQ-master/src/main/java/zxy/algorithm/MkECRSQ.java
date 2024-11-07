package zxy.algorithm;

import zxy.struct.GraphPoints;
import zxy.struct.Combination;
import zxy.struct.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MkECRSQ {

    public static<T> void run(ArrayList<ArrayList<GraphPoints<T>>> dsg, int k) {
        // 预处理dsg
        ArrayList<GraphPoints<T>> pre_points = Utils.preprocessing(dsg, k);
        
        int ans = 0;
        int total_point_size = pre_points.size();

        // 顺序遍历数组
        for (int i = 0; i < total_point_size; ++i) {
            GraphPoints<T> unit = pre_points.get(i);
            Combination<T> g_1 = new Combination<>(unit);
            // 当前unit里元素个数大于等于k
            int number_of_point = g_1.getNumberOfPoints();
            if (number_of_point >= k) {
                //g_1.print();
                if (number_of_point == k) {
                    ans++;
                }
                continue;
            }

            Queue<Combination> queue = new LinkedList<>();
            queue.offer(g_1);
            while (!queue.isEmpty()) {
                Combination<T> g = queue.poll();
                for (int idx: g.getCandidateUnitCombinationsForward(total_point_size)) {
                    Combination g_plus = new Combination(g);
                    g_plus.mergeUnitCombination(pre_points.get(idx));
                    int point_number = g_plus.getNumberOfPoints();
                    if (point_number == k) {
                        //g_plus.print();
                    	ans++;
                    }
                    else if (point_number < k) {
                        queue.offer(g_plus);
                    }
                }
            }
        }
        System.out.println("UnitCombinationWise的MkECRSQCombination数目为:" + ans);
    }
}
