package com.leocai.hellowear.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leocai on 15-12-23.
 */
public class RollApply {

    public static  List<Double> rollApply(List<Double> buffer, int window, RollApplyFunc rollApplyFunc) {
        List<Double> newBuffer = new ArrayList<>();
        for (int i = 0; i < buffer.size() - window + 1; i++) {
            Double val = rollApplyFunc.apply(buffer, i, window);
            newBuffer.add(val);
        }
        return newBuffer;
    }

    public static void main(String args[]) {

        List<Double> buffer = new ArrayList<>();
        double bf[] = new double[]{1,2,3,4,5,6,7,8,9,10};
        for(double b:bf){
            buffer.add(b);
        }
        List<Double> newBf = RollApply.rollApply(buffer, 5, new RollApplyFunc() {
            @Override
            public double apply(List<Double> buffer, int startIndex, int window) {
                double mean = 0;
                for (int i = startIndex; i < startIndex + window; i++) {
                    System.out.print(buffer.get(i) + " ");
                    mean += buffer.get(i);
                }
                System.out.println();
                return mean / window;
            }
        });
        for(double b : newBf){
            System.out.println(b);
        }
    }

}
