package zxy.algorithm;

import java.util.*;

import zxy.struct.GraphPoints;
import zxy.struct.Utils;

public class ECRSQ<T> {

//	//预处理之后的所有几点集合
//	public ArrayList<GraphPoints<T>> preprocessing(ArrayList<ArrayList<GraphPoints<T>>> dsgs,int k ){
//
//		ArrayList<GraphPoints<T>> pre_points = new ArrayList<GraphPoints<T>>();
//		//预处理，将|U| > k 的结点删除
//		//循环RSQ中每一层
//		for(ArrayList<GraphPoints<T>> layer : dsgs) {
//			//循环每一个layer中的每一个节点
//			for(GraphPoints<T> point : layer) {
//				if(point.layer_index <= k && point.parents.size()+1 <= k){
//					//判断该节点的|U|是否大于k
//					pre_points.add(point);
//					//System.out.println(point.toString());
//				}
//			}
//		}
//		//重置预处理后的index，按照在数组中的顺序从0 ~ size()-1
//		for(int i=0;i<pre_points.size();i++) pre_points.get(i).point_index = i;
//		return pre_points;
//	}

	public Long nodeNum = Long.parseUnsignedLong("0");
	public Long gans = Long.parseUnsignedLong("0");

	//根据预处理的Pre_dsg，执行kcrsq算法
	public int kcrsq(ArrayList<ArrayList<GraphPoints<T>>> dsgs,int k){


		ArrayList<GraphPoints<T>> pre_points = Utils.preprocessing(dsgs, k);
		//System.out.println("预处理后的节点数为：" + pre_points.size());

		//最终结果,combinations的集合
		ArrayList<ArrayList<ArrayList<GraphPoints<T>>>> k_skylineGcombinations = new ArrayList<ArrayList<ArrayList<GraphPoints<T>>>>();
		ArrayList<ArrayList<GraphPoints<T>>> levelCombination = new ArrayList<>();
		ArrayList<GraphPoints<T>> combination = new ArrayList<GraphPoints<T>>();

		//RSQ中的所有skyline点
		HashSet<GraphPoints<T>> skylinePoints = new HashSet<GraphPoints<T>>();
		skylinePoints.addAll(dsgs.get(0));
		//System.out.println("skyline points (first layer) number: "+ skylinePoints.size());

		//初始化k_skylineGcombinations(0),第一层combination,每个combination包含一个节点
		for(GraphPoints<T> point:dsgs.get(0)) {
			combination = new ArrayList<>();
			combination.add(point);
			levelCombination.add(combination);
		}
		//System.out.println("第一层Combinations:");
		//PrintCombination(levelCombination);
		k_skylineGcombinations.add(levelCombination);


		//line2 循环每一个k
		for(int i=1;i<k;i++) {

			//System.out.println("开始计算第" + (i+1) + "层Combinations:");

			levelCombination = new ArrayList<ArrayList<GraphPoints<T>>>();
			//line3-line14
			//循环i-1层中的每一个combination集合
			for(ArrayList<GraphPoints<T>> combinations:k_skylineGcombinations.get(i-1)) {

				//line4 - line5
				//得到该combinations中每一个节点的孩子结点
				HashSet<GraphPoints<T>> childrenSet = new HashSet<>();
				for(GraphPoints<T> point : combinations) {
					childrenSet.addAll(point.children);
				}

				//line6-line14
				int tail = 0;
				if(combinations.size()>0) tail = combinations.get(combinations.size()-1).point_index + 1;

				for(int j=tail;j<pre_points.size();j++) {

					nodeNum+=1;

					//line6-line10 剪枝
					//System.out.println("tail=" + tail + " pre=" + pre_points.size() + " j=" + j);
					GraphPoints<T> cur_point = pre_points.get(j);
					if( !childrenSet.contains(cur_point) && !skylinePoints.contains(cur_point)) continue;
					if( cur_point.layer_index - combinations.get(combinations.size()-1).layer_index >= 2 ) continue;

					//line10-line14  判断是否为 K-skylineGcombinations并加入levelCombination
					ArrayList<GraphPoints<T>> newCombination = new ArrayList<GraphPoints<T>>();
					newCombination.addAll(combinations);
					newCombination.add(cur_point);
					if( IsSkylineCombination(newCombination,i+1)) {
						gans += i+1 == k?1:0;
						levelCombination.add(newCombination);
					}
				}
				//将levelCombination加入k_skylineGcombinations
			}
			k_skylineGcombinations.add(levelCombination);
			//PrintCombination(levelCombination);
		}
		//PrintCombination(k_skylineGcombinations.get(k_skylineGcombinations.size()-1));
		System.out.println("得到的MkECRSQ数目为：" + gans + " 遍历结点的数目:" + nodeNum );
		return k_skylineGcombinations.get(k_skylineGcombinations.size()-1).size();

	}

	public boolean IsSkylineCombination(ArrayList<GraphPoints<T>> combinations , int k ) {
		HashSet<GraphPoints<T>> hashSet = new HashSet<>();
		for(GraphPoints<T> tail_points: combinations) {
			hashSet.addAll(tail_points.parents);
			hashSet.add(tail_points);
		}
		return (hashSet.size() == k);
	}

	public void PrintCombination(ArrayList<ArrayList<GraphPoints<T>>> levelcombinations) {

		for(ArrayList<GraphPoints<T>> combination : levelcombinations) {
			String combination_str = "{";
			for(GraphPoints<T> points : combination) {
				if(combination_str.length() != 1) combination_str += ",";
				combination_str += "(layer:" + points.layer_index + "," + points.value.toString() + ")";
			}
			combination_str += "}";
			System.out.println(combination_str);
		}

	}

}
