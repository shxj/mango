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

package org.jfaster.mango.invoker;

import org.jfaster.mango.exception.UncheckedException;
import org.jfaster.mango.util.concurrent.cache.CacheLoader;
import org.jfaster.mango.util.concurrent.cache.DoubleCheckCache;
import org.jfaster.mango.util.concurrent.cache.LoadingCache;

import javax.annotation.Nullable;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author ash
 */
public class InvokerCache {

    @Nullable
    public static GetterInvoker getGetterInvoker(Class<?> clazz, String propertyName) {
        return cache.get(clazz).getGetterInvoker(propertyName);
    }

    public static List<GetterInvoker> getGetterInvokers(Class<?> clazz) {
        return cache.get(clazz).getGetterInvokers();
    }

    public static List<SetterInvoker> getSetterInvokers(Class<?> clazz) {
        return cache.get(clazz).getSetterInvokers();
    }

    private final static LoadingCache<Class<?>, BeanInfo> cache = new DoubleCheckCache<Class<?>, BeanInfo>(
            new CacheLoader<Class<?>, BeanInfo>() {
                public BeanInfo load(Class<?> clazz) {
                    try {
                        return new BeanInfo(clazz);
                    } catch (Exception e) {
                        throw new UncheckedException(e.getMessage(), e);
                    }
                }
            });

    private static class BeanInfo {

        private final Map<String, GetterInvoker> getterInvokerMap;
        private final Map<String, SetterInvoker> setterInvokerMap;
        private final List<GetterInvoker> getterInvokers;
        private final List<SetterInvoker> setterInvokers;

        public BeanInfo(Class<?> clazz) throws Exception {
            Map<String, GetterInvoker> gim = new HashMap<String, GetterInvoker>();
            Map<String, SetterInvoker> sim = new HashMap<String, SetterInvoker>();
            List<GetterInvoker> gis = new ArrayList<GetterInvoker>();
            List<SetterInvoker> sis = new ArrayList<SetterInvoker>();

            java.beans.BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                if (!Class.class.equals(pd.getPropertyType())) {
                    String name = pd.getName();
                    Method readMethod = pd.getReadMethod();
                    if (readMethod != null) {
                        FunctionalGetterInvoker gi = FunctionalGetterInvoker.create(name, readMethod);
                        gim.put(name, gi);
                        gis.add(gi);
                    }
                    Method writeMethod = pd.getWriteMethod();
                    if (writeMethod != null) {
                        FunctionalSetterInvoker si = FunctionalSetterInvoker.create(name, writeMethod);
                        sim.put(name, si);
                        sis.add(si);
                    }
                }
            }

            getterInvokerMap = Collections.unmodifiableMap(gim);
            setterInvokerMap = Collections.unmodifiableMap(sim);
            getterInvokers = Collections.unmodifiableList(gis);
            setterInvokers = Collections.unmodifiableList(sis);
        }

        GetterInvoker getGetterInvoker(String propertyName) {
            return getterInvokerMap.get(propertyName);
        }

        SetterInvoker getSetterInvoker(String propertyName) {
            return setterInvokerMap.get(propertyName);
        }

        private List<GetterInvoker> getGetterInvokers() {
            return getterInvokers;
        }

        private List<SetterInvoker> getSetterInvokers() {
            return setterInvokers;
        }
    }

}
