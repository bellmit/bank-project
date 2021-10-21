package com.epam.bank.atm.repository;

import com.epam.bank.atm.entity.Transaction;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JdbcTransactionRepository implements TransactionRepository {
    private final Connection connection;

    public JdbcTransactionRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Transaction transaction) {
        var query = "insert into transaction "
            + "(source_account_id, destination_account_id, amount, date_time, operation_type, state) "
            + "values (?, ?, ?, ?, ?, ?)";

        try (var statement = this.connection.prepareStatement(query)) {
            if (transaction.getSourceAccountId() == null) {
                statement.setNull(1, Types.BIGINT);
            } else {
                statement.setLong(1, transaction.getSourceAccountId());
            }

            if (transaction.getDestinationAccountId() == null) {
                statement.setNull(2, Types.BIGINT);
            } else {
                statement.setLong(2, transaction.getDestinationAccountId());
            }

            statement.setDouble(3, transaction.getAmount());
            statement.setTimestamp(4, Timestamp.valueOf(transaction.getDateTime()));
            statement.setString(5, transaction.getOperationType().toString());
            statement.setString(6, transaction.getState().toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Transaction> getById(long id) {
        var query = "select * from transaction where id = ?";
        try (var statement = this.connection.prepareStatement(query)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();

            return resultSet.next() ? Optional.of(this.mapRecordToTransaction(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Transaction> getByAccountId(long accountId) {
        var query = "select * from transaction where source_account_id = ? or destination_account_id = ?";
        try (var statement = this.connection.prepareStatement(query)) {
            statement.setLong(1, accountId);
            statement.setLong(2, accountId);

            var resultSet = statement.executeQuery();

            var transactionList = new ArrayList<Transaction>();
            while (resultSet.next()) {
                transactionList.add(this.mapRecordToTransaction(resultSet));
            }

            return transactionList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Transaction mapRecordToTransaction(ResultSet resultSet) throws SQLException {
        return new Transaction(
            resultSet.getLong("id"),
            resultSet.getObject("source_account_id", Long.class),
            resultSet.getObject("destination_account_id", Long.class),
            resultSet.getDouble("amount"),
            resultSet.getTimestamp("date_time").toLocalDateTime(),
            Transaction.OperationType.valueOf(resultSet.getString("operation_type")),
            Transaction.State.valueOf(resultSet.getString("state"))
        );
    }
}