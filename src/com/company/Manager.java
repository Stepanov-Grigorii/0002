package com.company;

import Rizhenko.XOR;
import org.jetbrains.annotations.NotNull;
import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Manager {
    DataInputStream inFile = null;
    DataOutputStream outFile = null;

    private enum  Lexemes {
        CONSUMER,
        WORKER
    }
    static Object[] workerList;
    static int n;
    Class<?> clazz;
    Object worker;
    String[] splitConfigName;
    Logger logger = new Logger() {
        @Override
        public void log(@NotNull String s) {

        }

        @Override
        public void log(@NotNull String s, @NotNull Throwable throwable) {

        }

        @Override
        public void log(@NotNull Level level, @NotNull String s) {

        }

        @Override
        public void log(@NotNull Level level, @NotNull String s, @NotNull Throwable throwable) {

        }
    };

    static Map<String,String>workerPrList = new HashMap<>(){
        {
            put(Lexemes.CONSUMER.toString(), "CONSUMER");
            put(Lexemes.WORKER.toString(), "WORKER");
        }
    };
    Manager(String configFileName) throws Exception {
        FileReader srcReader = null;
        ConfigParser parser = new ConfigParser();
        String lg = parser.Parse(configFileName);

        if(lg == null){
            throw new Exception("Not correct config file.");
        }

        try {
            inFile = new DataInputStream(new FileInputStream(parser.fileNames.get(ConfigParser.configParam.get(ConfigParser.Lexemes.IN))));
            outFile = new DataOutputStream(new FileOutputStream(parser.fileNames.get(ConfigParser.configParam.get(ConfigParser.Lexemes.OUT))));
        }
        catch (IOException ex){
            Log.Print("Can't open file");
            return;
        }
        SrcParser srcParser = new SrcParser(parser.fileNames.get(ConfigParser.configParam.get(ConfigParser.Lexemes.SRC)));
        srcParser.Parse(parser.fileNames.get(ConfigParser.configParam.get(ConfigParser.Lexemes.SRC)));
        workerPrList = srcParser.getWorker();
        int countW = srcParser.getCountWorkers();
        workerList = new Object[countW];
        for(Integer i = 0; i < countW; i++){
            Integer j = i + 1;
            clazz = findClass(j);
            try{
                try {
                    worker = clazz.getConstructor(String.class).newInstance(splitConfigName[1]);
                    workerList[i] = worker;
                }catch (Exception ex){
                    worker = clazz.getConstructor(String.class, Logger.class).newInstance(splitConfigName[1], logger);
                    workerList[i] = worker;
                    setProvider(n, splitConfigName[1].getBytes());
                    n = i;
                }

            }catch (Exception ex){
                System.out.println(ex);
                return;
            }
        }

        for(Integer i = 0; i < countW-1; i++){
            Integer j = i + 1;
            clazz = findClass(j);
            String str = workerPrList.get(Lexemes.CONSUMER.toString()+j.toString());
            str = str.replaceAll("\\D+","");
            for(Method m : clazz.getDeclaredMethods()){
                if(m.getName().equals("setConsumer"))
                    m.invoke(workerList[i], workerList[Integer.parseInt(str) - 1]);
            }
        }

        clazz = findClass(1);
        for(Method m : clazz.getDeclaredMethods()){
            if(m.getName().equals("setInput"))
                m.invoke(workerList[0], inFile);
        }

        clazz = findClass(countW);
        for(Method m : clazz.getDeclaredMethods()){
            if(m.getName().equals("setOutput"))
                m.invoke(workerList[countW - 1], outFile);
        }

        clazz = findClass(n + 1);
        for(Method m : clazz.getDeclaredMethods()){
            if(m.getName().equals("addConsumer"))
                m.invoke(workerList[n], new Consumer() {
                    byte[] buf;
                    @Override
                    public void loadDataFrom(ru.spbstu.pipeline.@NotNull Producer producer) {
                        this.buf = producer.get();
                    }

                    @Override
                    public void run() {
                        if (n == countW - 1) {
                            try {
                                outFile.write(this.buf);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else{
                            try {
                                findClass(n+1);
                                for(Method m : clazz.getDeclaredMethods()){
                                    if(m.getName().equals("put"))
                                        m.invoke(workerList[n + 1], this.buf);
                                }
                                for(Method m : clazz.getDeclaredMethods()){
                                    if(m.getName().equals("run"))
                                        m.invoke(workerList[n + 1]);
                                }
                            } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public @NotNull Status status() {
                        return null;
                    }
                });
        }
    }

    public Class<?> findClass(int count) throws ClassNotFoundException {
        Integer i = count;
        Class<?> clazz;
        String configName = workerPrList.get(Lexemes.WORKER.toString()+i.toString());
        splitConfigName = configName.split("\\s+");
        clazz = Class.forName(splitConfigName[0]);

        return clazz;
    }


    static public void setProvider(int i, byte[] buf) throws NoSuchFieldException, IllegalAccessException {
        Field f;
        f = workerList[i].getClass().getDeclaredField("buf");
        f.setAccessible(true);
        f.set(workerList[i], buf);
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

    public int run()throws Exception{
        clazz = findClass(1);

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals("run"))
                m.invoke(workerList[0]);
        }

        return 0;
    }
}
