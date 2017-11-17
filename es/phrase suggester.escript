https://www.elastic.co/guide/en/elasticsearch/reference/6.0/search-suggesters-phrase.html

DELETE idx_phrase_suggester

PUT idx_phrase_suggester
{
   "settings": {
      "index": {
         "number_of_shards": 1,
         "analysis": {
            "analyzer": {
               "trigram": {
                  "type": "custom",
                  "tokenizer": "standard",
                  "filter": [
                     "standard",
                     "shingle"
                  ]
               },
               "reverse": {
                  "type": "custom",
                  "tokenizer": "standard",
                  "filter": [
                     "standard",
                     "reverse"
                  ]
               }
            },
            "filter": {
               "shingle": {
                  "type": "shingle",
                  "min_shingle_size": 2,
                  "max_shingle_size": 3
               }
            }
         }
      }
   },
   "mappings": {
      "mapping_phrase_suggester": {
         "properties": {
            "title": {
               "type": "string",
               "index": "analyzed",
               "fields": {
                  "trigram": {
                     "type": "string",
                     "index": "analyzed",
                     "analyzer": "trigram"
                  },
                  "reverse": {
                     "type": "string",
                     "index": "analyzed",
                     "analyzer": "reverse"
                  }
               }
            }
         }
      }
   }
}

POST idx_phrase_suggester/mapping_phrase_suggester?refresh=true
{"title": "noble warriors"}
POST idx_phrase_suggester/mapping_phrase_suggester?refresh=true
{"title": "nobel prize"}


POST idx_phrase_suggester/mapping_phrase_suggester/_search
{
   "suggest": {
      "text": "noble prize",
      "simple_phrase": {
         "phrase": {
            "field": "title.trigram",
            "size": 1,
            "gram_size": 3,
            "direct_generator": [
               {
                  "field": "title.trigram",
                  "suggest_mode": "always"
               }
            ],
            "highlight": {
               "pre_tag": "<em>",
               "post_tag": "</em>"
            }
         }
      }
   }
}


POST idx_phrase_suggester/mapping_phrase_suggester/_search
{
   "suggest": {
      "text": "obel prize",
      "simple_phrase": {
         "phrase": {
            "field": "title.trigram",
            "size": 1,
            "direct_generator": [
               {
                  "field": "title.trigram",
                  "suggest_mode": "always"
               },
               {
                  "field": "title.reverse",
                  "suggest_mode": "always",
                  "pre_filter": "reverse",
                  "post_filter": "reverse"
               }
            ]
         }
      }
   }
}