package websearch

class SearchEngine(
  val Map: Map<URL, WebPage>,
  private val index: MutableMap<String, List<SearchResult>> = mutableMapOf()
) {
  fun compileIndex() {
    val wrdURL: MutableMap<String, MutableList<URL>> = mutableMapOf()
    for ((url, page) in Map) {
      val words = page.extractWords()
      for (word in words) {
        wrdURL.computeIfAbsent(word) { mutableListOf() }.add(url)
      }
    }
    for ((word, urls) in wrdURL) {
      index[word] = rank(urls)
    }
  }

  fun rank(urls: List<URL>): List<SearchResult> {
    val urlcnt = urls.groupingBy { it }.eachCount()
    return urlcnt.map { (url, count) -> SearchResult(url, count) }.sortedByDescending { it.numRefs }
  }

  fun searchFor(query: String): SearchResultsSummary = SearchResultsSummary(query, index[query] ?: emptyList())
}

class SearchResult(
  val url: URL,
  val numRefs: Int
)

class SearchResultsSummary(
  val query: String,
  val results: List<SearchResult>
) {
  override fun toString(): String {
    if (results.isEmpty()) {
      return "No results found for \"$query\"."
    }
    val resultStrings = results.joinToString("\n") { "${it.url} - ${it.numRefs} references" }
    return "Results for \"$query\":\n$resultStrings"
  }
}
