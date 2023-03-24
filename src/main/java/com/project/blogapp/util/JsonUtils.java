package com.project.blogapp.util;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class JsonUtils {

    @Autowired
    private ResourceLoader resourceLoader;

    public JSONObject read() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:/openapi/json-refs.json");
        InputStream inputStream= resource.getInputStream();
        byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
        String content = new String(bdata, StandardCharsets.UTF_8);
        return new JSONObject(content);
    }
}
