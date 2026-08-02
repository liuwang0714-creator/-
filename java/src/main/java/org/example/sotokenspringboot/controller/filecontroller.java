package org.example.sotokenspringboot.controller;


import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import org.example.sotokenspringboot.pojo.Files;
import org.example.sotokenspringboot.pojo.Result;
import org.example.sotokenspringboot.service.fileservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/file/")
public class filecontroller {
    @Autowired
    private fileservice fileservice;
    @RequestMapping("upload")
    public Result<Integer> upload(MultipartFile file) throws Exception {

        String originalFilename = file.getOriginalFilename();
//        利用uuid定义一个不重复的图片命
        String filename = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        String dir = "E:\\pg_file\\images\\";
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
//        上传图片到指定路径
        file.transferTo(new File(dir + filename));
        Files files = new Files();
        files.setFile_url(filename);
        files.setCreate_id(Integer.valueOf(StpUtil.getLoginIdAsString()));
        return Result.success(fileservice.insetFIle(files));
    }
}
