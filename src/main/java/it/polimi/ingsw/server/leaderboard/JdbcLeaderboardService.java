package it.polimi.ingsw.server.leaderboard;

import it.polimi.ingsw.common.network.dto.LeaderboardEntryDto;
import it.polimi.ingsw.common.network.dto.LeaderboardSnapshotDto;
import it.polimi.ingsw.server.model.CompletedGameResult;
import it.polimi.ingsw.server.model.PlayerGameResult;
import it.polimi.ingsw.server.model.exception.LeaderboardStorageException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Represents the jdbc leaderboard service component. */
public class JdbcLeaderboardService implements LeaderboardService {
    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS leaderboard_results (
                id BIGSERIAL PRIMARY KEY,
                nickname VARCHAR(64) NOT NULL,
                player_count SMALLINT NOT NULL CHECK (player_count BETWEEN 2 AND 5),
                final_score INTEGER NOT NULL,
                remaining_food INTEGER NOT NULL,
                played_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;

    private static final String INSERT_RESULT_SQL = """
            INSERT INTO leaderboard_results (
                nickname,
                player_count,
                final_score,
                remaining_food,
                played_at
            )
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String FULL_LEADERBOARD_SQL = """
              SELECT
                  DENSE_RANK() OVER (
                      ORDER BY final_score DESC, remaining_food DESC
                  ) AS position,
                  nickname,
                  final_score,
                  remaining_food,
                  played_at
              FROM leaderboard_results
              WHERE player_count = ?
              ORDER BY position, played_at, nickname
              """;

    private static final String PERSONAL_BEST_LEADERBOARD_SQL = """
          WITH personal_best AS (
              SELECT DISTINCT ON (nickname)
                  nickname,
                  final_score,
                  remaining_food,
                  played_at
              FROM leaderboard_results
              WHERE player_count = ?
              ORDER BY nickname, final_score DESC, remaining_food DESC, played_at ASC
          ),
          ranked AS (
              SELECT
                  DENSE_RANK() OVER (
                      ORDER BY final_score DESC, remaining_food DESC
                  ) AS position,
                  nickname,
                  final_score,
                  remaining_food,
                  played_at
              FROM personal_best
          )
          SELECT position, nickname, final_score, remaining_food, played_at
          FROM ranked
          ORDER BY position, played_at, nickname
          """;

    private final String url;
    private final String user;
    private final String password;

    /**
     * Creates a new {@code JdbcLeaderboardService} instance.
     *
     * @param url database URL
     * @param user database user
     * @param password database password
     */
    public JdbcLeaderboardService(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        initializeSchema();
    }

    /** {@inheritDoc} */
    @Override
    public List<LeaderboardEntryDto> recordCompletedGame(CompletedGameResult result) {
        LocalDateTime playedAt = LocalDateTime.now();
        int playerCount = result.playerResults().size();
        Set<String> completedPlayerNicknames = new LinkedHashSet<>();

        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try {
                insertCompletedGame(connection, result, playerCount, playedAt, completedPlayerNicknames);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new LeaderboardStorageException("Unable to record completed game leaderboard results.", e);
        }

        return personalBestLeaderboardEntries(playerCount).stream()
                .filter(entry -> completedPlayerNicknames.contains(entry.nickname()))
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public LeaderboardSnapshotDto getLeaderboard(int playerCount) {
        return new LeaderboardSnapshotDto(playerCount, fullLeaderboardEntries(playerCount));
    }


    private void initializeSchema() {
        try (Connection connection = openConnection();
             PreparedStatement createTable = connection.prepareStatement(CREATE_TABLE_SQL)) {
            createTable.executeUpdate();
        } catch (SQLException e) {
            throw new LeaderboardStorageException("Unable to initialize leaderboard database schema.", e);
        }
    }

    private void insertCompletedGame(
            Connection connection,
            CompletedGameResult result,
            int playerCount,
            LocalDateTime playedAt,
            Set<String> completedPlayerNicknames
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_RESULT_SQL)) {
            for (PlayerGameResult playerResult : result.playerResults()) {
                completedPlayerNicknames.add(playerResult.nickname());
                statement.setString(1, playerResult.nickname());
                statement.setInt(2, playerCount);
                statement.setInt(3, playerResult.finalScore());
                statement.setInt(4, playerResult.remainingFood());
                statement.setTimestamp(5, Timestamp.valueOf(playedAt));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private List<LeaderboardEntryDto> fullLeaderboardEntries(int playerCount) {
        return readLeaderboardEntries(FULL_LEADERBOARD_SQL, playerCount);
    }

    private List<LeaderboardEntryDto> personalBestLeaderboardEntries(int playerCount) {
        return readLeaderboardEntries(PERSONAL_BEST_LEADERBOARD_SQL, playerCount);
    }

    private List<LeaderboardEntryDto> readLeaderboardEntries(String sql, int playerCount) {
        List<LeaderboardEntryDto> entries = new ArrayList<>();

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, playerCount);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    entries.add(new LeaderboardEntryDto(
                            resultSet.getInt("position"),
                            resultSet.getString("nickname"),
                            resultSet.getInt("final_score"),
                            resultSet.getInt("remaining_food"),
                            resultSet.getTimestamp("played_at").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new LeaderboardStorageException("Unable to read leaderboard results.", e);
        }

        return entries;
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}