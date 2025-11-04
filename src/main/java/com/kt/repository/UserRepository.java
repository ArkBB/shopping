package com.kt.repository;

import com.kt.domain.Gender;
import com.kt.dto.CustomPage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.kt.domain.User;
import com.kt.dto.UserCreateRequest;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository {
	private final JdbcTemplate jdbcTemplate;

	public void save(User user) {
		// 서비스에서 dto를 도메인(비지니스모델)으로 바꾼다음 전달

		var sql = """
				INSERT INTO MEMBER (
									id,
									loginId,
									password,
									name,
									birthday,
									mobile,
									email,
									gender,
									createdAt,
									updatedAt
									) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				
				""";

		jdbcTemplate.update(
				sql,
				user.getId(),
				user.getLoginId(),
				user.getPassword(),
				user.getName(),
				user.getBirthday(),
				user.getMobile(),
				user.getEmail(),
				user.getGender().name(),
				user.getCreatedAt(),
				user.getUpdatedAt()
		);
	}

	public Long selectMaxId() {
		var sql = "SELECT MAX(id) FROM MEMBER";

		var maxId = jdbcTemplate.queryForObject(sql, Long.class);

		return maxId == null ? 0L : maxId;

	}

    public boolean existsByLoginId(String loginId) {
        var sql = "SELECT EXISTS (SELECT id FROM MEMBER WHERE loginId = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, loginId));
    }

    public void updatePassWordById(String userId, String newHashedPassword) {
        var sql = "UPDATE MEMBER SET password = ? WHERE id = ?";
        jdbcTemplate.update(sql, newHashedPassword, userId);
    }

    public Optional<User> selectById(long id) {
        var sql = "SELECT * FROM MEMBER WHERE id = ?";

        var list = jdbcTemplate.query(sql, rowMapper(), id);

        return list.stream().findFirst();
    }

    private RowMapper<User> rowMapper() {
        return (rs,rowNum) -> mapToUser(rs);
    }

    private User mapToUser(ResultSet rs) throws SQLException {

        return new User(
                rs.getLong("id"),
                rs.getString("loginId"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("mobile"),
                Gender.valueOf(rs.getString("gender")),
                rs.getObject("birthday", LocalDate.class),
                rs.getObject("createdAt", LocalDateTime.class),
                rs.getObject("updatedAt", LocalDateTime.class)
        );

    }

    public CustomPage selectAll(int page, int size) {
        var sql = "SELECT * FROM MEMBER LIMIT ? OFFSET ?";

        var users = jdbcTemplate.query(sql, rowMapper(), size, page * size);

        var countSql = "SELECT COUNT(*) FROM MEMBER";
        var totalElements = jdbcTemplate.queryForObject(countSql, Long.class);
        var pages = (int) Math.ceil((double)totalElements / size);

        return new CustomPage(
                users,
                size,
                page,
                pages,
                totalElements
        );
    }

    public CustomPage selectAll(int page, int size, String keyword) {

        var sql = "SELECT * FROM MEMBER WHERE name LIKE CONCAT('%', ? , '%') LIMIT ? OFFSET ?";

        var users = jdbcTemplate.query(sql, rowMapper(), keyword, size, page);

        var countSql = "SELECT COUNT(*) FROM MEMBER WHERE name LIKE CONCAT('%', ? , '%')";
        var totalElements = jdbcTemplate.queryForObject(countSql, Long.class,keyword);
        var pages = (int) Math.ceil((double)totalElements / size);

        return new CustomPage(
                users,
                size,
                page,
                pages,
                totalElements
        );
    }
}
