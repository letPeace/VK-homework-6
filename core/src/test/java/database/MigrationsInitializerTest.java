package database;

import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;

public class MigrationsInitializerTest {

    private static final @NotNull JDBCCredentials CREDENTIALS = JDBCCredentialsTest.DEFAULT_CREDENTIALS;

    public static void initialize() {
        initialize(CREDENTIALS);
    }

    public static void initialize(@NotNull JDBCCredentials jdbcCredentials){
        final Flyway flyway = Flyway.configure()
                .dataSource(
                        jdbcCredentials.url(),
                        jdbcCredentials.login(),
                        jdbcCredentials.password()
                )
                .cleanDisabled(false)
                .locations("migrations")
                .load();
        flyway.clean();
        flyway.migrate();
    }

}
