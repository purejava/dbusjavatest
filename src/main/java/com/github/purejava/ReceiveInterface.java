package com.github.purejava;

import org.freedesktop.dbus.DBusInterface;

public interface ReceiveInterface extends DBusInterface {
    public int echoMessage(String str);

}
