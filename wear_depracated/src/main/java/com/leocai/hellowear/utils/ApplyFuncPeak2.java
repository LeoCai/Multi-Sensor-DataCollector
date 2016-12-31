package com.leocai.hellowear.utils;

import java.util.List;

/**
 * Created by leocai on 15-12-23.
 */
public class ApplyFuncPeak2 implements RollApplyFunc {
    private double meanPeak;

    public ApplyFuncPeak2(double meanPeak) {
        this.meanPeak = meanPeak;
    }

    @Override
    public double apply(List<Double> buffer, int startIndex, int window) {
        double val = buffer.get(startIndex);
        if(val>meanPeak) return 2; else if(val!=0) return 1;
        return 0;
    }
}
