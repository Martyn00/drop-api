package com;

import com.foldermanipulation.RootFolderCreator;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@AllArgsConstructor
public class DropcoxApplication {

    public static void main(String[] args) {
        SpringApplication.run(DropcoxApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createServerFolder() {
        RootFolderCreator rootFolderCreator = new RootFolderCreator();
        rootFolderCreator.createDirectoryAboveWorkingDirectory("/server");
        rootFolderCreator.createDirectoryAboveWorkingDirectory("/server/temp");
    }
}
