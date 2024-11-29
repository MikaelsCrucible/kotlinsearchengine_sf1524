package websearch

class WebCrawler(
  private val start: URL,
  private val max: Int = 10
) {
  private val downloaded = mutableSetOf<URL>()
  private val pages = mutableMapOf<URL, WebPage>()

  fun run() {
    val queue = mutableListOf(start)
    while (queue.isNotEmpty() && pages.size < max) {
      val url = queue.removeAt(0) // use BFS because usually these websites have a lot of subroutines
      if (url !in downloaded) {
        try {
          val page = url.download()
          pages[url] = page
          downloaded.add(url)
          queue.addAll(page.extractLinks().filter { it !in downloaded })
        } catch (e: Exception) {
          println("Failed to download $url: ${e.message}")
          downloaded.add(url)
        }
      }
    }
  }

  fun dump(): Map<URL, WebPage> = pages
}

fun main() {
  val crawler = WebCrawler(URL("http://www.bbc.co.uk")) // I cant find a better example lol
  crawler.run()
  val searchEngine = SearchEngine(crawler.dump())
  searchEngine.compileIndex()
  println(searchEngine.searchFor("news"))
}
