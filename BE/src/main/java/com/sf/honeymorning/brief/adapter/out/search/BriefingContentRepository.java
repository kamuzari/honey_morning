package com.sf.honeymorning.brief.adapter.out.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.sf.honeymorning.brief.adapter.out.search.index.BriefingContentDocument;

public interface BriefingContentRepository extends ElasticsearchRepository<BriefingContentDocument, String> {

	@Query("""
		{
		  "bool": {
		    "must": [
		      {"term": {"userId": "?0"}}
		    ],
		    "should": [
		      {
		        "multi_match": {
		          "query": "?1",
		          "fields": ["summary^3", "fullText^2", "keywords^2"],
		          "fuzziness": "AUTO"
		        }
		      },
		      {
		        "nested": {
		          "path": "quizzes", 
		          "query": {
		            "multi_match": {
		              "query": "?1",
		              "fields": ["quizzes.question^2", "quizzes.options", "quizzes.answer^2"]
		            }
		          }
		        }
		      }
		    ],
		    "minimum_should_match": 1
		  }
		}
		""")
	Page<BriefingContentDocument> searchByUserIdAndAll(Long userId, String keyword, Pageable pageable);
}
