package org.freedesktop.Secret;

import java.util.List;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.Position;
import org.freedesktop.dbus.Struct;

public final class Secret extends Struct {
    @Position(0)
    public final DBusInterface session;
    @Position(1)
    public final List<Byte> parameters;
    @Position(2)
    public final List<Byte> value;
    @Position(3)
    public final CharSequence content_type;

    public Secret(DBusInterface session, List<Byte> parameters, List<Byte> value, CharSequence content_type) {
        this.session = session;
        this.parameters = parameters;
        this.value = value;
        this.content_type = content_type;
    }
}
