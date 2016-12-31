package com.leocai.detecdtion.transformation;

/**
 * Created by leocai on 15-10-10.
 */
public class MatrixRotate {
    public static double[][] rotateI2B(double[][] vertexArray, double[][] rotateMatrix) {
        return MatrixUtils.multiply(rotateMatrix,MatrixUtils.T(vertexArray));
    }
}
