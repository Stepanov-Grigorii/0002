package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface Executor {
    int setInput(DataInputStream input);
    int setOutput(DataOutputStream output);
    int setConsumer(Executor consumer);
    int put(Object obj);
    int run();
}
