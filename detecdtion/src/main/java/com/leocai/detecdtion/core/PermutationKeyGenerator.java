package com.leocai.detecdtion.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * PermutationKeyGenerator
 */
public class PermutationKeyGenerator {

    private int numOne;
    private int n;
    private List<List<Integer>> permutations = new ArrayList<>();

    /**
     * if generateKey(2,3)
     * then output:
     * [0, 0, 0]
     * [0, 0, 1]
     * [0, 1, 0]
     * [0, 1, 1]
     * [1, 0, 0]
     * [1, 0, 1]
     * [1, 1, 0]
     *
     * @param numOne the numbers of one
     * @param n
     */
    public String[] generateKey(int numOne, int n) {
        this.numOne = numOne;
        this.n = n;
        permutations.clear();
        List<Integer> pList = new ArrayList<>();
        recursiveGenerate(0, 0, pList);
        String keys[] = new String[permutations.size()];
        int permutationSize = keys.length;
        for (int i = 0; i < permutationSize; i++) {
            StringBuilder key = new StringBuilder();
            List<Integer> keyList = permutations.get(i);
            for (Integer bit : keyList) key.append(bit);
            keys[i] = key.toString();
        }
//        for(List<Integer> pm:permutations){
//			System.out.println(pm.toString());
//		}
        return keys;
    }


    private void recursiveGenerate(int cuN, int cuNumOne, List<Integer> pList) {
        if (cuN == n) {
            if (cuNumOne == numOne)
                permutations.add(pList);
            // System.out.println(pList.toString());
            return;
        }
        List<Integer> newP = new ArrayList<>(pList);
        newP.add(0);
        cuN += 1;
        recursiveGenerate(cuN, cuNumOne, newP);
        if (cuNumOne < numOne) {
            pList.add(1);
            recursiveGenerate(cuN, cuNumOne + 1, pList);
        }

    }

    public static void main(String[] args) {
        PermutationKeyGenerator pm2 = new PermutationKeyGenerator();
        long start = System.nanoTime();
        pm2.generateKey(1, 4);
        System.out.println((System.nanoTime() - start) / 1000000);

//		start = System.nanoTime();
//		pm2.pm2(5, 50);
//		System.out.println((System.nanoTime() - start) / 1000000);
    }

}
