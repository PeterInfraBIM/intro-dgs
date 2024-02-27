package com.example.soundtracks.repositories;

import org.json.JSONTokener;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

abstract class AbstractRepository {
    private BufferedReader reader;

    protected JSONTokener getJsonTokener(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource("data/" + fileName);
        InputStream inputStream = resource.getInputStream();
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        reader = new BufferedReader(streamReader);
        return new JSONTokener(reader);
    }

    protected void closeReader() throws IOException {
        reader.close();
    }
}
