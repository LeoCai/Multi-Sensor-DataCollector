package com.leocai.detecdtion.core;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leocai on 16-5-23.
 */
public class RandomnessExtractorTest extends TestCase {

    private List<Byte> bitsList = new ArrayList<>();
    {
        byte[] bitsArray = new byte[]{0,1,0,0,0,1,1,1};
        for(byte b:bitsArray){
            bitsList.add(b);
        }
    }
    RandomnessExtractor randomnessExtractor = new RandomnessExtractor(bitsList);

    public void testCodeTable() throws Exception {
    }

    public void testSubStringByMalkov() throws Exception {

    }

    public void testGetKey() throws Exception {
        List<Byte> key = randomnessExtractor.getKey(bitsList);
        System.out.println(key.toString());

    }
}