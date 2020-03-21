package com.company;

import org.jetbrains.annotations.NotNull;
import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Worker implements Executor {
    Executor workerCons = null;
    Consumer consumer = null;
    int blockSize = -1;
    ConfigParser parser = new ConfigParser();
    String key = null;
    Object workerF;
    DataInputStream inFile = null;
    DataOutputStream outFile = null;
    byte[] text = null;
    byte[] buf = null;

    @Override
    public @NotNull Status status() {
        return null;
    }

    @Override
    public void addProducer(@NotNull Producer producer) {

    }

    @Override
    public void addProducers(@NotNull List<Producer> list) {

    }

    @Override
    public void addConsumer(@NotNull Consumer consumer) {

    }

    @Override
    public void addConsumers(@NotNull List<Consumer> list) {

    }

    @NotNull
    public byte[] get() {
        byte[] var10000 = this.buf;

        return var10000;
    }

    @Override
    public void loadDataFrom(@NotNull Producer producer) {

    }

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
        super();
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

    public int setConsumer(Object consumer){
        try{
            workerCons = (Worker)consumer;
        }catch (Exception e){
            this.consumer = (Consumer)consumer;
        }

        return 0;
    }

    public int put(Object obj){
        text = (byte[])obj;
        return 0;
    }

    public  byte[] getText(){
        return text;
    }

    public void run(){
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
                    else if(consumer != null){
                        try {
                            Manager.setProvider(Manager.n, getText());
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        consumer.run();
                    }
                    else{
                        outFile.write(text);
                    }
                }
            } catch(IOException ex){
                Log.Print("Can't read file");
                return ;
            }
        }
        else{
            text = coder.Coder(text);
            if(workerCons != null){
                workerCons.put(text);
                workerCons.run();
            }
            else if(consumer != null){
                try {
                    Manager.setProvider(Manager.n, getText());
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                consumer.run();
            }
            else{
                try{
                    outFile.write(text);
                }catch (IOException ex){
                    Log.Print("Can't write in file");
                }
            }
        }
    }
}