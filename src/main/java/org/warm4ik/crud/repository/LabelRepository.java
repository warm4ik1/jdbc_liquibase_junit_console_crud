package org.warm4ik.crud.repository;

import org.warm4ik.crud.model.Label;

public interface LabelRepository extends GenericRepository<Label, Integer> {
    void removeLabelFromPost(Integer postId, Integer labelId);
}
