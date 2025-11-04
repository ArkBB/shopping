package com.kt.dto;

import com.kt.domain.User;
import java.util.List;

public record CustomPage(
        List<User> users,
        int size,
        int page,
        int pages, //페이지 개수
        long totalElements
) {

}
