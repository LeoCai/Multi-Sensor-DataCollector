package com.leocai.hellowear.utils;

import java.util.List;

/**
 * Created by leocai on 15-12-23.
 */
public class ApplyFuncMean implements RollApplyFunc {
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
}
