package hr.foi.rampu.memento

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import hr.foi.rampu.memento.ws.NewsItem
import hr.foi.rampu.memento.ws.WsNews
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val name = intent.getStringExtra("news_name")!!

        WsNews.newsService.getNews(name).enqueue(object : Callback<NewsItem> {
            override fun onResponse(call: Call<NewsItem>?, response: Response<NewsItem>?) {
                response?.body()?.let {
                    findViewById<TextView>(R.id.tv_news_activity_name).text = it.title
                    findViewById<TextView>(R.id.tv_news_activity_text).text = it.text
                    Picasso.get().load(WsNews.BASE_URL + it.imagePath)
                        .into(findViewById<ImageView>(R.id.iv_news_activity_image))
                }
            }

            override fun onFailure(call: Call<NewsItem>?, t: Throwable?) {
                Toast.makeText(this@NewsActivity, "Nije uspio dohvat ", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }
}
