package com.v.exo.recordtime;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Author:v
 * Time:2021/4/22
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class MergeUtil {
    public static void main(String[] args) {
        final LinkedList<Section> redTeaList0 = recordedList0();
        final List<Section> redTeaList1 = recordedList1();

//        mergeList(redTeaList0);
//        getTotalTime(redTeaList0);

        mergeList(redTeaList1);
        getTotalTime(redTeaList1);
    }

    private static long getTotalTime(List<Section> list) {
        if (list == null || list.size() == 0) {
            return 0L;
        }

        long t = 0L;
        for (Section s : list) {
            t += s.getEndPoint() - s.getStartPoint();
        }
        System.out.println("Total time is:" + t);
        return t;
    }


    public static List<Section> mergeList(List<Section> rawList) {
        if (rawList == null || rawList.size() == 0) {
            return rawList;
        }

        System.out.println("********before sort*******************");
        printList(rawList);

        rawList.sort(new SectionComparator());

        System.out.println("********after sort*******************");
        printList(rawList);

        merge(rawList);

        System.out.println("********after merge*******************");
        printList(rawList);

        return rawList;
    }

    private static void merge(List<Section> rawList) {
        ListIterator<Section> iterator = rawList.listIterator();
        Section tmp = iterator.next();
        System.out.println("tmp  " + tmp.toString());
        while (iterator.hasNext()) {
            Section next = iterator.next();
            System.out.println("next  " + next.toString());
            if (mergeSuccess(tmp, next)) {
                iterator.remove();
            } else {
                tmp = next;
            }
        }
    }

    private static boolean mergeSuccess(Section pre, Section next) {
        if (next.getStartPoint() <= pre.getEndPoint()) {
            pre.setEndPoint(Math.max(pre.getEndPoint(), next.getEndPoint()));
            return true;
        }

        return false;
    }


    /**
     * @return 模拟记录的表
     */
    private static LinkedList<Section> recordedList0() {
        final LinkedList<Section> ret = new LinkedList<>();

        Section s1 = new Section(0L, 12_000L);
        Section s2 = new Section(12_000L, 53_000L);
        Section s3 = new Section(56_000L, 99_100L);
        Section s4 = new Section(99_100L, 120_000L);


        ret.add(s2);
        ret.add(s1);
        ret.add(s4);
        ret.add(s3);

        return ret;
    }


    /**
     * @return 模拟记录的表
     */
    private static LinkedList<Section> recordedList1() {
        final LinkedList<Section> ret = new LinkedList<>();

        Section s1 = new Section(1_000L, 5_000L);
        Section s2 = new Section(3_000L, 10_000L);
        Section s3 = new Section(0L, 6_000L);
        Section s4 = new Section(11_000L, 15_000L);
        Section s5 = new Section(3_000L, 10_000L);


        ret.add(s1);
        ret.add(s2);
        ret.add(s3);
        ret.add(s4);
        ret.add(s5);

        return ret;
    }


    private static void printList(List<Section> list) {
        for (Section s : list) {
            System.out.println(s.toString());
        }
    }

    /**
     * 按 startPoint 由低到高排序
     */
    private static final class SectionComparator implements Comparator<Section> {

        @Override
        public int compare(Section o1, Section o2) {
            return (int) (o1.getStartPoint() - o2.getStartPoint());
        }
    }

}
