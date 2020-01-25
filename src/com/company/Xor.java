package com.company;

public class Xor {
    static String key;
    Xor(String Key){
        key = Key;
    }

    public byte[] Coder(byte[] text){

        byte[] res = new byte[text.length];
        for (int i = 0; i < text.length; i++)
            res[i] = (byte)(((byte)text[i])^ key.getBytes()[i % key.length()]);

        return res;
    }
}