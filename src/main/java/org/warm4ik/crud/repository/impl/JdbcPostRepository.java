package org.warm4ik.crud.repository.impl;

import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.exception.JdbcRepositoryException;
import org.warm4ik.crud.exception.NotFoundException;
import org.warm4ik.crud.mapper.PostMapper;
import org.warm4ik.crud.model.Label;
import org.warm4ik.crud.model.Post;
import org.warm4ik.crud.repository.PostRepository;
import org.warm4ik.crud.utils.ConnectionManager;

import java.sql.*;
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
            ORDER BY p.id ASC
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
        return ConnectionManager.executeInTransactionWithResult(connection -> {
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
        });
    }

    @Override
    public List<Post> getAll() {
        return ConnectionManager.executeInTransactionWithResult(connection -> {
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
        });
    }


    @Override
    public Post save(Post post) {
        return ConnectionManager.executeInTransactionWithResult(connection -> {
            try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatementWithKeys(SAVE_SQL)) {
                preparedStatement.setString(1, post.getContent());
                preparedStatement.setTimestamp(2, new Timestamp(post.getCreated().getTime()));
                preparedStatement.setTimestamp(3, new Timestamp(post.getUpdated().getTime()));
                preparedStatement.setString(4, post.getPostStatus().name());
                preparedStatement.setLong(5, post.getWriter().getId());

                int rowCount = preparedStatement.executeUpdate();

                if (rowCount == 0) {
                    throw new JdbcRepositoryException("Создание поста не удалось.");
                }

                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    post.setId(rs.getInt(1));
                } else {
                    throw new NotFoundException("Не удалось получить ID поста.");
                }

                addLabelToPost(post.getId(), post.getLabels(), connection);

                return post;
            } catch (SQLException e) {
                throw new JdbcRepositoryException("Ошибка при создании поста", e);
            }
        });
    }

    @Override
    public Post update(Post updatePost) {
        return ConnectionManager.executeInTransactionWithResult(connection -> {
            try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_BY_ID_SQL)) {

                updateStatement.setString(1, updatePost.getContent());
                updateStatement.setTimestamp(2, new Timestamp(updatePost.getUpdated().getTime()));
                updateStatement.setString(3, Status.UNDER_REVIEW.name());
                updateStatement.setLong(4, updatePost.getId());
                updateStatement.executeUpdate();

                deleteLabelsForPost(updatePost.getId(), connection);

                addLabelToPost(updatePost.getId(), updatePost.getLabels(), connection);

                return updatePost;
            } catch (SQLException e) {
                throw new JdbcRepositoryException("Ошибка при обновлении поста", e);
            }
        });
    }

    @Override
    public void deleteById(Integer id) {
        ConnectionManager.executeInTransaction(connection -> {
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
        });
    }

    private void addLabelToPost(Integer postId, List<Label> labels, Connection connection) {
        if (labels == null) return;

        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_POST_LABEL_SQL)) {
            for (Label label : labels) {
                preparedStatement.setLong(1, postId);
                preparedStatement.setLong(2, label.getId());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new JdbcRepositoryException(e.getMessage());
        }
    }

    private void deleteLabelsForPost(Integer postId, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_POST_LABEL_SQL)) {
            preparedStatement.setInt(1, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcRepositoryException(e.getMessage());
        }
    }
}
