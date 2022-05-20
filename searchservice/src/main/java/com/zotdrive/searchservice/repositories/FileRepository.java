package com.zotdrive.searchservice.repositories;

import com.zotdrive.searchservice.document.FileObject;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface FileRepository extends ElasticsearchRepository<FileObject, String> {

    List<FileObject> findByTagsContainingOrName(String tag, String name);
}
