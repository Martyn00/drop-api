package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentFileDto {

    @JsonProperty(value = "id")
    private String uuid;

    @JsonProperty(value = "fileName")
    private String fileName;

    @JsonProperty(value = "path")
    private String path;

    @JsonProperty(value = "parentId")
    private String parentUuid;

    @JsonProperty(value = "addedDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime addedDate;

    @JsonProperty(value = "modifiedDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime modifiedDate;

    @JsonProperty(value = "size")
    private Double size;

    @JsonProperty(value = "fileType")
    private FileTypeDto fileType;

    @JsonProperty(value = "uploadedByUser")
    private String fileCreator;
}
