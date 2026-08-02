package org.example.sotokenspringboot.service;

import org.example.sotokenspringboot.pojo.Files;

public interface fileservice {
    Integer insetFIle(Files files);

    String selectId(Integer id);

    void delete(String url);
}
