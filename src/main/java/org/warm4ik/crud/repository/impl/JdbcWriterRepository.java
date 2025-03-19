package org.warm4ik.crud.repository.impl;

import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.exception.JdbcRepositoryException;
import org.warm4ik.crud.exception.NotFoundException;
import org.warm4ik.crud.mapper.WriterMapper;
import org.warm4ik.crud.model.Post;
import org.warm4ik.crud.model.Writer;
import org.warm4ik.crud.repository.WriterRepository;
import org.warm4ik.crud.utils.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcWriterRepository implements WriterRepository {
    private final static String GET_BY_ID_SQL = """
            SELECT *
            FROM public.writer
            WHERE id = ?
            """;
    private final static String GET_ALL_SQL = """
            SELECT *
            FROM public.writer
            ORDER BY id ASC
            """;
    private final static String SAVE_WRITER_SQL = """
            INSERT INTO public.writer
            (first_name, last_name, status)
            VALUES (?, ?, ?::status_enum)
            """;
    private final static String UPDATE_BY_ID_SQL = """
            UPDATE public.writer
            SET first_name = ?, last_name = ?, status = ?::status_enum
            WHERE id = ?
            """;
    private final static String DELETE_BY_ID_SQL = """
            UPDATE public.writer
            SET status = ?::status_enum
            WHERE id = ?
            """;

    @Override
    public Writer getById(Integer id) {
        return ConnectionManager.executeInTransactionWithResult(connection -> {
            try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(GET_BY_ID_SQL)) {
                preparedStatement.setInt(1, id);

                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return WriterMapper.mapResultSetToWriter(rs);
                } else {
                    throw new NotFoundException("Писатель с таким id не найден.");
                }
            } catch (SQLException e) {
                throw new JdbcRepositoryException(e.getMessage());
            }
        });
    }

    @Override
    public List<Writer> getAll() {
        return ConnectionManager.executeInTransactionWithResult(connection -> {
            List<Writer> writers = new ArrayList<>();
            try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(GET_ALL_SQL)) {

                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    writers.add(WriterMapper.mapResultSetToWriter(rs));
                }
                return writers;
            } catch (SQLException e) {
                throw new JdbcRepositoryException(e.getMessage());
            }
        });
    }

    @Override
    public Writer save(Writer writer) {
        return ConnectionManager.executeInTransactionWithResult(connection -> {
            try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatementWithKeys(SAVE_WRITER_SQL)) {
                preparedStatement.setString(1, writer.getFirstName());
                preparedStatement.setString(2, writer.getLastName());
                preparedStatement.setString(3, writer.getWriterStatus().name());

                int rowCount = preparedStatement.executeUpdate();
                if (rowCount == 0) {
                    throw new JdbcRepositoryException("Не удалось сохранить пользователя");
                }

                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    writer.setId(rs.getInt(1));
                } else {
                    throw new JdbcRepositoryException("Создание писателя не удалось, получить id не удалось");
                }
            } catch (SQLException e) {
                throw new JdbcRepositoryException(e.getMessage());
            }
            return writer;
        });
    }

    @Override
    public Writer update(Writer updateWriter) {
        return ConnectionManager.executeInTransactionWithResult(connection -> {
            try (PreparedStatement preparedStatement = ConnectionManager.getPreparedStatement(UPDATE_BY_ID_SQL)) {
                preparedStatement.setString(1, updateWriter.getFirstName());
                preparedStatement.setString(2, updateWriter.getLastName());
                preparedStatement.setString(3, Status.UNDER_REVIEW.name());
                preparedStatement.setLong(4, updateWriter.getId());

                int rowCount = preparedStatement.executeUpdate();
                if (rowCount == 0) {
                    throw new JdbcRepositoryException("Обновление писателя не удалось, ни одна запись не была изменена.");
                }
            } catch (SQLException e) {
                throw new JdbcRepositoryException("Ошибка выполнения SQL-запроса", e);
            }
            return updateWriter;
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
                    throw new NotFoundException("Id не найден. Писатель не может быть удалён.");
                }
            } catch (SQLException e) {
                throw new JdbcRepositoryException(e.getMessage());
            }

        });
    }
}
