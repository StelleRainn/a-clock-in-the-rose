package com.stellerainn.backend.entity;

import lombok.Data;

@Data
public class TagFocusStats {
    private String tagName;
    private String tagColor;
    private Long totalSeconds;
}
