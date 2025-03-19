package org.warm4ik.crud.mapper;

import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.exception.JdbcRepositoryException;
import org.warm4ik.crud.model.Label;
import org.warm4ik.crud.model.Post;
import org.warm4ik.crud.utils.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostMapper {
    private static final String GET_LABELS_SQL = """
            SELECT *
            FROM public.label l 
            INNER JOIN public.post_label pl on l.id = pl.label_id
            WHERE pl.post_id = ?
            """;

    public static Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Integer postId = rs.getInt("id");
        Integer writerId = rs.getInt("writer_id");
        List<Label> labels = getLabelsForPost(postId);

        return Post.builder().
                id(postId).
                content(rs.getString("content")).
                created(rs.getDate("created_at")).
                updated(rs.getDate("updated_at")).
                postStatus(Status.valueOf(rs.getString("status"))).
                labels(labels).
                writer(WriterMapper.mapBasicDataForWriter(writerId, rs))
                .build();
    }

    public static List<Label> getLabelsForPost(Integer postId) {

        ArrayList<Label> labels = new ArrayList<>();

        try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(GET_LABELS_SQL)) {
            preparedStatement.setInt(1, postId);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                labels.add(LabelMapper.mapResultSetToLabel(rs));
            }
        } catch (SQLException e) {
            throw new JdbcRepositoryException(e.getMessage());
        }
        return labels;
    }
}
