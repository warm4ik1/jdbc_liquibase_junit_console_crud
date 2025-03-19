package org.warm4ik.crud.service;

import lombok.RequiredArgsConstructor;
import org.warm4ik.crud.model.Label;
import org.warm4ik.crud.repository.LabelRepository;

import java.util.List;

@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;

    public Label getLabelById(int id) {
        return labelRepository.getById(id);
    }

    public List<Label> getAllLabels() {
        return labelRepository.getAll();
    }

    public Label saveLabel(Label label) {
        return labelRepository.save(label);
    }

    public Label updateLabel(Label label) {
        return labelRepository.update(label);
    }

    public void deleteLabel(int id) {
        labelRepository.deleteById(id);
    }

    public void removeLabelFromPost(Integer postId, Integer labelId) {
        labelRepository.removeLabelFromPost(postId, labelId);
    }

}
