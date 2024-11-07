package zxy.algorithm;

import java.util.*;

public class KCRSQ {

    // 主算法入口
    public static void kCRSQ(List<Point> userDataset, List<QRGMapTree> dynamicSkylineDataset, List<QRGMapTree> CGSet, List<QRGMapTree> RSet) {
        // 1. 遍历CGSet，为每个候选组合创建候选R树和GiMap
        List<QRGMapTree> candidateQRGMapTrees = new ArrayList<>();
        Map<QRGMapTreeNode, List<Point>> GiMap = new HashMap<>();

        for (QRGMapTree candidate : CGSet) {
            candidateQRGMapTrees.add(candidate);
            GiMap.put(candidate.root, new ArrayList<>());
        }

        // 2. 将每个候选R树的根节点作为子节点插入四叉树，构建QR-GMap索引
        Quadtree qrGMap = new Quadtree();
        for (QRGMapTree candidate : candidateQRGMapTrees) {
            qrGMap.root.data.add(candidate.root);
        }

        // 3. 构建RSet的R树
        QRGMapTree rSetTree = buildQRGMapTree(RSet);

        // 4. 初始化堆H，插入QR-GMap树的根节点
        Heap heapH = new Heap();
        heapH.insert(qrGMap.root);

        // 5. 开始处理堆H
        while (!heapH.isEmpty()) {
            QRGMapTreeNode h = heapH.pop();

            // 7. 如果h是QR-GMap索引中的新组合节点
            if (isNewCombinationNode(h)) {
                Set<Point> S = new HashSet<>();  // 存储当前候选组合的全局天际线集
                Heap heapHs = new Heap();
                heapHs.insert(h);

                // 10. 处理heapHs
                while (!heapHs.isEmpty()) {
                    QRGMapTreeNode e = heapHs.pop();
                    List<Point> temp = new ArrayList<>();

                    // 13. 如果e被S中的任何点全局支配，直接丢弃e
                    if (isGloballyDominated(e, S)) {
                        continue;
                    }

                    // 16. 如果e是中间条目
                    if (isIntermediateEntry(e)) {
                        for (QRGMapTreeNode ei : e.children) {
                            if (!isGloballyDominated(ei, S)) {
                                heapHs.insert(ei);
                            }
                        }
                    } else {
                        // 20. e是数据点且未被全局支配
                        S.add(new Point(e.mbr));
                    }

                    // 22. 如果h是数据点
                    if (isDataPoint(h)) {
                        if (isCandidateCombinationNode(h) && isWithinDDR(h, e)) {
                            // 24. 丢弃e
                            continue;
                        } else if (isCandidateCombinationNode(h) && isWithinDADR(h, e)) {
                            // 25. 将h和e放入GiMap
                            GiMap.get(h).add(new Point(e.mbr));
                        } else {
                            // 28. 分解h并插入堆H
                            decomposeAndInsert(h, heapH);
                        }
                    }
                }
            }
        }

        // 39. 遍历GiMap，找到具有最大GiMap.value的组合Gi
        List<QRGMapTreeNode> GSet = new ArrayList<>();
        List<Point> SG = new ArrayList<>();

        for (Map.Entry<QRGMapTreeNode, List<Point>> entry : GiMap.entrySet()) {
            if (entry.getValue().size() > 0) {
                GSet.add(entry.getKey());
                SG.addAll(entry.getValue());
            }
        }


    }
    private static boolean isNewCombinationNode(QRGMapTreeNode node) {
        // 假设QRGMapTreeNode有一个属性isCombinationNode来标识是否为组合节点
        return node.isCombinationNode && !node.visited;
    }
    private static boolean isGloballyDominated(QRGMapTreeNode node, Set<Point> skylineSet) {
        for (Point skylinePoint : skylineSet) {
            if (dominates(skylinePoint, node.dataPoint)) {
                return true;
            }
        }
        return false;
    }

    // 判断点 p1 是否支配点 p2
    private static boolean dominates(Point p1, Point p2) {
        boolean dominatesInAllDimensions = true;
        for (int i = 0; i < p1.coordinates.length; i++) {
            if (p1.coordinates[i] > p2.coordinates[i]) {
                dominatesInAllDimensions = false;
                break;
            }
        }
        return dominatesInAllDimensions;
    }
    private static boolean isIntermediateEntry(QRGMapTreeNode node) {
        return node.children.size() > 0;
    }
    private static boolean isDataPoint(QRGMapTreeNode node) {
        return node.children.size() == 0;
    }
    private static boolean isCandidateCombinationNode(QRGMapTreeNode node) {
        // 假设QRGMapTreeNode有一个属性isCandidateCombination来标识是否为候选组合节点
        return node.isCandidateCombination;
    }
    private static boolean isWithinDDR(QRGMapTreeNode node, QRGMapTreeNode e) {
        // 假设QRGMapTreeNode有一个方法getMBR()返回其最小包围矩形
        double[] nodeMBR = node.getMBR();
        double[] ddrMBR = e.getDDR();  // 假设e有一个DDR区域的MBR

        // 判断node的MBR是否完全位于e的DDR内
        return isMBRWithin(nodeMBR, ddrMBR);
    }

    // 判断MBR1是否完全位于MBR2内
    private static boolean isMBRWithin(double[] mbr1, double[] mbr2) {
        for (int i = 0; i < mbr1.length / 2; i++) {
            if (mbr1[2 * i] < mbr2[2 * i] || mbr1[2 * i + 1] > mbr2[2 * i + 1]) {
                return false;
            }
        }
        return true;
    }
    private static boolean isWithinDADR(QRGMapTreeNode node, QRGMapTreeNode e) {
        // 假设QRGMapTreeNode有一个方法getDADR()返回其DADR区域的MBR
        double[] nodeMBR = node.getMBR();
        double[] dadrMBR = e.getDADR();  // 假设e有一个DADR区域的MBR

        // 判断node的MBR是否完全位于e的DADR内
        return isMBRWithin(nodeMBR, dadrMBR);
    }
    private static void decomposeAndInsert(QRGMapTreeNode node, Heap heap) {
        // 如果当前节点是中间条目，则将其子节点插入堆中
        if (isIntermediateEntry(node)) {
            for (QRGMapTreeNode child : node.children) {
                heap.insert(child);
            }
        }
    }






}

