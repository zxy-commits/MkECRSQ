package zxy.algorithm;

import zxy.optimization.*;
import zxy.struct.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Experiments {

    //以数据集的维度数量为x轴，y轴为算法运算时间，输入参数dataPreName为数据名字前缀（"anti","clu","uniform"）,k为combination的数目
    //在data/output文件夹下生成输出的时间，第一列为x轴
    public static void drawBydimensions(String dataPreName,int k) {
        int maxDim = 4;
        double[] timePoint = new double[maxDim];
        double[] timeUnit = new double[maxDim];
        double[] timeUnitPlus = new double[maxDim];
        int[] xaxis = new int[maxDim];
        long startTime,buildDsgTime,finallyTimePoint,finallyTimeUnit,finallyTimeUnitPlus;
        for(int i=0;i<maxDim;i++) {
            System.out.println("-----------------------------");
            String filepath =  dataPreName + "_" + String.valueOf(i*2+2) + ".txt";
            xaxis[i] = i*2+2;
            ReadData readData = new ReadData();
            startTime = System.currentTimeMillis();

            //--------------TwoDim-------------------------
            if(i==2) {
                ArrayList<TwoDim> twodim_points = readData.buildTwoPoints(filepath);

                BuildRSQTwoDim bDsg = new BuildRSQTwoDim();
                ArrayList<ArrayList<GraphPoints<TwoDim>>> skylineLayerTwoDim = bDsg.BuildSkylineLayerForTwoDim(twodim_points);
                ArrayList<ArrayList<GraphPoints<TwoDim>>> dsg = bDsg.BuildDsgForTwoDim(skylineLayerTwoDim,k);
                buildDsgTime = System.currentTimeMillis() - startTime;

                startTime = System.currentTimeMillis();
                ECRSQ<TwoDim> kcrsq = new ECRSQ<TwoDim>();
                kcrsq.kcrsq(dsg, k);
                finallyTimePoint = System.currentTimeMillis()-startTime+buildDsgTime;
                timePoint[i] = finallyTimePoint;
                System.out.println(filepath + "CombinationSize:" + k +  " kCRSQ Time:" + finallyTimePoint + "ms" );

                startTime = System.currentTimeMillis();
                MkECRSQ.run(dsg, k);
                finallyTimeUnit = System.currentTimeMillis()-startTime+buildDsgTime;
                timeUnit[i] = finallyTimeUnit;
                System.out.println(filepath + "CombinationSize:" + k +  " ERSQC Time:" + finallyTimeUnit + "ms" );

                startTime = System.currentTimeMillis();
                MkECRSQPlus.run(dsg, k);
                finallyTimeUnitPlus = System.currentTimeMillis()-startTime+buildDsgTime;
                timeUnitPlus[i] = finallyTimeUnitPlus;
                System.out.println(filepath + "CombinationSize:" + k +  " MkECRSQ Time:" + finallyTimeUnitPlus + "ms" );

            }
            //--------------MultiDim-------------------------
            else {
                ArrayList<MultiDim> multi_dim_points = readData.buildMultiPoints(filepath);

                BuildRSQMultiDim dsgMultiDim = new BuildRSQMultiDim();
                ArrayList<ArrayList<GraphPoints<MultiDim>>> skylineLayerMultiDim = dsgMultiDim.BuildSkylineLayerForMultiDim(multi_dim_points);
                ArrayList<ArrayList<GraphPoints<MultiDim>>> dsg_multi_dim = dsgMultiDim.BuildDsgForMultiDim(skylineLayerMultiDim,k);
                buildDsgTime = System.currentTimeMillis() - startTime;

                startTime = System.currentTimeMillis();
                ECRSQ<MultiDim> kcrsq = new ECRSQ<MultiDim>();
                kcrsq.kcrsq(dsg_multi_dim, k);
                finallyTimePoint = System.currentTimeMillis()-startTime+buildDsgTime;
                timePoint[i] = finallyTimePoint;
                System.out.println(filepath + "CombinationSize:" + k +  " kCRSQ Time:" + finallyTimePoint + "ms" );

                startTime = System.currentTimeMillis();
                MkECRSQ.run(dsg_multi_dim, k);
                finallyTimeUnit = System.currentTimeMillis()-startTime+buildDsgTime;
                timeUnit[i] = finallyTimeUnit;
                System.out.println(filepath + "CombinationSize:" + k +  " ECRSQ Time:" + finallyTimeUnit + "ms" );

                startTime = System.currentTimeMillis();
                MkECRSQPlus.run(dsg_multi_dim, k);
                finallyTimeUnitPlus = System.currentTimeMillis()-startTime+buildDsgTime;
                timeUnitPlus[i] = finallyTimeUnitPlus;
                System.out.println(filepath + "CombinationSize:" + k +  " MkECRSQ Time:" + finallyTimeUnitPlus + "ms" );
            }
        }
        ArrayList<double[]> allTime = new ArrayList<>();
        allTime.add(timePoint);
        allTime.add(timeUnit);
        allTime.add(timeUnitPlus);
        String filepath = "output/DimTime_CombinationSize" + k + "_"  + dataPreName + ".txt";
        getResult(allTime, xaxis, filepath);
    }

    //以combination数量为x轴，y轴为算法运算时间,输入filepath为data文件名，参数k为最大combination数目
    //在data/output文件夹下生成输出的时间，第一列为x轴
    public static void drawByCombination(String filename,int k) {
        double[] timePoint = new double[k];
        double[] timeUnit = new double[k];
        double[] timeUnitPlus = new double[k];
        int[] xaxis = new int[k];
        long startTime,buildDsgTime,finallyTimePoint,finallyTimeUnit,finallyTimeUnitPlus;

        String filepath = filename;
        ReadData readData = new ReadData();
        if(filename.contains("2")) {
            for(int i=1;i<=k;i++) {
                xaxis[i-1] = i;
                System.out.println("-----------------------------");
                startTime = System.currentTimeMillis();
                ArrayList<TwoDim> twodim_points = readData.buildTwoPoints(filepath);

                BuildRSQTwoDim bDsg = new BuildRSQTwoDim();
                ArrayList<ArrayList<GraphPoints<TwoDim>>> skylineLayerTwoDim = bDsg.BuildSkylineLayerForTwoDim(twodim_points);
                ArrayList<ArrayList<GraphPoints<TwoDim>>> dsg = bDsg.BuildDsgForTwoDim(skylineLayerTwoDim,i);
                buildDsgTime = System.currentTimeMillis() - startTime;

                startTime = System.currentTimeMillis();
                ECRSQ<TwoDim> kcrsq = new ECRSQ<TwoDim>();
                kcrsq.kcrsq(dsg, i);
                finallyTimePoint = System.currentTimeMillis()-startTime+buildDsgTime;
                timePoint[i-1] = finallyTimePoint;
                System.out.println(filepath + " combinationSize:" + i + " kCRSQ Time:" + finallyTimePoint + "ms" );

                startTime = System.currentTimeMillis();
                MkECRSQ.run(dsg, i);
                finallyTimeUnit = System.currentTimeMillis()-startTime+buildDsgTime;
                timeUnit[i-1] = finallyTimeUnit;
                System.out.println(filepath + " combinationSize:" + i + " ECRSQ Time:" + finallyTimeUnit + "ms" );

                startTime = System.currentTimeMillis();
                MkECRSQPlus.run(dsg, i);
                finallyTimeUnitPlus = System.currentTimeMillis()-startTime+buildDsgTime;
                timeUnitPlus[i-1] = finallyTimeUnitPlus;
                System.out.println(filepath + " combinationSize:" + i + " MkECRSQ Time:" + finallyTimeUnitPlus + "ms" );


            }
        }else {
            for(int i=1;i<=k;i++) {
                xaxis[i-1] = i;
                System.out.println("-----------------------------");
                startTime = System.currentTimeMillis();
                ArrayList<MultiDim> multi_dim_points = readData.buildMultiPoints(filepath);

                BuildRSQMultiDim dsgMultiDim = new BuildRSQMultiDim();
                ArrayList<ArrayList<GraphPoints<MultiDim>>> skylineLayerMultiDim = dsgMultiDim.BuildSkylineLayerForMultiDim(multi_dim_points);
                ArrayList<ArrayList<GraphPoints<MultiDim>>> dsg_multi_dim = dsgMultiDim.BuildDsgForMultiDim(skylineLayerMultiDim,i);
                buildDsgTime = System.currentTimeMillis() - startTime;

                startTime = System.currentTimeMillis();
                ECRSQ<MultiDim> kcrsq = new ECRSQ<MultiDim>();
                kcrsq.kcrsq(dsg_multi_dim, i);
                finallyTimePoint = System.currentTimeMillis()-startTime+buildDsgTime;
                timePoint[i-1] = finallyTimePoint;
                System.out.println(filepath + " combinationSize:" + i +  " kCRSQ Time:" + finallyTimePoint + "ms" );

                startTime = System.currentTimeMillis();
                MkECRSQ.run(dsg_multi_dim, i);
                finallyTimeUnit = System.currentTimeMillis()-startTime+buildDsgTime;
                timeUnit[i-1] = finallyTimeUnit;
                System.out.println(filepath + " combinationSize:" + i + " ECRSQ Time:" + finallyTimeUnit + "ms" );

                startTime = System.currentTimeMillis();
                MkECRSQPlus.run(dsg_multi_dim, i);
                finallyTimeUnitPlus = System.currentTimeMillis()-startTime+buildDsgTime;
                timeUnitPlus[i-1] = finallyTimeUnitPlus;
                System.out.println(filepath + " combinationSize:" + i + " MkECRSQ Time:" + finallyTimeUnitPlus + "ms" );
            }
        }

        ArrayList<double[]> allTime = new ArrayList<>();
        allTime.add(timePoint);
        allTime.add(timeUnit);
        allTime.add(timeUnitPlus);
        filepath = "output/CombinationNumTime_MaxCombinationSize" + k + "_" + filename ;
        getResult(allTime, xaxis, filepath);
    }

    //获取实验的输出文本
    public static void getResult(ArrayList<double[]> allTime,int[] xaxis,String filepath) {

        try {
            //判断目录下是否有output文件夹,若不存在则创建目录
            File file = new File("output");
            if(!file.exists() && !file.isDirectory()) {
                file.mkdir();
            }

            file = new File(filepath);
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int len = xaxis.length;
            String output = "";
            for(int i=0;i<len;i++) {
                output += String.valueOf(xaxis[i]) + "	" + String.valueOf(allTime.get(0)[i]) + "	" + String.valueOf(allTime.get(1)[i]) + "	" + String.valueOf(allTime.get(2)[i]) + "\n";
            }
            writer.write(output);
            writer.flush();
            writer.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    //遍历所有文本进行实验，得到实验输出,其中maxCombination为drawByCombination中最大组数，combinations为drawBydimensions每个维度数据运行的combination数目；
    public static void AllExperiments(String[] allCombinationName,String[] allDimPreName,int maxCombination,int combinations) {

        try {
            //按数据combination数量进行实验，得到结果
            for(String file:allCombinationName) {
                drawByCombination(file, maxCombination);
            }

            //按数据维度进行实验，得到结果
            for(String name:allDimPreName) {
                drawBydimensions(name, combinations);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

    }


    public static void RunSingleFile(String filename,int k,int kind) {
        ReadData readData = new ReadData();
        long startTime ;
        if(filename.contains("2")) {
            ArrayList<TwoDim> twodim_points = readData.buildTwoPoints(filename);

            startTime = System.currentTimeMillis();
            BuildRSQTwoDim bDsg = new BuildRSQTwoDim();
            ArrayList<ArrayList<GraphPoints<TwoDim>>> skylineLayerTwoDim = bDsg.BuildSkylineLayerForTwoDim(twodim_points);
            ArrayList<ArrayList<GraphPoints<TwoDim>>> dsg = bDsg.BuildDsgForTwoDim(skylineLayerTwoDim,k);

            //运行kcrsq算法
            if(kind == 0) {
                ECRSQ<TwoDim> kcrsq = new ECRSQ<TwoDim>();
                kcrsq.kcrsq(dsg, k);
                System.out.println(filename + " kCRSQ Time:" + (System.currentTimeMillis()-startTime) + "ms" );
            }else if(kind == 1) {
                MkECRSQ.run(dsg, k);
                System.out.println(filename + " CRSQC Time:" + (System.currentTimeMillis()-startTime) + "ms" );
            }else if(kind == 2) {
                MkECRSQPlus.run(dsg, k);
                System.out.println(filename + " MkECRSQ Time:" + (System.currentTimeMillis()-startTime) + "ms" );
            }else if(kind == 3) {
                CRSQC<TwoDim> uWisePlusDFS = new CRSQC<>(dsg, k);
                uWisePlusDFS.run();
                System.out.println(filename + "MkECRSQPlus Time:" + (System.currentTimeMillis()-startTime) + "ms");
            }
        }else {
            ArrayList<MultiDim> multidim_points = readData.buildMultiPoints(filename);

            startTime = System.currentTimeMillis();
            BuildRSQMultiDim bDsg = new BuildRSQMultiDim();
            ArrayList<ArrayList<GraphPoints<MultiDim>>> skylineLayerTwoDim = bDsg.BuildSkylineLayerForMultiDim(multidim_points);
            ArrayList<ArrayList<GraphPoints<MultiDim>>> dsg = bDsg.BuildDsgForMultiDim(skylineLayerTwoDim,k);

            //运行kcrsq算法
            if(kind == 0) {
                ECRSQ<MultiDim> kcrsq = new ECRSQ<>();
                kcrsq.kcrsq(dsg, k);
                System.out.println(filename + " kCRSQ Time:" + (System.currentTimeMillis()-startTime) + "ms" );
            }else if(kind == 1) {
                MkECRSQ.run(dsg, k);
                System.out.println(filename + " ECRSQ Time:" + (System.currentTimeMillis()-startTime) + "ms" );
            }else if(kind == 2) {
                MkECRSQPlus.run(dsg, k);
                System.out.println(filename + " MkECRSQ Time:" + (System.currentTimeMillis()-startTime) + "ms" );
            }else if(kind == 3) {
                CRSQC<MultiDim> uWisePlusDFS = new CRSQC<>(dsg, k);
                uWisePlusDFS.run();
                System.out.println(filename + "MkECRSQPlus Time:" + (System.currentTimeMillis()-startTime) + "ms");
            }
        }
    }

    public static void main(String[] args) {



        Scanner input=new Scanner(System.in);
 		System.out.println("输入要跑的文件名：");
 		String name = input.next();
 		System.out.println("输入CombinationSize：");
 		int k = input.nextInt();
 		System.out.println("输入算法（0位kcrsq，1位ECRSQ，2为MkECRSQ，3为MkECRSQPlus）");
 		int kind = input.nextInt();
 		RunSingleFile(name,k,kind );




    }
}
