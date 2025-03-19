package org.warm4ik.crud;

import org.warm4ik.crud.utils.ConnectionManager;
import org.warm4ik.crud.view.MainView;

import java.sql.Connection;

import static org.warm4ik.crud.utils.LiquibaseManager.runLiquibase;

public class ApplicationRunner {
    public static void main(String[] args) {
        try (Connection connection = ConnectionManager.open()) {
            System.out.println(connection.getTransactionIsolation());
            runLiquibase();
            Thread.sleep(1000); //чтобы mainView не налез поверх загрузки liquibase.
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ApplicationContext applicationContext = new ApplicationContext();
        MainView mainView = new MainView(applicationContext);
        mainView.start();
    }
}