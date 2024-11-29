package websearch
import org.jsoup.Jsoup.connect
import org.jsoup.nodes.Document

data class URL(
  val urlstr: String
) {
  override fun toString() = urlstr

  fun download(): WebPage {
    val doc = connect(urlstr).get()
    return WebPage(doc)
  }
}

class WebPage(
  val doc: Document
) {
  fun extractWords(): List<String> {
    val text = doc.text().lowercase()
    return text.replace(Regex("[,.?!]"), "").split(Regex("\\s+"))
  }

  fun extractLinks(): List<URL> {
    val links = doc.select("a[href]")
    return links
      .map { it.attr("href") }
      .filter { it.isNotEmpty() && (it.startsWith("http") || it.startsWith("https")) }
      .map { URL(it) }
  }
}
