package com.leocai.hellowear.utils;

import java.util.List;

/**
 * Created by leocai on 15-12-23.
 */
public class ApplyFuncPeak implements RollApplyFunc {
    @Override
    public double apply(List<Double> buffer, int startIndex, int window) {
        int xCenterIndex = startIndex + (window - 1) / 2;
        double center = buffer.get(xCenterIndex), centerPre = buffer.get(xCenterIndex - 1), centerAfter = buffer.get(xCenterIndex + 1);
        double mean = 0, sd = 0;
        for (int i = startIndex; i < startIndex + window; i++) {
            mean += buffer.get(i);
        }
        mean /= window;
        for (int i = startIndex; i < startIndex + window; i++) {
            sd += Math.pow(buffer.get(i) - mean, 2);
        }
        sd = Math.sqrt(sd / window);
        if (center > 3
                && center > centerPre && center > centerAfter
                && center > (mean + 0.3 * sd))
            return center;
        return 0;
    }
}
