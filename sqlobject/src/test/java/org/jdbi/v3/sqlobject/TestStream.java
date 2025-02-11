/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.sqlobject;

import java.util.stream.Stream;

import org.jdbi.v3.core.Something;
import org.jdbi.v3.core.mapper.SomethingMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;
import org.jdbi.v3.testing.junit5.JdbiExtension;
import org.jdbi.v3.testing.junit5.internal.TestingInitializers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class TestStream {

    @RegisterExtension
    public JdbiExtension h2Extension = JdbiExtension.h2().withInitializer(TestingInitializers.something()).withPlugin(new SqlObjectPlugin());

    @Test
    public void testReturnStream() {
        Something one = new Something(3, "foo");
        Something two = new Something(4, "bar");
        Something thr = new Something(5, "baz");

        Spiffy dao = h2Extension.getSharedHandle().attach(Spiffy.class);
        dao.insert(one);
        dao.insert(thr);
        dao.insert(two);

        try (Stream<Something> stream = dao.stream()) {
            assertThat(stream).containsExactly(thr, two, one);
        }

    }

    public interface Spiffy {
        @SqlQuery("select id, name from something order by id desc")
        @UseRowMapper(SomethingMapper.class)
        Stream<Something> stream();

        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insert(@BindBean Something something);
    }
}
