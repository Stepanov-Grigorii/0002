package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Worker implements Executor{
    Executor workerCons = null;
    int blockSize = -1;
    String key = null;
    DataInputStream inFile = null;
    DataOutputStream outFile = null;
    byte[] text = null;

    private enum Lexemes{
        BLOCK_SIZE,
        KEY
    }

    static final Map<Lexemes, String> configParam = new HashMap<>(){
        {
            put(Lexemes.BLOCK_SIZE, "BLOCK_SIZE");
            put(Lexemes.KEY, "KEY");
        }
    };

    public Worker(String fileName) {
        Properties property = new Properties();
        try{
            FileReader configReader = new FileReader(fileName);
            property.load(configReader);

            String str = configParam.get(Lexemes.KEY);

            if((key = property.getProperty(str)) == null){
                Log.Print("no " + str + " file name");
                return;
            }

            str = configParam.get(Lexemes.BLOCK_SIZE);

            if((blockSize = Integer.parseInt(property.getProperty(str))) == -1){
                Log.Print("no " + str + " file name");
                return;
            }


        }catch (IOException ex){
            Log.Print("Can't parse config file");
        }
    }

    public int setInput(DataInputStream input){
        inFile = input;
        return 0;
    }

    public int setOutput(DataOutputStream output){
        outFile = output;
        return 0;
    }

    public int setConsumer(Executor consumer){
        workerCons = consumer;
        return 0;
    }

    public int put(Object obj){
        text = (byte[])obj;
        return 0;
    }

    public int run(){
        Xor coder = new Xor(key);
        if(inFile != null){
            byte[] buf = new byte[blockSize];
            try{
                while(inFile.read(buf) != -1){
                    text = coder.Coder(buf);
                    if(workerCons != null){
                        workerCons.put(text);
                        workerCons.run();
                    }
                    else{
                        outFile.write(text);
                    }
                }
            } catch(IOException ex){
                Log.Print("Can't read file");
                return 1;
            }
        }
        else{
            text = coder.Coder(text);
            if(workerCons != null){
                workerCons.put(text);
                workerCons.run();
            }
            else{
                try{
                    outFile.write(text);
                }catch (IOException ex){
                    Log.Print("Can't write in file");
                }
            }
        }
        return 0;
    }
}