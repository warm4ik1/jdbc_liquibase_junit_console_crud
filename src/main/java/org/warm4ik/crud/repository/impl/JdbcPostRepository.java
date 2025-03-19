package org.warm4ik.crud.repository.impl;

import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.exception.JdbcRepositoryException;
import org.warm4ik.crud.exception.NotFoundException;
import org.warm4ik.crud.mapper.PostMapper;
import org.warm4ik.crud.model.Label;
import org.warm4ik.crud.model.Post;
import org.warm4ik.crud.repository.PostRepository;
import org.warm4ik.crud.utils.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class JdbcPostRepository implements PostRepository {
    private final static String GET_BY_ID_SQL = """
            SELECT p.id, p.content, p.created_at, p.updated_at, p.status::status_enum, p.writer_id,
            l.id, l.name, l.status::status_enum,
            w.first_name, w.last_name, w.status::status_enum
            FROM public.post p
            LEFT JOIN public.post_label as pl on p.id = pl.post_id
            LEFT JOIN public.label as l on l.id = pl.label_id
            LEFT JOIN public.writer as w on w.id = p.writer_id
            WHERE p.id = ?
            """;
    private final static String GET_ALL_SQL = """
            SELECT p.id, p.content, p.created_at, p.updated_at, p.status::status_enum, p.writer_id,
            l.id, l.name, l.status::status_enum,
            w.id, w.first_name, w.last_name, w.status::status_enum
            FROM public.post p
            LEFT JOIN public.post_label pl on p.id = pl.post_id
            LEFT JOIN public.label l on l.id = pl.label_id
            LEFT JOIN public.writer w on p.writer_id = w.id
            """;
    private final static String SAVE_SQL = """
            INSERT INTO public.post (content, created_at, updated_at, status, writer_id)
            VALUES (?, ?, ?, ?::status_enum, ?)
            """;
    private final static String UPDATE_BY_ID_SQL = """
            UPDATE public.post
            SET content = ?, updated_at = ?, status = ?::status_enum
            WHERE id = ?
            """;
    private final static String DELETE_BY_ID_SQL = """
            UPDATE public.post
            SET status = ?::status_enum
            WHERE ID = ?
            """;

    private static final String INSERT_POST_LABEL_SQL = """
            INSERT INTO post_label (post_id, label_id) VALUES (?, ?)
            """;
    private static final String DELETE_POST_LABEL_SQL = """
            DELETE FROM post_label WHERE post_id = ?
            """;


    @Override
    public Post getById(Integer id) {
        try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(GET_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();
            if (!rs.next()) {
                throw new NotFoundException("Поста с данным id не существует.");
            }
            return PostMapper.mapResultSetToPost(rs);
        } catch (SQLException e) {
            throw new JdbcRepositoryException(e.getMessage());
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(GET_ALL_SQL)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                posts.add(PostMapper.mapResultSetToPost(rs));
            }
        } catch (SQLException e) {
            throw new JdbcRepositoryException(e.getMessage());
        }
        return posts;
    }


    @Override
    public Post save(Post post) {
        try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatementWithKeys(SAVE_SQL)) {
            preparedStatement.setString(1, post.getContent());
            preparedStatement.setTimestamp(2, new Timestamp(post.getCreated().getTime()));
            preparedStatement.setTimestamp(3, new Timestamp(post.getUpdated().getTime()));
            preparedStatement.setString(4, post.getPostStatus().name());
            preparedStatement.setLong(5, post.getWriter().getId());

            int rowCount = preparedStatement.executeUpdate();

            if (rowCount == 0) {
                throw new JdbcRepositoryException("Создание поста не удалось, ни одна запись не была изменена.");
            }

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                post.setId(rs.getInt(1));
            } else {
                throw new NotFoundException("Не удалось получить id, создание поста не удалось.");
            }
        } catch (SQLException e) {
            throw new JdbcRepositoryException(e.getMessage());
        }
        return post;
    }

    @Override
    public Post update(Post updatePost) {
        try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatementWithKeys(UPDATE_BY_ID_SQL)) {
            preparedStatement.setString(1, updatePost.getContent());
            preparedStatement.setTimestamp(2, new Timestamp(updatePost.getUpdated().getTime()));
            preparedStatement.setString(3, Status.UNDER_REVIEW.name());
            preparedStatement.setLong(4, updatePost.getId());

            int rowCount = preparedStatement.executeUpdate();

            if (rowCount == 0) {
                throw new JdbcRepositoryException("Обновление поста не удалось, ни одна запись не была изменена.");
            }

            deleteLabelsForPost(updatePost.getId());
            addLabelToPost(updatePost.getId(), updatePost.getLabels());

        } catch (SQLException e) {
            throw new JdbcRepositoryException("Ошибка выполнения SQL-запроса", e);
        }
        return updatePost;
    }

    @Override
    public void deleteById(Integer id) {
        try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(DELETE_BY_ID_SQL)) {
            preparedStatement.setString(1, Status.DELETED.name());
            preparedStatement.setInt(2, id);

            int rowCount = preparedStatement.executeUpdate();
            if (rowCount == 0) {
                throw new NotFoundException("Невозможно удалить несуществующий пост, id не найден.");
            }
        } catch (SQLException e) {
            throw new JdbcRepositoryException(e.getMessage());
        }
    }

    private void addLabelToPost(Integer postId, List<Label> labels) {
        if (labels == null) {
            return;
        }

        try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(INSERT_POST_LABEL_SQL)) {
            for (Label label : labels) {
                if (label.getId() != null) {
                    preparedStatement.setLong(1, postId);
                    preparedStatement.setLong(2, label.getId());

                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new JdbcRepositoryException(e.getMessage());
        }
    }

    private void deleteLabelsForPost(Integer postId) {
        try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(DELETE_POST_LABEL_SQL)) {
            preparedStatement.setInt(1, postId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcRepositoryException(e.getMessage());
        }
    }
}
