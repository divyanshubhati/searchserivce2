package com.zotdrive.searchservice.controller;

import com.zotdrive.searchservice.document.FileObject;
import com.zotdrive.searchservice.search.SearchRequestDTO;
import com.zotdrive.searchservice.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/protected/api")
public class ProtectedController {

    private final FileService service;

    public ProtectedController(FileService service){
        this.service = service;
    }


    @PostMapping("/search")
    public List<FileObject> search(
            @RequestBody final SearchRequestDTO dto) {
        return service.searchUsingDTO(dto);
    }
}
