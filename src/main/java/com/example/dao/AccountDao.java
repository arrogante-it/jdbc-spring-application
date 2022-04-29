package com.example.dao;

import com.example.entity.Account;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AccountDao {
    void save(Account account);
    void update(Account account, Long id);
    Account findOne(Long id);
    List<Account> findAll();
}

