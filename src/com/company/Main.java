package com.company;

public class Main {

    public static void main(String[] args) {
        try {
            Log lg = new Log();
            if(args.length == 1) {
                Manager manager = new Manager(args[0]);
                manager.run();
                manager.closeStreams();
            } else
                lg.Print("Wrong amount of arguments");
            lg.Close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}