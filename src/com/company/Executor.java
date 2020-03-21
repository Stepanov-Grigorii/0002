package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface Executor extends ru.spbstu.pipeline.Executor {
    int setInput(DataInputStream input);
    int setOutput(DataOutputStream output);
    int setConsumer(Object consumer);
    int put(Object obj);
    void run();

}
