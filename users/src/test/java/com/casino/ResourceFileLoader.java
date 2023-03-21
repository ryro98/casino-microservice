package com.casino;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;

@NoArgsConstructor
public class ResourceFileLoader {
    public String getJson(String path) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // read JSON file and map/convert to Java POJO
            // data/sample.json is the file containing the JSON content
            User user = mapper.readValue(new File("src/test/resources/jsons/"+path), User.class);
            return mapper.writeValueAsString(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
