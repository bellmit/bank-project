package com.epam.bank.atm.repository;

import com.epam.bank.atm.entity.Card;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Optional;

public class JdbcCardRepository implements CardRepository {
    private final Connection connection;

    public JdbcCardRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Card> getById(long id) {
        var query = "select * from card where id = ?";
        try (var statement = this.connection.prepareStatement(query)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();

            return resultSet.next() ? Optional.of(this.mapRecordToCard(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Card> getByNumber(String number) {
        var query = "select * from card where number = ?";
        try (var statement = this.connection.prepareStatement(query)) {
            statement.setString(1, number);
            var resultSet = statement.executeQuery();

            return resultSet.next() ? Optional.of(this.mapRecordToCard(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Card mapRecordToCard(ResultSet resultSet) throws SQLException {
        return new Card(
            resultSet.getLong("id"),
            resultSet.getString("number"),
            resultSet.getLong("account_id"),
            resultSet.getString("pin_code"),
            Card.Plan.valueOf(resultSet.getString("plan")),
            resultSet.getTimestamp("expiration_date").toLocalDateTime().atZone(ZoneId.systemDefault()),
            resultSet.getBoolean("is_blocked"),
            resultSet.getInt("pin_counter")
        );
    }
}
