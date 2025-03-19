package org.warm4ik.crud.model;

import lombok.*;
import org.warm4ik.crud.enums.Status;

import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Post {
    private Integer id;
    private String content;
    private Date created;
    private Date updated;
    private Status postStatus;
    private List<Label> labels;
    private Writer writer;
}
