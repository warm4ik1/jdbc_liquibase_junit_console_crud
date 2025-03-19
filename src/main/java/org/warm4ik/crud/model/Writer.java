package org.warm4ik.crud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.warm4ik.crud.enums.Status;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Writer {
    private Integer id;
    private String firstName;
    private String lastName;
    private Status writerStatus;
    private List<Post> posts;
}
