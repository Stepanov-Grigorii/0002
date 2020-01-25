package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Manager {
    DataInputStream inFile = null;
    DataOutputStream outFile = null;
    private enum  Lexemes {
        CONSUMER,
        WORKER
    }
    Worker[] workerList;
    static Map<String,String>workerPrList = new HashMap<>(){
        {
            put(Lexemes.CONSUMER.toString(), "CONSUMER");
            put(Lexemes.WORKER.toString(), "WORKER");
        }
    };
    Manager(String configFileName){
        FileReader srcReader = null;
        ConfigParser parser = new ConfigParser();
        String lg = parser.Parse(configFileName);
        try {
            inFile = new DataInputStream(new FileInputStream(parser.fileNames.get(ConfigParser.configParam.get(ConfigParser.Lexemes.IN))));
            outFile = new DataOutputStream(new FileOutputStream(parser.fileNames.get(ConfigParser.configParam.get(ConfigParser.Lexemes.OUT))));
        }catch (IOException ex){
            Log.Print("Can't open file");
            return;
        }
        SrcParser srcParser = new SrcParser(parser.fileNames.get(ConfigParser.configParam.get(ConfigParser.Lexemes.SRC)));
        srcParser.Parse(parser.fileNames.get(ConfigParser.configParam.get(ConfigParser.Lexemes.SRC)));
        workerPrList = srcParser.getWorker();
        int countW = srcParser.getCountWorkers();
        workerList = new Worker[countW];
        for(Integer i = 0; i < countW; i++){
            Integer j = i + 1;
            String configName = workerPrList.get(Lexemes.WORKER.toString()+j.toString());
            Worker MyWorker = new Worker(configName);
            workerList[i] = MyWorker;
        }
        for(Integer i = 0; i < countW - 1; i++){
            Integer j = i + 1;
            String str = workerPrList.get(Lexemes.CONSUMER.toString()+j.toString());
            str = str.replaceAll("\\D+","");
            workerList[i].setConsumer(workerList[Integer.parseInt(str) - 1]);
        }
        workerList[0].setInput(inFile);
        workerList[countW-1].setOutput(outFile);
    }
    public void closeStreams() {
        try {
            if (inFile != null)
                inFile.close();
            if (outFile != null)
                outFile.close();
        }
        catch (IOException e) {
            Log.Print("Can't close streams");
        }
    }

    public int run(){
        workerList[0].run();
        return 0;
    }
}
