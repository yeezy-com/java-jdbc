package com.techcourse.service;

import com.interface21.jdbc.core.TransactionTemplate;
import com.techcourse.domain.User;
import javax.sql.DataSource;

public class TransactionUserService implements UserService {

    private final TransactionTemplate transactionTemplate;
    private final UserService userService;

    public TransactionUserService(DataSource dataSource, UserService userService) {
        this.transactionTemplate = new TransactionTemplate(dataSource);
        this.userService = userService;
    }

    @Override
    public User findById(final long id) {
        return userService.findById(id);
    }

    @Override
    public void save(final User user) {
        userService.save(user);
    }

    @Override
    public void changePassword(final long id, final String newPassword, final String createdBy) {
        transactionTemplate.execute(() -> {
            userService.changePassword(id, newPassword, createdBy);
            return null;
        });
    }
}
