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
package org.jdbi.v3.core.statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestEscapedCharacters {
    private ColonPrefixSqlParser parser;
    private StatementContext ctx;

    @BeforeEach
    public void setUp() {
        parser = new ColonPrefixSqlParser();
        ctx = StatementContextAccess.createContext();
    }

    private String parseString(final String src) {
        return parser.parse(src, ctx).getSql();
    }

    @Test
    public void testSimpleString() {
        assertThat(parseString("hello, world")).isEqualTo("hello, world");
    }

    @Test
    public void testSimpleSql() {
        assertThat(parseString("insert into foo (xyz) values (:bar)"))
                .isEqualTo("insert into foo (xyz) values (?)");
    }

    @Test
    public void testEscapedSql() {
        assertThat(parseString("insert into foo (xyz) values (:bar\\:\\:some_strange_type)"))
        .isEqualTo("insert into foo (xyz) values (?::some_strange_type)");
    }
}
