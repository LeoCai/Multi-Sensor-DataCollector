package com.leocai.detecdtion.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extractor Randomness From Bits
 * Created by leocai on 16-5-19.
 */
public class RandomnessExtractor {

    private static final int MALKOV_ORDER = 4;
    private static final int CODINGLENGTH = 3;

    Map<String, String> codeTable = new HashMap<>();
    private List<Byte> finalKey;
    PermutationKeyGenerator permutationKeyGenerator = new PermutationKeyGenerator();

    /**
     * Once construct class, start computation
     *
     * @param bitsList
     */
    public RandomnessExtractor(List<Byte> bitsList) {
        int len = bitsList.size();
        byte[] bits = new byte[len];
        for (int i = 0; i < len; i++) {
            bits[i] = bitsList.get(i);
        }

        loadCodeTable();

        String[] subStrings = subStringByMalkov(bits, MALKOV_ORDER);
        codeTable(subStrings, CODINGLENGTH);
    }

    private void loadCodeTable() {
        for (int i = 0; i <= CODINGLENGTH; i++) {
            String[] keys = permutationKeyGenerator.generateKey(i, CODINGLENGTH);
            int codesize = (int) (Math.log(keys.length) / Math.log(2));
            if (codesize == 0) codesize = 1;
            String fomStr = "%" + codesize + "s";
            for (int j = 0; j < keys.length; j++) {
                String val = String.format(fomStr, Integer.toBinaryString(j)).replaceAll(" ", "0");
                codeTable.put(keys[j], val);
            }
        }
    }

    /**
     * code substring and return finalkeys
     *
     * @param subStrings
     * @param codinglength
     * @return
     */
    public List<Byte> codeTable(String[] subStrings, int codinglength) {
        List<Byte> finalKey = new ArrayList<>();
        for (String str : subStrings) {
            for (int i = 0; i < str.length(); i += codinglength) {
                String key;
                if ((i + codinglength) > str.length()) key = str.substring(i);
                else key = str.substring(i, i + codinglength);
                key = String.format("%" + codinglength + "s", key).replaceAll(" ", "0");
                String code = codeTable.get(key);
                for (int j = 0; j < code.length(); j++) {
                    finalKey.add(Byte.parseByte("" + code.charAt(j)));
                }
//                for(byte c:code) finalKey.add(c);
            }
        }
        this.finalKey = finalKey;
        return finalKey;
    }

    /**
     * compute substrings using malkov
     *
     * @param bits
     * @param malkovOrder
     * @return
     */
    public String[] subStringByMalkov(byte[] bits, int malkovOrder) {
        int len = (bits.length / malkovOrder - 1) * malkovOrder;
        int stringNum = (int) Math.pow(2, malkovOrder);
        String[] subStrings = new String[stringNum];
        for (int i = 0; i < subStrings.length; i++) {
            subStrings[i] = "";
        }
        for (int i = 0; i < len; i++) {
            int strIndex = 0;
            for (int j = 0; j < malkovOrder; j++) {
                strIndex = strIndex * 2 + bits[i + j];
            }
            subStrings[strIndex] += bits[i + malkovOrder];
        }
        return subStrings;
    }

    public List<Byte> getKey(List<Byte> bitsList) {
        return finalKey;
    }

    public static void main(String args[]) {
        List<Byte> bitsList = new ArrayList<>();

        byte[] bitsArray = new byte[]{0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1};
        for (byte b : bitsArray) {
            bitsList.add(b);
        }

        RandomnessExtractor randomnessExtractor = new RandomnessExtractor(bitsList);
        List<Byte> key = randomnessExtractor.getKey(bitsList);
        System.out.println(key.toString());
    }
}
