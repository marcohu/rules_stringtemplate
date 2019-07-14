package org.stringtemplate.bazel;

import java.util.LinkedHashMap;
import java.util.Map;

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.ObjectModelAdaptor;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;


public class UserAdaptor
{
    public Map<Class<?>, ModelAdaptor> adaptors()
    {
        Map<Class<?>, ModelAdaptor> adaptors = new LinkedHashMap<>();
        adaptors.put(User.class, new ObjectModelAdaptor() {
            @Override
            public synchronized Object getProperty(Interpreter interp, ST self, Object o, Object property,
                    String propertyName) throws STNoSuchPropertyException {

                if ("id".equals(propertyName) && o instanceof User)
                {
                    return ((User) o).id();
                }

                return super.getProperty(interp, self, o, property, propertyName);
            }
        });
        return adaptors;
    }
}
