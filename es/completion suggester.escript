https://www.elastic.co/guide/en/elasticsearch/reference/5.6/search-suggesters-completion.html

DELETE idx_completion_suggester
PUT idx_completion_suggester
{
   "mappings": {
      "song": {
         "properties": {
            "suggest": {
               "type": "completion"
            },
            "title": {
               "type": "string",
               "index": "not_analyzed"
            }
         }
      }
   }
}

PUT idx_completion_suggester/song/1?refresh
{
   "suggest": {
      "input": [
         "Nevermind",
         "Nirvana"
      ],
      "weight": 34
   }
}

PUT idx_completion_suggester/song/1?refresh
{
   "suggest": [
      {
         "input": "Nevermind",
         "weight": 10
      },
      {
         "input": "Nirvana",
         "weight": 3
      }
   ]
}


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