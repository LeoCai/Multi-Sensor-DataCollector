package com.leocai.detecdtion.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leocai on 16-5-19.
 */
public class RandomnessExtractor {

    private static final int MALKOV_ORDER = 4;
    private static final int CODINGLENGTH = 4;

    Map<String,byte[]> codeTable = new HashMap<>();
    private List<Byte> finalKey;

    public RandomnessExtractor(List<Byte> bitsList) {
        int len = bitsList.size();
        byte[] bits = new byte[len];
        for (int i = 0; i <len; i++) {
            bits[i] = bitsList.get(i);
        }
        String[] subStrings = subStringByMalkov(bits,MALKOV_ORDER);
        codeTable(subStrings,CODINGLENGTH);
    }

    private void codeTable(String[] subStrings, int codinglength) {
        List<Byte> finalKey = new ArrayList<>();
        for(String str:subStrings){
            for (int i = 0; i < str.length(); i+=codinglength) {
                String key ;
                if((i+codinglength)>str.length()) key = str.substring(i);
                else key = str.substring(i,i+codinglength);
                byte[] code = codeTable.get(key);
                for(byte c:code) finalKey.add(c);
            }
        }
        this.finalKey = finalKey;
    }

    private String[] subStringByMalkov(byte[] bits, int malkovOrder) {
        int len = bits.length;
        int stringNum = (int) Math.pow(2,malkovOrder);
        String[] subStrings = new String[stringNum];
        for (int i = 0; i < len; i++) {
            int strIndex = 0;
            for (int j = 0; j < malkovOrder; j++) {
                strIndex += bits[strIndex*2+j+len];
            }
            subStrings[strIndex]+=bits[i+malkovOrder+1];
        }
        return subStrings;
    }

    public List<Byte> getKey(List<Byte> bitsList) {
        return finalKey;
    }
}
