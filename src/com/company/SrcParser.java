package com.company;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SrcParser {
    public enum Lexemes {
        CONSUMER,
        WORKER,
        COUNT_WORKER
    }

    Map<String, String> worker = new HashMap<>();
    int countWorkers;

    static final Map<Lexemes, String> srcParam = new HashMap<>(){
        {
            put(Lexemes.CONSUMER, "CONSUMER");
            put(Lexemes.WORKER, "WORKER");
            put(Lexemes.COUNT_WORKER, "COUNT_WORKER");
        }
    };

    FileReader srcReader = null;
    SrcParser(String srcName){
        try {
            srcReader = new FileReader(srcName);
        }catch (IOException ex){
            Log.Print("Cant't open file");
        }
        return;
    }
    public  String Parse(String srcFileName) {
        Properties property = new Properties();
        try {
            srcReader = new FileReader(srcFileName);
            property.load(srcReader);
            String str = srcParam.get(Lexemes.COUNT_WORKER);
            String countW = property.getProperty(str);
            countWorkers = Integer.parseInt(countW);

            for (int i = 0; i < countWorkers; i++) {
                str = Integer.toString(i + 1);
                String buf = null;
                if ((buf = property.getProperty(srcParam.get(Lexemes.WORKER) + str)) == null) {
                    Log.Print("Worker" + str + " doesn't exist");
                    return "";
                } else {
                    worker.put(srcParam.get(Lexemes.WORKER) + str, buf);
                }
            }
            for(int i = 0; i < countWorkers; i++) {
                str = Integer.toString(i + 1);
                String buf = null;
                if ((buf = property.getProperty(srcParam.get(Lexemes.CONSUMER) + str)) == null) {
                    Log.Print("Consumer doesn't exist: Worker" + str);
                    return "";
                } else {
                    worker.put(srcParam.get(Lexemes.CONSUMER) + str, buf);
                }
            }

        } catch (IOException ex) {
            return "src file does not parse";
        }

        return "";
    }
    public Map<String, String> getWorker() {
        return worker;
    }
    public int getCountWorkers(){
        return countWorkers;
    }
}
