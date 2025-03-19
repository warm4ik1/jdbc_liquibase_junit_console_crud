package org.warm4ik.crud.repository.impl;

import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.exception.JdbcRepositoryException;
import org.warm4ik.crud.exception.NotFoundException;
import org.warm4ik.crud.mapper.LabelMapper;
import org.warm4ik.crud.model.Label;
import org.warm4ik.crud.repository.LabelRepository;
import org.warm4ik.crud.utils.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcLabelRepository implements LabelRepository {
    private final static String GET_BY_ID_SQL = """
            SELECT *
            FROM public.label
            WHERE id = ?
            """;
    private final static String GET_ALL_SQL = """
            SELECT *
            FROM public.label
            ORDER BY id ASC
            """;
    private final static String SAVE_SQL = """
            INSERT INTO public.label (name, status)
            VALUES(?, ?::status_enum)
            """;

    private final static String UPDATE_BY_ID_SQL = """
            UPDATE public.label
            SET name = ? , status = ?::status_enum
            WHERE id = ?
            """;
    private final static String DELETE_BY_ID_SQL = """
            UPDATE public.label
            SET status = ?::status_enum
            WHERE id = ?
            """;
    private final static String DELETE_LABEL_FROM_POST = """
            DELETE FROM post_label WHERE post_id = ? AND label_id = ?
            """;

    @Override
    public Label getById(Integer id) {
        return ConnectionManager.executeInTransactionWithResult(connection -> {
            try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(GET_BY_ID_SQL)) {
                preparedStatement.setInt(1, id);

                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return LabelMapper.mapResultSetToLabel(rs);
                } else {
                    throw new NotFoundException("Метка с данным id не существует: " + id);
                }
            } catch (SQLException e) {
                throw new JdbcRepositoryException(e.getMessage());
            }
        });
    }

    @Override
    public List<Label> getAll() {
        return ConnectionManager.executeInTransactionWithResult(connection -> {
            List<Label> labels = new ArrayList<>();
            try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(GET_ALL_SQL)) {
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    labels.add(LabelMapper.mapResultSetToLabel(rs));
                }
            } catch (SQLException e) {
                throw new JdbcRepositoryException(e.getMessage());
            }
            return labels;
        });
    }

    @Override
    public Label save(Label label) {
        return ConnectionManager.executeInTransactionWithResult(connection -> {
            try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatementWithKeys(SAVE_SQL)) {
                preparedStatement.setString(1, label.getName());
                preparedStatement.setObject(2, label.getLabelStatus().name());

                int rowCount = preparedStatement.executeUpdate();
                if (rowCount == 0) {
                    throw new JdbcRepositoryException("Создание метки не удалось!");
                }
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    label.setId(generatedKeys.getInt(1));
                } else {
                    throw new NotFoundException("Создание метки не удалось, id не удалось получить.");
                }
            } catch (SQLException e) {
                throw new JdbcRepositoryException(e.getMessage());
            }
            return label;
        });
    }

    @Override
    public Label update(Label updateLabel) {
        return ConnectionManager.executeInTransactionWithResult(connection -> {
            try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(UPDATE_BY_ID_SQL)) {
                preparedStatement.setString(1, updateLabel.getName());
                preparedStatement.setString(2, Status.UNDER_REVIEW.name());
                preparedStatement.setInt(3, updateLabel.getId());

                int countRow = preparedStatement.executeUpdate();
                if (countRow == 0) {
                    throw new JdbcRepositoryException("Не удалось обновить метку!");
                }

            } catch (SQLException e) {
                throw new JdbcRepositoryException(e.getMessage());
            }
            return updateLabel;
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
                    throw new NotFoundException("Метска с указанным id: " + id + " не найдена!");
                }

            } catch (SQLException e) {
                throw new JdbcRepositoryException(e.getMessage());
            }
        });
    }

    @Override
    public void removeLabelFromPost(Integer postId, Integer labelId) {
        ConnectionManager.executeInTransaction(connection -> {
            try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(DELETE_LABEL_FROM_POST)) {
                preparedStatement.setInt(1, postId);
                preparedStatement.setInt(2, labelId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new JdbcRepositoryException("Ошибка при удалении связи метки с постом: " + e.getMessage());
            }
        });
    }

}
