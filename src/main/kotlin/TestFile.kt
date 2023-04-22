import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

fun main() {
    val httpReq = "https://discord.com/api/v9/channels/860386303677825054/messages?limit=100"
        .httpGet()
        .header("Authorization" to "Bot ODIwMTQ1NjMwNjY3MzQxODY0.YEw6Iw.DEqF9YWqRP-xVsOqoQjOc8SXFDQ")

    httpReq.responseString { request, response, result ->
        when (result) {
            is Result.Failure -> {
                if (response.statusCode != 200) {
                    println("Failed to get Rain's randomized sign")
                    result.getException().printStackTrace()

                    return@responseString
                }
            }

            is Result.Success -> {
                println(result.get())
            }
        }
    }.join()
}