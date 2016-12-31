package com.leocai.hellowear.utils;

import java.util.List;

/**
 * Created by leocai on 15-12-23.
 */
public interface RollApplyFunc {

     double apply(List<Double> buffer, int startIndex, int window);
}
