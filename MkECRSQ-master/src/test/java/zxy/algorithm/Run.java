package zxy.algorithm;
import java.util.ArrayList;

import zxy.optimization.CRSQC;
import zxy.struct.GraphPoints;
import zxy.struct.MultiDim;
import zxy.struct.TwoDim;

public class Run {
	
	public static void main(String[] args) {
		//读取数据文件
		ReadData readData = new ReadData();
		ArrayList<TwoDim> twodim_points = readData.buildTwoPoints("hotel_2.txt");
		
		System.out.println(twodim_points.size());
		long startTime = System.currentTimeMillis();
		BuildRSQTwoDim bDsg = new BuildRSQTwoDim();
		ArrayList<ArrayList<GraphPoints<TwoDim>>> skylineLayerTwoDim = bDsg.BuildSkylineLayerForTwoDim(twodim_points);
		ArrayList<ArrayList<GraphPoints<TwoDim>>> dsg = bDsg.BuildDsgForTwoDim(skylineLayerTwoDim,4);
		
		/**
		//bDsg.PrintRSQ(dsg);
		long startTime1 = System.currentTimeMillis();
		PointWise<TwoDim> kcrsq = new PointWise<TwoDim>();
		kcrsq.kcrsq(dsg, 4);
		long endTime1 = System.currentTimeMillis();
		**/
		
		System.out.println("----------------------");
		
		long startTime2 = System.currentTimeMillis();
		MkECRSQ.run(dsg, 4);
		long endTime2 = System.currentTimeMillis();

		//--------------MultiDim-------------------------
		//读取数据文件
		ArrayList<MultiDim> multi_dim_points = readData.buildMultiPoints("hotel_4.txt");

		BuildRSQMultiDim dsgMultiDim = new BuildRSQMultiDim();
		ArrayList<ArrayList<GraphPoints<MultiDim>>> skylineLayerMultiDim = dsgMultiDim.BuildSkylineLayerForMultiDim(multi_dim_points);
		ArrayList<ArrayList<GraphPoints<MultiDim>>> dsg_multi_dim = dsgMultiDim.BuildDsgForMultiDim(skylineLayerMultiDim,4);
		//dsgMultiDim.PrintRSQ(dsg_multi_dim);
		
		long startTime1 = System.currentTimeMillis();
		ECRSQ<MultiDim> kcrsq = new ECRSQ<MultiDim>();
		kcrsq.kcrsq(dsg_multi_dim, 4);
		long endTime1 = System.currentTimeMillis();
		
		long startTime3 = System.currentTimeMillis();
		MkECRSQPlus.run(dsg, 4);
		long endTime3 = System.currentTimeMillis();

		long startTime4 = System.currentTimeMillis();
		CRSQC<TwoDim> uWisePlusDFS = new CRSQC<TwoDim>(dsg, 4);
        uWisePlusDFS.run();
        long endTime4 = System.currentTimeMillis();

		long startTime5 = System.currentTimeMillis();

		long endTime5 = System.currentTimeMillis();

		System.out.println("程序kCRSQ运行时间为：" + (endTime1-startTime1) + "ms" );
		System.out.println("程序ECRSQ运行时间为：" + (endTime2-startTime2) + "ms" );
		System.out.println("程序MkECRSQ+运行时间为：" + (endTime3-startTime3) + "ms" );
		System.out.println("程序MkECRSQPlus运行时间为：" + (endTime4 - startTime4) + "ms");


		
 	}
	
	
}
