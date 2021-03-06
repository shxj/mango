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

package org.jfaster.mango.operator;

import org.jfaster.mango.datasource.SimpleDataSourceFactory;
import org.jfaster.mango.exception.IncorrectReturnTypeException;
import org.jfaster.mango.jdbc.GeneratedKeyHolder;
import org.jfaster.mango.reflect.MethodDescriptor;
import org.jfaster.mango.reflect.ParameterDescriptor;
import org.jfaster.mango.reflect.ReturnDescriptor;
import org.jfaster.mango.reflect.TypeToken;
import org.jfaster.mango.support.*;
import org.jfaster.mango.support.model4table.User;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author ash
 */
public class UpdateOperatorTest {

    @Test
    public void testUpdate() throws Exception {
        TypeToken<User> pt = TypeToken.of(User.class);
        TypeToken<Integer> rt = TypeToken.of(int.class);
        String srcSql = "update user set name=:1.name where id=:1.id";
        Operator operator = getOperator(pt, rt, srcSql);

        StatsCounter sc = new StatsCounter();
        operator.setStatsCounter(sc);
        operator.setJdbcOperations(new JdbcOperationsAdapter() {
            @Override
            public int update(DataSource ds, String sql, Object[] args) {
                String descSql = "update user set name=? where id=?";
                assertThat(sql, equalTo(descSql));
                assertThat(args.length, equalTo(2));
                assertThat(args[0], equalTo((Object) "ash"));
                assertThat(args[1], equalTo((Object) 100));
                return 1;
            }
        });

        User user = new User();
        user.setId(100);
        user.setName("ash");
        Object r = operator.execute(new Object[]{user});
        assertThat(r.getClass().equals(Integer.class), is(true));
    }

    @Test
    public void testUpdateReturnVoid() throws Exception {
        TypeToken<User> pt = TypeToken.of(User.class);
        TypeToken<Void> rt = TypeToken.of(void.class);
        String srcSql = "update user set name=:1.name where id=:1.id";
        Operator operator = getOperator(pt, rt, srcSql);

        StatsCounter sc = new StatsCounter();
        operator.setStatsCounter(sc);
        operator.setJdbcOperations(new JdbcOperationsAdapter() {
            @Override
            public int update(DataSource ds, String sql, Object[] args) {
                String descSql = "update user set name=? where id=?";
                assertThat(sql, equalTo(descSql));
                assertThat(args.length, equalTo(2));
                assertThat(args[0], equalTo((Object) "ash"));
                assertThat(args[1], equalTo((Object) 100));
                return 1;
            }
        });

        User user = new User();
        user.setId(100);
        user.setName("ash");
        Object r = operator.execute(new Object[]{user});
        assertThat(r, nullValue());
    }

    @Test
    public void testUpdateReturnBoolean() throws Exception {
        TypeToken<User> pt = TypeToken.of(User.class);
        TypeToken<Boolean> rt = TypeToken.of(boolean.class);
        String srcSql = "update user set name=:1.name where id=:1.id";
        Operator operator = getOperator(pt, rt, srcSql);

        StatsCounter sc = new StatsCounter();
        operator.setStatsCounter(sc);
        operator.setJdbcOperations(new JdbcOperationsAdapter() {
            @Override
            public int update(DataSource ds, String sql, Object[] args) {
                String descSql = "update user set name=? where id=?";
                assertThat(sql, equalTo(descSql));
                assertThat(args.length, equalTo(2));
                assertThat(args[0], equalTo((Object) "ash"));
                assertThat(args[1], equalTo((Object) 100));
                return 0;
            }
        });

        User user = new User();
        user.setId(100);
        user.setName("ash");
        boolean r = (Boolean) operator.execute(new Object[]{user});
        assertThat(r, is(false));
    }

    @Test
    public void testUpdateReturnGeneratedIdInt() throws Exception {
        TypeToken<User> pt = TypeToken.of(User.class);
        TypeToken<Integer> rt = TypeToken.of(int.class);
        String srcSql = "insert into user(id, name) values(:1.id, :1.name)";
        Operator operator = getOperatorReturnGeneratedId(pt, rt, srcSql);

        StatsCounter sc = new StatsCounter();
        operator.setStatsCounter(sc);
        operator.setJdbcOperations(new JdbcOperationsAdapter() {
            @Override
            public int update(DataSource ds, String sql, Object[] args, GeneratedKeyHolder holder) {
                String descSql = "insert into user(id, name) values(?, ?)";
                assertThat(sql, equalTo(descSql));
                assertThat(args.length, equalTo(2));
                assertThat(args[0], equalTo((Object) 100));
                assertThat(args[1], equalTo((Object) "ash"));
                assertThat(holder.getKeyClass().equals(int.class), is(true));
                holder.setKey(100);
                return 1;
            }
        });

        User user = new User();
        user.setId(100);
        user.setName("ash");
        Object r = operator.execute(new Object[]{user});
        assertThat(r.getClass().equals(Integer.class), is(true));
    }

    @Test
    public void testUpdateReturnGeneratedIdLong() throws Exception {
        TypeToken<User> pt = TypeToken.of(User.class);
        TypeToken<Long> rt = TypeToken.of(long.class);
        String srcSql = "insert into user(id, name) values(:1.id, :1.name)";
        Operator operator = getOperatorReturnGeneratedId(pt, rt, srcSql);

        StatsCounter sc = new StatsCounter();
        operator.setStatsCounter(sc);
        operator.setJdbcOperations(new JdbcOperationsAdapter() {
            @Override
            public int update(DataSource ds, String sql, Object[] args, GeneratedKeyHolder holder) {
                String descSql = "insert into user(id, name) values(?, ?)";
                assertThat(sql, equalTo(descSql));
                assertThat(args.length, equalTo(2));
                assertThat(args[0], equalTo((Object) 100));
                assertThat(args[1], equalTo((Object) "ash"));
                assertThat(holder.getKeyClass().equals(long.class), is(true));
                holder.setKey(100L);
                return 1;
            }
        });

        User user = new User();
        user.setId(100);
        user.setName("ash");
        Object r = operator.execute(new Object[]{user});
        assertThat(r.getClass().equals(Long.class), is(true));
    }

    @Test
    public void testStatsCounter() throws Exception {
        TypeToken<User> pt = TypeToken.of(User.class);
        TypeToken<Integer> rt = TypeToken.of(int.class);
        String srcSql = "update user set name=:1.name where id=:1.id";
        Operator operator = getOperator(pt, rt, srcSql);

        StatsCounter sc = new StatsCounter();
        operator.setStatsCounter(sc);
        operator.setJdbcOperations(new JdbcOperationsAdapter() {
            @Override
            public int update(DataSource ds, String sql, Object[] args) {
                String descSql = "update user set name=? where id=?";
                assertThat(sql, equalTo(descSql));
                assertThat(args.length, equalTo(2));
                assertThat(args[0], equalTo((Object) "ash"));
                assertThat(args[1], equalTo((Object) 100));
                return 1;
            }
        });

        User user = new User();
        user.setId(100);
        user.setName("ash");
        operator.execute(new Object[]{user});
        assertThat(sc.snapshot().getExecuteSuccessCount(), equalTo(1L));
        operator.execute(new Object[]{user});
        assertThat(sc.snapshot().getExecuteSuccessCount(), equalTo(2L));

        operator.setJdbcOperations(new JdbcOperationsAdapter());
        try {
            operator.execute(new Object[]{user});
        } catch (UnsupportedOperationException e) {
        }
        assertThat(sc.snapshot().getExecuteExceptionCount(), equalTo(1L));
        try {
            operator.execute(new Object[]{user});
        } catch (UnsupportedOperationException e) {
        }
        assertThat(sc.snapshot().getExecuteExceptionCount(), equalTo(2L));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testUpdateReturnTypeError() throws Exception {
        thrown.expect(IncorrectReturnTypeException.class);
        thrown.expectMessage("the return type of update expected one of [void, int, long, boolean, " +
                "Void, Integer, Long, Boolean] but class java.lang.String");

        TypeToken<User> pt = TypeToken.of(User.class);
        TypeToken<String> rt = TypeToken.of(String.class);
        String srcSql = "update user set name=:1.name where id=:1.id";
        Operator operator = getOperator(pt, rt, srcSql);

        StatsCounter sc = new StatsCounter();
        operator.setStatsCounter(sc);
        operator.setJdbcOperations(new JdbcOperationsAdapter() {
            @Override
            public int update(DataSource ds, String sql, Object[] args) {
                String descSql = "update user set name=? where id=?";
                assertThat(sql, equalTo(descSql));
                assertThat(args.length, equalTo(2));
                assertThat(args[0], equalTo((Object) "ash"));
                assertThat(args[1], equalTo((Object) 100));
                return 1;
            }
        });

        User user = new User();
        user.setId(100);
        user.setName("ash");
        operator.execute(new Object[]{user});
    }

    @Test
    public void testUpdateReturnGeneratedIdReturnTypeError() throws Exception {
        thrown.expect(IncorrectReturnTypeException.class);
        thrown.expectMessage("the return type of update(returnGeneratedId) expected " +
                "one of [int, long, Integer, Long] but void");

        TypeToken<User> pt = TypeToken.of(User.class);
        TypeToken<Void> rt = TypeToken.of(void.class);
        String srcSql = "insert into user(id, name) values(:1.id, :1.name)";
        Operator operator = getOperatorReturnGeneratedId(pt, rt, srcSql);

        StatsCounter sc = new StatsCounter();
        operator.setStatsCounter(sc);
        operator.setJdbcOperations(new JdbcOperationsAdapter() {
            @Override
            public int update(DataSource ds, String sql, Object[] args, GeneratedKeyHolder holder) {
                String descSql = "insert into user(id, name) values(?, ?)";
                assertThat(sql, equalTo(descSql));
                assertThat(args.length, equalTo(2));
                assertThat(args[0], equalTo((Object) 100));
                assertThat(args[1], equalTo((Object) "ash"));
                assertThat(holder.getKeyClass().equals(int.class), is(true));
                holder.setKey(100);
                return 1;
            }
        });

        User user = new User();
        user.setId(100);
        user.setName("ash");
        operator.execute(new Object[]{user});
    }


    private Operator getOperator(TypeToken<?> pt, TypeToken<?> rt, String srcSql) throws Exception {
        List<Annotation> empty = Collections.emptyList();
        ParameterDescriptor p = new ParameterDescriptor(0, pt.getType(), empty, "1");
        List<ParameterDescriptor> pds = Arrays.asList(p);

        List<Annotation> methodAnnos = new ArrayList<Annotation>();
        methodAnnos.add(new MockDB());
        methodAnnos.add(new MockSQL(srcSql));
        ReturnDescriptor rd = new ReturnDescriptor(rt.getType(), methodAnnos);
        MethodDescriptor md = new MethodDescriptor(rd, pds);

        OperatorFactory factory = new OperatorFactory(
                new SimpleDataSourceFactory(Config.getDataSource()), null, new InterceptorChain());

        Operator operator = factory.getOperator(md);
        return operator;
    }

    private Operator getOperatorReturnGeneratedId(TypeToken<?> pt, TypeToken<?> rt, String srcSql) throws Exception {
        List<Annotation> empty = Collections.emptyList();
        ParameterDescriptor p = new ParameterDescriptor(0, pt.getType(), empty, "1");
        List<ParameterDescriptor> pds = Arrays.asList(p);

        List<Annotation> methodAnnos = new ArrayList<Annotation>();
        methodAnnos.add(new MockDB());
        methodAnnos.add(new MockSQL(srcSql));
        methodAnnos.add(new MockReturnGeneratedId());
        ReturnDescriptor rd = new ReturnDescriptor(rt.getType(), methodAnnos);
        MethodDescriptor md = new MethodDescriptor(rd, pds);

        OperatorFactory factory = new OperatorFactory(
                new SimpleDataSourceFactory(Config.getDataSource()), null, new InterceptorChain());

        Operator operator = factory.getOperator(md);
        return operator;
    }

}
