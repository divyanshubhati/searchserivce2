package com.zotdrive.searchservice.service;

import com.zotdrive.searchservice.helper.Indices;
import com.zotdrive.searchservice.helper.Utility;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class IndexService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);
    private final List<String> INDICES_TO_CREATE = List.of(Indices.FILE_INDEX);
    private final RestHighLevelClient client;

    @Autowired
    public IndexService(RestHighLevelClient client){
        this.client = client;
    }

    @PostConstruct
    public void checkIndex(){
        checkAndCreateIndices(false);
    }

    public void checkAndCreateIndices(final boolean deleteExisting) {

        final String settings = Utility.loadAsString("static/es-settings.json");
        if(settings == null){
            LOG.error("Fail to load index settings");
            return;
        }

        for( final String index : INDICES_TO_CREATE){

            try{
                boolean indexExist = client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
                if(indexExist) {
                    if(!deleteExisting){
                        continue;
                    }

                    client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);

                };

                final String mappings = Utility.loadAsString("static/mappings/" + index + ".json");
                if(mappings == null){
                    LOG.error("Fail to create index : '{}'", index);
                    continue;

                }
                final CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
                createIndexRequest.settings(settings, XContentType.JSON);
                createIndexRequest.mapping(mappings, XContentType.JSON);
                client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            } catch (final Exception e){
                LOG.error(e.getMessage(), e);
            }

        }
    }
}
