package org.warm4ik.crud.controller;

import lombok.RequiredArgsConstructor;
import org.warm4ik.crud.model.Label;
import org.warm4ik.crud.service.LabelService;

import java.util.List;

@RequiredArgsConstructor
public class LabelController {
    private final LabelService labelService;

    public Label getLabelById(int id) {
        return labelService.getLabelById(id);
    }

    public List<Label> getAllLabels() {
        return labelService.getAllLabels();
    }

    public void createLabel(Label label) {
        labelService.saveLabel(label);
    }

    public void updateLabel(Label updateLabel) {
        labelService.updateLabel(updateLabel);
    }

    public void deleteLabel(int id) {
        labelService.deleteLabel(id);
    }

    public void removeLabelFromPost(Integer postId, Integer labelId) {
        labelService.removeLabelFromPost(postId, labelId);
    }

}
