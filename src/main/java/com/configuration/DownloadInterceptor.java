package com.configuration;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class DownloadInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws IOException {
        System.out.println("ZIPPPPPP");
        File workingFile = new File(System.getProperty("user.dir"));
        File aboveWorking = new File(workingFile.getParent());
        FileSystemUtils.deleteRecursively(Paths.get(aboveWorking.getPath() + "/server/temp/" + SecurityContextHolder.getContext().getAuthentication().getName()));
    }

}
