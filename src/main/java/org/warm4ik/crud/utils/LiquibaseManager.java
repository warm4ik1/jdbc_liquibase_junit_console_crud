package org.warm4ik.crud.utils;

import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;

public final class LiquibaseManager {

    public static void runLiquibase() throws Exception {
        System.out.println("Running Liquibase...");

        Connection connection = ConnectionManager.open();
        Database database = DatabaseFactory.getInstance().
                findCorrectDatabaseImplementation(new JdbcConnection(connection));

        Scope.child(Scope.Attr.resourceAccessor, new ClassLoaderResourceAccessor(), () -> {
            CommandScope update = new CommandScope("update");

            update.addArgumentValue("changelogFile",
                    "db/changelog/changelog-master.xml");

            update.addArgumentValue("database", database);

            update.execute();
        });

        System.out.println("Running Liquibase...DONE");
    }

    private LiquibaseManager() {
    }
}
