package com.leocai.detecdtion.core;

import java.util.Arrays;
import java.util.List;

/**
 * Created by leocai on 15-12-31.
 */
public class ShakeBits {
    private  List<Byte> bits;

    public ShakeBits(List<Byte> bits) {
        this.bits = bits;
    }

    public List<Byte> getBits() {
        return bits;
    }

    public void setBits(List<Byte> bits) {
        this.bits = bits;
    }

    @Override
    public String toString() {
        return "ShakeBits{" +
                "bits=" + Arrays.toString(bits.toArray()) +
                '}';
    }
}
