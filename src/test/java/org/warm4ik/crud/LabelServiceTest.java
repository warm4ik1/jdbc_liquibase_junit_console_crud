package org.warm4ik.crud;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.warm4ik.crud.enums.Status;
import org.warm4ik.crud.model.Label;
import org.warm4ik.crud.repository.LabelRepository;
import org.warm4ik.crud.service.LabelService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {

    @InjectMocks
    private LabelService labelService;

    @Mock
    private LabelRepository labelRepository;

    private static Label testLabel;

    @BeforeAll
    static void setUp() {
        testLabel = Label.builder()
                .id(1)
                .name("Test")
                .labelStatus(Status.ACTIVE)
                .build();
    }

    @Test
    void getLabelById() {
        when(labelRepository.getById(eq(1))).thenReturn(testLabel);
        Label label = labelService.getLabelById(1);

        assertNotNull(label);
        assertEquals(testLabel.getName(), label.getName());
        assertEquals(Status.ACTIVE, label.getLabelStatus());

        verify(labelRepository, times(1)).getById(eq(1));
    }

    @Test
    void getAllLabels() {
        List<Label> labels = new ArrayList<>();
        labels.add(testLabel);
        when(labelRepository.getAll()).thenReturn(labels);
        List<Label> result = labelService.getAllLabels();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(labels.get(0).getName(), result.get(0).getName());
        assertEquals(labels.get(0).getLabelStatus(), result.get(0).getLabelStatus());

        verify(labelRepository, times(1)).getAll();
    }

    @Test
    void saveLabel() {
        when(labelRepository.save(eq(testLabel))).thenReturn(testLabel);

        Label label = labelService.saveLabel(testLabel);

        assertNotNull(label);
        assertEquals(testLabel.getName(), label.getName());
        assertEquals(testLabel.getLabelStatus(), label.getLabelStatus());

        verify(labelRepository, times(1)).save(eq(testLabel));
    }

    @Test
    void updateLabel() {
        Label updatedLabel = Label.builder()
                .id(1)
                .name("Test name updated")
                .labelStatus(Status.ACTIVE)
                .build();

        when(labelRepository.update(eq(updatedLabel))).thenReturn(updatedLabel);
        Label label = labelService.updateLabel(updatedLabel);

        assertNotNull(label);
        assertEquals("Test name updated", label.getName());
        assertEquals(Status.ACTIVE, label.getLabelStatus());

        verify(labelRepository, times(1)).update(eq(updatedLabel));
    }

    @Test
    void deleteLabel() {
        doNothing().when(labelRepository).deleteById(eq(1));
        labelService.deleteLabel(1);

        verify(labelRepository, times(1)).deleteById(eq(1));
    }
}