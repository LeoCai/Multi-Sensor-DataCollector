package com.leocai.detecdtion.core;

import com.leocai.detecdtion.utils.MathUtils;
import com.leocai.publiclibs.ShakingData;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leocai on 16-1-6.
 */
public class KeyExtractorTest extends TestCase {

    public void testGetCors() throws Exception {
        KeyExtractor keyExtractor = new Master();
        List<ShakingData> data1 = new ArrayList<>();
        double d1[][] = new double[][]{
                {1,2,3,4,5},
                {2,3,4,5,6},
                {3,4,5,6,7}
        };
        List<ShakingData> data2 = new ArrayList<>();
        double d2[][] = new double[][]{
                {2,3,1,5,6},
                {-3,-4,-5,-6,-7},
                {4,5,6,7,8}
        };
        for (int i = 0; i < d1[0].length; i++) {
            ShakingData sd1 = new ShakingData();
            sd1.setConvertedData(new double[]{d1[0][i],d1[1][i],d1[2][i]});
            data1.add(sd1);
            ShakingData sd2 = new ShakingData();
            sd2.setConvertedData(new double[]{d2[0][i], d2[1][i], d2[2][i]});
            data2.add(sd2);
        }
        double[] cors = MathUtils.getCors(data1, data2, data1.size());
        int i = 0;
    }
}