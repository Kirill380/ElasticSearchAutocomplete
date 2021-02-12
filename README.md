1. Prefix query with fuzziness
Not use analizer (not full text search)
GET nft/_search
{
  "_source": ["act.data.author", "act.data.category"],
  "query": { 
    "bool" : {
      "should": [
        {
          "prefix": { "act.data.author.keyword": "ilo" } 
        },
        {
          "prefix": { "act.data.category.keyword": "ilo" } 
        },
        {
          "fuzzy": { 
            "act.data.author.keyword": { "value": "ilo", "fuzziness": 2, "prefix_length": 0 } 
            
          } 
        },
        {
          "fuzzy": { 
            "act.data.category.keyword": { "value": "ilo", "fuzziness": 2, "prefix_length": 0 } 
            
          } 
        }
      ]
    }
  }
}
2. Match Phrase Prefix Query
 Only the first 50 terms are relevant for the search in a text field.
 Duplicates
 No fuzziness
 
3.  Edge NGram Tokenizer. 
{
  "settings": {
    "analysis": {
      "analyzer": {
        "autocomplete": {
          "tokenizer": "autocomplete",
          "filter": [
            "lowercase"
          ]
        },
        "autocomplete_search": {
          "tokenizer": "lowercase"
        }
      },
      "tokenizer": {
        "autocomplete": {
          "type": "edge_ngram",
          "min_gram": 2,
          "max_gram": 20,
          "token_chars": [
            "letter"
          ]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "act": {
        "properties": {
            "data" : {
                "properties": {
                    "author": {
                      "type": "text",
                     "analyzer": "autocomplete",
                      "search_analyzer": "autocomplete_search"
                    },
                    "category": {
                      "type": "text",
                      "analyzer": "autocomplete",
                      "search_analyzer": "autocomplete_search"
                    }
                }
            }
        }
      }
    }
  }
}
