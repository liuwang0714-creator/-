package org.example.sotokenspringboot.service.serviceimpl;


import org.example.sotokenspringboot.mapper.filemapper;
import org.example.sotokenspringboot.pojo.Files;
import org.example.sotokenspringboot.service.fileservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class fileserviceimpl implements fileservice {
    @Autowired
    private filemapper filemapper;


    @Override
    public Integer insetFIle(Files files) {
        filemapper.insetFIle(files);
        return files.getId();
    }

    @Override
    public String selectId(Integer id) {
        return filemapper.selectId(id).getFile_url();
    }


    @Override
    public void delete(String url){
        filemapper.delete(url);
    }
}
