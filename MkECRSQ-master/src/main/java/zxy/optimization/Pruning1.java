package zxy.optimization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import zxy.struct.GraphPoints;
import zxy.struct.Utils;


public class Pruning1 <T>{

	public Long nodeNum = Long.parseUnsignedLong("0");
	public Long gans = Long.parseUnsignedLong("0");

	//根据预处理的Pre_dsg，执行kcrsqDFS算法
	public ArrayList<ArrayList<GraphPoints<T>>> kcrsqDFS(ArrayList<ArrayList<GraphPoints<T>>> dsgs,int k){

		ArrayList<GraphPoints<T>> pre_points = Utils.preprocessing(dsgs, k);
		//System.out.println("预处理后的节点数为：" + pre_points.size());

		ArrayList<ArrayList<GraphPoints<T>>> k_skylineCombinations = new ArrayList<>();
		ArrayList<GraphPoints<T>> combinations;
		Stack<ArrayList<GraphPoints<T>>> skylineStack = new Stack<>();

		//RSQ中的所有skyline点
		HashSet<GraphPoints<T>> skylinePoints = new HashSet<GraphPoints<T>>();
		skylinePoints.addAll(dsgs.get(0));

		//初始化栈，将第一层元素加入栈中
//		for (int i=dsgs.get(0).size()-1;i>=0;i--) {
//			combinations = new ArrayList<>();
//			combinations.add(dsgs.get(0).get(i));
//			skylineStack.push(combinations);
//		}

		for(GraphPoints<T> points:dsgs.get(0)) {
			combinations = new ArrayList<>();
			combinations.add(points);
			skylineStack.push(combinations);
		}

		int ans = 0;

		//栈不为空，则循环
		while(!skylineStack.empty()) {

			combinations = new ArrayList<>();
			combinations.addAll(skylineStack.pop());
			//System.out.println(skylineStack.size());

			int tail = combinations.size() > 0 ? combinations.get(combinations.size()-1).point_index + 1:0;
			int tail_layer = combinations.get(combinations.size()-1).layer_index;

			//得到该combinations中每一个节点的孩子结点
			HashSet<GraphPoints<T>> childrenSet = new HashSet<>();
			for(GraphPoints<T> point : combinations) {
				childrenSet.addAll(point.children);
			}

			//剪枝操作
			//1.如果tail set中的结点不是newCombinations的孩子结点或者不是skyline结点，则忽略
			for(int j=tail;j<pre_points.size();j++) {

				nodeNum+=1;

				//line6-line10 剪枝
				//System.out.println("tail=" + tail + " pre=" + pre_points.size() + " j=" + j);
				GraphPoints<T> cur_point = pre_points.get(j);
				if( !childrenSet.contains(cur_point) && !skylinePoints.contains(cur_point)) continue;
				if( cur_point.layer_index - tail_layer >= 2 ) break;

				//line10-line14  入栈
				ArrayList<GraphPoints<T>> newCombinations = new ArrayList<>();
				newCombinations.addAll(combinations);
				newCombinations.add(cur_point);
				int k_newCombinations = IsSkylineCombination(newCombinations);

				//运行较慢的程序
//				if (k_newCombinations == newCombinations.size() && newCombinations.size() == k) {
//					k_skylineCombinations.add(newCombinations);
//				}
//				else if (k_newCombinations < k && newCombinations.size() < k) {
//					//tempStack.add(newCombinations);
//					skylineStack.push(newCombinations);
//				}

				if (k_newCombinations == combinations.size()+1 ) {

					if(newCombinations.size() == k) {
						k_skylineCombinations.add(newCombinations);
						gans += 1;
					}
					else{
						//tempStack.add(newCombinations);
						skylineStack.push(newCombinations);
					}
				}
			}

		}
		System.out.println("得到的MkECRSQ数目为：" +  gans  + " 遍历结点的数目:" + nodeNum );
		return k_skylineCombinations;

	}

	public int IsSkylineCombination(ArrayList<GraphPoints<T>> combinations) {
		HashSet<GraphPoints<T>> hashSet = new HashSet<>();
		for(GraphPoints<T> tail_points: combinations) {
			hashSet.addAll(tail_points.parents);
			hashSet.add(tail_points);
		}
		return hashSet.size();
	}
}
