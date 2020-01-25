package com.company;

import java.io.*;
import java.util.*;


public class ConfigParser {
    public enum  Lexemes {
        IN,
        OUT,
        SRC
    }

    Map<String, String> fileNames = new HashMap<>();
    static final Map<Lexemes, String> configParam = new HashMap<>(){
        {
            put(Lexemes.IN, "IN");
            put(Lexemes.OUT, "OUT");
            put(Lexemes.SRC, "SRC");
        }
    };


    public  String Parse(String configFileName){
        FileReader configReader = null;
        Properties property = new Properties();
        try {
            configReader = new FileReader(configFileName);
            property.load(configReader);
            for(int i = 0; i < configParam.size() ; i++){
                String str = configParam.get(Lexemes.values()[i]);
                String buf;
                if((buf = property.getProperty(str)) == null){
                    return "no " + str + " file name";
                }
                else {
                    fileNames.put(str,buf);
                }
            }
        } catch (IOException ex) {
            return "config file does not parse";
        }
        try {
            configReader.close();
        } catch (IOException ex){
            return "config file does not close";
        }
        return "";
    }
}