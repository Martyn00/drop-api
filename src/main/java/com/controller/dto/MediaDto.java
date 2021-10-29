package com.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MediaDto {
    String path;
    MediaType mediaType;
}
