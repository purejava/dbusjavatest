package com.github.purejava;

public class JavaReceive implements ReceiveInterface {
    public int echoMessage(String str) {
        System.out.println(str);
        return 0;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public String getObjectPath() {
        return null;
    }
}
