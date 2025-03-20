package org.warm4ik.crud;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.model.Writer;
import org.warm4ik.crud.repository.WriterRepository;
import org.warm4ik.crud.service.WriterService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WriterServiceTest {

    @Mock
    private WriterRepository writerRepository;

    @InjectMocks
    private WriterService writerService;

    private static Writer testWriter;

    @BeforeAll
    static void setUp() {
        testWriter = Writer.builder()
                .id(1)
                .firstName("Test")
                .lastName("Test")
                .posts(new ArrayList<>())
                .writerStatus(Status.ACTIVE)
                .build();
    }

    @Test
    void getWriterById() {
        when(writerRepository.getById(eq(1))).thenReturn(testWriter);
        Writer writer = writerService.getWriterById(1);

        assertNotNull(writer);
        assertEquals(testWriter.getFirstName(), writer.getFirstName());
        assertEquals(testWriter.getLastName(), writer.getLastName());
        assertEquals(testWriter.getPosts(), writer.getPosts());
        assertEquals(testWriter.getWriterStatus(), writer.getWriterStatus());

        verify(writerRepository, times(1)).getById(eq(1));
    }

    @Test
    void getAllWriters() {
        List<Writer> writers = new ArrayList<>();
        writers.add(testWriter);

        when(writerRepository.getAll()).thenReturn(writers);
        List<Writer> result = writerService.getAllWriters();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(writers.get(0).getFirstName(), result.get(0).getFirstName());
        assertEquals(writers.get(0).getLastName(), result.get(0).getLastName());
        assertEquals(writers.get(0).getPosts(), result.get(0).getPosts());
        assertEquals(writers.get(0).getWriterStatus(), result.get(0).getWriterStatus());

        verify(writerRepository, times(1)).getAll();
    }

    @Test
    void saveWriter() {
        when(writerRepository.save(eq(testWriter))).thenReturn(testWriter);

        Writer writer = writerService.saveWriter(testWriter);

        assertNotNull(writer);
        assertEquals(testWriter.getFirstName(), writer.getFirstName());
        assertEquals(testWriter.getLastName(), writer.getLastName());
        assertEquals(testWriter.getPosts(), writer.getPosts());
        assertEquals(testWriter.getWriterStatus(), writer.getWriterStatus());

        verify(writerRepository, times(1)).save(eq(testWriter));
    }

    @Test
    void updateWriter() {
        Writer updatedWriter = Writer.builder()
                .id(1)
                .firstName("Updated name")
                .lastName("Updated name")
                .posts(new ArrayList<>())
                .writerStatus(Status.UNDER_REVIEW)
                .build();

        when(writerRepository.update(eq(updatedWriter))).thenReturn(updatedWriter);
        Writer writer = writerService.updateWriter(updatedWriter);

        assertNotNull(writer);
        assertEquals(updatedWriter.getFirstName(), writer.getFirstName());
        assertEquals(updatedWriter.getLastName(), writer.getLastName());
        assertEquals(updatedWriter.getPosts(), writer.getPosts());
        assertEquals(updatedWriter.getWriterStatus(), writer.getWriterStatus());

        verify(writerRepository, times(1)).update(eq(updatedWriter));
    }

    @Test
    void deleteWriter() {
        doNothing().when(writerRepository).deleteById(eq(1));
        writerService.deleteWriterById(1);

        verify(writerRepository, times(1)).deleteById(eq(1));
    }
}