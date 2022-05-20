package com.zotdrive.searchservice.search.utility;

import com.zotdrive.searchservice.search.SearchReq;
import com.zotdrive.searchservice.search.SearchRequestDTO;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

public class SearchUtility {


    public static SearchRequest buildSearchRequest(final String indexName, final SearchRequestDTO dto){
        try {
//            final QueryBuilder userQuery = QueryBuilders.termQuery("createdBy", dto.getUserId());

            final QueryBuilder userQuery = QueryBuilders.multiMatchQuery(dto.getUserId())
                    .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                    .field("createdBy")
                    .field("sharedWith");

            final QueryBuilder notDeletedQuery = QueryBuilders.termQuery("deleted", dto.isDeleted());


            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .filter(userQuery)
                    .filter(notDeletedQuery);

            if(dto.getKeyword() != null){
                //final QueryBuilder keywordQuery = QueryBuilders.matchQuery("tags", dto.getKeyword()).operator(Operator.AND);
                final MultiMatchQueryBuilder keywordQuery = QueryBuilders.multiMatchQuery(dto.getKeyword())
                        .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                        .operator(Operator.AND)
                        .field("tags")
                        .field("name");
                boolQuery = boolQuery.must(keywordQuery);
            }


            if(dto.getCreatedAfter() != null){
                final QueryBuilder dateQuery = QueryBuilders.rangeQuery("createdOn").gte(dto.getCreatedAfter());
                boolQuery = boolQuery.filter(dateQuery);
            }

            if(dto.getCreatedBefore() != null){
                final QueryBuilder dateQuery = QueryBuilders.rangeQuery("createdOn").lte(dto.getCreatedBefore());
                boolQuery = boolQuery.filter(dateQuery);
            }

            if(dto.getParentId() != null){
                final QueryBuilder parentQuery = QueryBuilders.termQuery("parentId", dto.getParentId());
                boolQuery = boolQuery.filter(parentQuery);
            }

            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .postFilter(boolQuery);

            builder = builder.sort("name", SortOrder.ASC);

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(final String indexName, final SearchReq dto){

        try{
            SearchSourceBuilder builder = new SearchSourceBuilder().postFilter(getQueryBuilder(dto));

            if (dto.getSortBy() != null) {
                builder = builder.sort(
                        dto.getSortBy(),
                        dto.getOrder() != null ? dto.getOrder() : SortOrder.ASC
                );
            }


            SearchRequest request = new SearchRequest(indexName);
            request.source(builder);
            return request;
        } catch (final Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                                                   final String field,
                                                                                   final Date date) {
        try {
            final SearchSourceBuilder builder = new SearchSourceBuilder()
                    .postFilter(getQueryBuilder(field, date));

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                                                   final SearchReq dto,
                                                                                   final Date date) {
        try {
            final QueryBuilder searchQuery = getQueryBuilder(dto);
            final QueryBuilder dateQuery = getQueryBuilder("created", date);

            final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .must(searchQuery)
                    .must(dateQuery);

            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .postFilter(boolQuery);

            if (dto.getSortBy() != null) {
                builder = builder.sort(
                        dto.getSortBy(),
                        dto.getOrder() != null ? dto.getOrder() : SortOrder.ASC
                );
            }

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static QueryBuilder getQueryBuilder(final SearchReq dto) {
        if (dto == null) {
            return null;
        }

        final List<String> fields = dto.getFields();
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }

        if (fields.size() > 1) {
            final MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(dto.getSearchTerm())
                    .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                    .operator(Operator.AND);

            fields.forEach(queryBuilder::field);

            return queryBuilder;
        }

        return fields.stream()
                .findFirst()
                .map(field ->
                        QueryBuilders.matchQuery(field, dto.getSearchTerm())
                                .operator(Operator.AND))
                .orElse(null);
    }

    private static QueryBuilder getQueryBuilder(final String field, final Date date) {
        return QueryBuilders.rangeQuery(field).gte(date);
    }
}
