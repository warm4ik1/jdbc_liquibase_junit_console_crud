package org.warm4ik.crud.controller;

import lombok.RequiredArgsConstructor;
import org.warm4ik.crud.model.Writer;
import org.warm4ik.crud.repository.WriterRepository;
import org.warm4ik.crud.service.WriterService;

import java.util.List;

@RequiredArgsConstructor
public class WriterController {
    private final WriterService writerService;

    public void deleteWriterById(Integer id) {
        writerService.deleteWriterById(id);
    }

    public void updateWriter(Writer updatedWriter) {
        writerService.updateWriter(updatedWriter);
    }

    public List<Writer> getAllWriters() {
        return writerService.getAllWriters();
    }

    public Writer getWriterById(Integer id) {
        return writerService.getWriterById(id);
    }

    public void saveWriter(Writer createWriter) {
        writerService.saveWriter(createWriter);
    }
}
