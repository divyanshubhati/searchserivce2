package com.zotdrive.searchservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zotdrive.searchservice.document.FileObject;
import com.zotdrive.searchservice.helper.Indices;
import com.zotdrive.searchservice.repositories.FileRepository;
import com.zotdrive.searchservice.search.SearchReq;
import com.zotdrive.searchservice.search.SearchRequestDTO;
import com.zotdrive.searchservice.search.utility.SearchUtility;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class FileService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);
    private final RestHighLevelClient client;
    private final FileRepository repository;

    @Autowired
    public FileService(RestHighLevelClient client, FileRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    public boolean saveFile(final FileObject file){
        try{
            final String fileAsString = MAPPER.writeValueAsString(file);
            final IndexRequest request = new IndexRequest(Indices.FILE_INDEX);
            request.id(file.getId());
            request.source(fileAsString, XContentType.JSON);
            final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            return response != null && response.status().getStatus() == 201;

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
           // repository.save(file);
    }

    public boolean update(final FileObject updatedFileObject){
        FileObject originalFileObject = getFileById(updatedFileObject.getId());
        if(updatedFileObject.getParentId() != null){
            originalFileObject.setParentId(updatedFileObject.getParentId());
        }
        if(updatedFileObject.getName() != null){
            originalFileObject.setName(updatedFileObject.getName());
        }
        if(updatedFileObject.getPath() != null){
            originalFileObject.setPath(updatedFileObject.getPath());
        }
        if(updatedFileObject.getTags() != null){
            originalFileObject.setTags(updatedFileObject.getTags());
        }
        return saveFile(originalFileObject);
    }

    public boolean updateFileStatus (final String fieldId, final boolean status){

        FileObject originalFileObject = getFileById(fieldId);
        originalFileObject.setDeleted(status);
        return saveFile(originalFileObject);

    }

    public boolean shareFile(final FileObject inputFile){
        FileObject originalFileObject = getFileById(inputFile.getId());

        if(!originalFileObject.getCreatedBy().equals(inputFile.getCreatedBy())) return false;

        if(originalFileObject.getSharedWith() == null){
            originalFileObject.setSharedWith(new ArrayList<String>());
        }
        for(String user : inputFile.getSharedWith()){
            if(originalFileObject.getSharedWith().contains(user)) continue;
            originalFileObject.getSharedWith().add(user);
        }

        return saveFile(originalFileObject);
    }

    public boolean removeFileShare(final FileObject inputFileObject){

        FileObject originalFileObject = getFileById(inputFileObject.getId());
        if(!originalFileObject.getCreatedBy().equals(inputFileObject.getCreatedBy())) return false;

        if(originalFileObject.getSharedWith() == null){
            return true;
        }
        for(String user : inputFileObject.getSharedWith()){
            originalFileObject.getSharedWith().remove(user);
        }
        return saveFile(originalFileObject);
    }


    public FileObject getFileById(final String fileId){

        try{
            final GetResponse responseDocument = client.get(new GetRequest(Indices.FILE_INDEX, fileId), RequestOptions.DEFAULT);
            if(responseDocument == null || responseDocument.isSourceEmpty()) return null;

            return MAPPER.readValue(responseDocument.getSourceAsString(), FileObject.class);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }

       // return repository.findById(fileId).orElse(null);
    }

//    public List<FileObject> findKeyWord(final String keyword){
//        return repository.findByTagsContainingOrName(keyword, keyword);
//    }
//
//    public List<FileObject> search(final SearchReq dto) {
//        final org.elasticsearch.action.search.SearchRequest request = SearchUtility.buildSearchRequest(
//                Indices.FILE_INDEX,
//                dto
//        );
//
//        return searchInternal(request);
//    }

    public List<FileObject> searchUsingDTO(final SearchRequestDTO dto){
        final SearchRequest request = SearchUtility.buildSearchRequest( Indices.FILE_INDEX, dto);
        return searchInternal(request);
    }



//    public List<FileObject> getAllVehiclesCreatedSince(final Date date) {
//        final org.elasticsearch.action.search.SearchRequest request = SearchUtility.buildSearchRequest(
//                Indices.FILE_INDEX,
//                "created",
//                date
//        );
//
//        return searchInternal(request);
//    }
//
//    public List<FileObject> searchCreatedSince(final SearchReq dto, final Date date) {
//        final org.elasticsearch.action.search.SearchRequest request = SearchUtility.buildSearchRequest(
//                Indices.FILE_INDEX,
//                dto,
//                date
//        );
//
//        return searchInternal(request);
//    }


    private List<FileObject> searchInternal(final org.elasticsearch.action.search.SearchRequest request) {
        if (request == null) {
            LOG.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<FileObject> fileObjects = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                fileObjects.add(
                        MAPPER.readValue(hit.getSourceAsString(), FileObject.class)
                );
            }

            return fileObjects;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }



}
