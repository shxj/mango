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

package org.jfaster.mango.operator.cache;

import java.util.Map;
import java.util.Set;

/**
 * 抽象的缓存操作接口，您可以使用memcache或redis等第三方缓存实现该接口。
 *
 * @author ash
 */
public interface CacheHandler {

    public Object get(String key);

    public Map<String, Object> getBulk(Set<String> keys);

    public void set(String key, Object value, int expires);

    public void delete(Set<String> keys);

    public void delete(String key);

}
