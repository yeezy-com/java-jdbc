package com.interface21.jdbc.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NamedJdbcTemplateTest {

    private final JdbcTemplate mockJdbcTemplate = mock(JdbcTemplate.class);
    private final NamedJdbcTemplate namedJdbcTemplate = new NamedJdbcTemplate(mockJdbcTemplate);

    @Captor
    private ArgumentCaptor<Object[]> captorParameters;

    @Test
    void 칼럼_매핑_순서_상관없이_매핑된다() {
        // given
        String sql = "insert into test_users (name, password) values (:name, :password)";
        when(mockJdbcTemplate.update(anyString())).thenReturn(1);
        NamedSqlParamMap params = new NamedSqlParamMap()
            .addValue("password", "password")
            .addValue("name", "test");

        // when
        namedJdbcTemplate.update(sql, params);

        // then
        verify(mockJdbcTemplate).update(anyString(), captorParameters.capture());

        Object[] capture = captorParameters.getValue();
        assertThat(capture).hasSize(2);
        assertThat(capture[0]).isEqualTo("test");
        assertThat(capture[1]).isEqualTo("password");
    }
}
