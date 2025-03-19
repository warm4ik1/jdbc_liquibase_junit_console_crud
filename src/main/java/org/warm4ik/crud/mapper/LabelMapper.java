package org.warm4ik.crud.mapper;

import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.model.Label;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LabelMapper {
    public static Label mapResultSetToLabel(ResultSet resultSet) throws SQLException {
        return Label.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .labelStatus(Status.valueOf(resultSet.getString("status")))
                .build();
    }
}
