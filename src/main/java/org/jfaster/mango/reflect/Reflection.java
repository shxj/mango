/*
 * Copyright 2014 mango.jfaster.org
 *
 * The Mango Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.jfaster.mango.reflect;


import org.jfaster.mango.exception.BeanInstantiationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author ash
 */
public class Reflection {

    public static <T> T instantiate(Class<T> clazz) throws BeanInstantiationException {
        if (clazz.isInterface()) {
            throw new BeanInstantiationException(clazz, "specified class is an interface");
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new BeanInstantiationException(clazz, "Is it an abstract class?", e);
        } catch (IllegalAccessException e) {
            throw new BeanInstantiationException(clazz, "Is the constructor accessible?", e);
        }
    }

    public static <T> T newProxy(
            Class<T> interfaceType, InvocationHandler handler) {
        if (!interfaceType.isInterface()) {
            throw new IllegalArgumentException("expected an interface to proxy, but " + interfaceType);
        }
        Object object = Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                handler);
        return interfaceType.cast(object);
    }


}
