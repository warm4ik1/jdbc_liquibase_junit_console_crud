package org.warm4ik.crud.service;

import lombok.RequiredArgsConstructor;
import org.warm4ik.crud.model.Writer;
import org.warm4ik.crud.repository.WriterRepository;

import java.util.List;

@RequiredArgsConstructor
public class WriterService {
    private final WriterRepository writerRepository;

    public void deleteWriterById(Integer id) {
        writerRepository.deleteById(id);
    }

    public Writer updateWriter(Writer updatedWriter) {
        return writerRepository.update(updatedWriter);
    }

    public List<Writer> getAllWriters() {
        return writerRepository.getAll();
    }

    public Writer getWriterById(Integer id) {
        return writerRepository.getById(id);
    }

    public Writer saveWriter(Writer createWriter) {
        return writerRepository.save(createWriter);
    }
}
