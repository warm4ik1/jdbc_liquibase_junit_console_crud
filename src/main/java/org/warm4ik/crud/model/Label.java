package org.warm4ik.crud.model;

import lombok.*;
import org.warm4ik.crud.enums.Status;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Label {
    private Integer id;
    private String name;
    private Status labelStatus;
}
