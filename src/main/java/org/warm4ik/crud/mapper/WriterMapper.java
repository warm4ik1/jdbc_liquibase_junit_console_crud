package org.warm4ik.crud.mapper;

import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.model.Writer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WriterMapper {
    public static Writer mapResultSetToWriter(ResultSet rs) throws SQLException {
        return Writer.builder()
                .id(rs.getInt("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .writerStatus(Status.valueOf(rs.getString("status")))
                .build();
    }

    public static Writer mapBasicDataForWriter(Integer writerId, ResultSet rs) throws SQLException {
        return Writer.builder()
                .id(writerId)
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .build();
    }

}
