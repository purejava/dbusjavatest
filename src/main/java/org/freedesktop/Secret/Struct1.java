package org.freedesktop.Secret;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Struct;

import java.util.List;

public final class Struct1 extends Struct {
    @Position(0)
    public final DBusInterface a;
    @Position(1)
    public final List<Byte> b;
    @Position(2)
    public final List<Byte> c;
    @Position(3)
    public final String d;

    public Struct1(DBusInterface a, List<Byte> b, List<Byte> c, String d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
}
