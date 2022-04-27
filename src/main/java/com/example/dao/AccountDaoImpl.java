package com.example.dao;

import com.example.entity.Account;
import com.example.exception.DaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDaoImpl implements AccountDao {

    private final static String INSERT_ACCOUNT_SQL =
            "insert into account(first_name, last_name, age, email) values(?,?,?,?)";
    private final static String SELECT_ACCOUNT_BY_ID_SQL =
            "SELECT * FROM account WHERE account.id = ?;";
    private final static String SELECT_ALL_ACCOUNTS_SQL = "SELECT * FROM account;";
    private final static String UPDATE_ACCOUNT_SQL =
            "UPDATE account SET first_name =?, last_name = ?, " +
                    "email = ?, birthday = ?, sex = ?, balance = ? WHERE id = ?;";


    DataSource dataSource;

    @Autowired
    public AccountDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Account account) {
        try(Connection connection = dataSource.getConnection()) {
            saveAccount(account, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void saveAccount(Account account, Connection connection){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ACCOUNT_SQL,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, account.getFirstName());
            preparedStatement.setString(2, account.getLastName());
            preparedStatement.setString(3, account.getEmail());
            preparedStatement.setInt(4, account.getAge());

            int rowsAffect = preparedStatement.executeUpdate();
            if(rowsAffect == 0){
                throw new DaoException("Account was not created");
            }

            Long id = fetchGeneratedId(preparedStatement);
            account.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    private Long fetchGeneratedId(PreparedStatement insertStatement) throws SQLException {
        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        } else {
            throw new DaoException("Can not obtain an account ID");
        }
    }

    @Override
    public Account findOne(Long id) {
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ACCOUNT_BY_ID_SQL);

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            Account account = new Account();
            account.setId(resultSet.getLong(1));
            account.setFirstName(resultSet.getString(2));
            account.setLastName(resultSet.getString(3));
            account.setAge(resultSet.getInt(4));
            account.setEmail(resultSet.getString(5));
            return account;
        } catch (SQLException e) {
            throw new DaoException("Cannor find Account by Id - ...");
        }
    }

    @Override
    public List<Account> findAll() {
        try(Connection connection = dataSource.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_ACCOUNTS_SQL);
            return collectToList(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }
    private List<Account> collectToList(ResultSet resultSet) throws SQLException {
        List<Account> list = new ArrayList<>();
        while (resultSet.next()) {
            Account account = parseRow(resultSet);
            list.add(account);
        }
        return list;
    }
    private Account parseRow(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getLong(1));
        account.setFirstName(rs.getString(2));
        account.setLastName(rs.getString(3));
        account.setAge(rs.getInt(4));
        account.setEmail(rs.getString(5));
        return account;
    }

    @Override
    public void update(Account account, Long id) {
        //Account account1 = account.findOne(id);
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement updateStatement = prepareUpdateStatement(account, connection);

            executeUpdate(updateStatement, "Account was not updated");
        } catch (SQLException e) {
            throw new DaoException(String.format("Cannot update Account with id = %d", account.getId()), e);
        }
    }
    private PreparedStatement prepareUpdateStatement(Account account, Connection connection) {
        try {
            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_ACCOUNT_SQL);
            fillStatementWithAccountData(updateStatement, account);
            updateStatement.setLong(7, account.getId());
            return updateStatement;
        } catch (SQLException e) {
            throw new DaoException(String.format("Cannot prepare update statement for account id = %d", account.getId()), e);
        }
    }
    private void executeUpdate(PreparedStatement insertStatement, String errorMessage) throws SQLException {
        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected == 0) {
            throw new DaoException(errorMessage);
        }
    }
    private PreparedStatement fillStatementWithAccountData(PreparedStatement insertStatement, Account account)
            throws SQLException {
        insertStatement.setString(1, account.getFirstName());
        insertStatement.setString(2, account.getLastName());
        insertStatement.setInt(3, account.getAge());
        insertStatement.setString(4, account.getEmail());
        return insertStatement;
    }

}

