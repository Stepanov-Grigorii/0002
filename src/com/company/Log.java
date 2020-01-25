package com.company;

import java.io.*;

public class Log {
    static BufferedWriter LogFile;
    Log(){
        try {
            LogFile = new BufferedWriter(new FileWriter("Log.txt"));
        }catch (IOException ex){
            System.out.println("Log file does not open");
        }
    }
    public static int Print(String str){
        try {
            LogFile.write(str+'\n');
            LogFile.flush();
        }catch (IOException ex) {
            System.out.println("Can't write in log");
        }
        return 0;
    }
    public void Close(){
        try {
            LogFile.close();
        }catch (IOException ex) {
            System.out.println("Log file does not close");
        }
    }
}