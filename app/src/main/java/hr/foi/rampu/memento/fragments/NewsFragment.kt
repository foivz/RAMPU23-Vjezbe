package hr.foi.rampu.memento.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hr.foi.rampu.memento.R
import hr.foi.rampu.memento.adapters.NewsAdapter
import hr.foi.rampu.memento.ws.NewsResponse
import hr.foi.rampu.memento.ws.WsNews
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingCircle: ProgressBar
    private lateinit var btnRefresh: FloatingActionButton
    private val ws = WsNews.newsService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadingCircle = view.findViewById(R.id.pb_news_fragment_loading)

        btnRefresh = view.findViewById(R.id.fab_news_fragment_refresh_news)
        btnRefresh.setOnClickListener {
            loadNews()
        }

        recyclerView = view.findViewById(R.id.rv_news)
        recyclerView.layoutManager = LinearLayoutManager(context)
        loadNews()
    }

    private fun loadNews() {
        changeDisplay(true)

        ws.getNews().enqueue(
            object : Callback<NewsResponse> {
                override fun onResponse(
                    call: Call<NewsResponse>?,
                    response: Response<NewsResponse>?
                ) {
                    if (response?.isSuccessful == true) {
                        val responseBody = response.body()
                        val news = responseBody.results
                        recyclerView.adapter = NewsAdapter(news)
                    } else {
                        displayWebServiceErrorMessage()
                    }

                    changeDisplay(false)
                }

                override fun onFailure(call: Call<NewsResponse>?, t: Throwable?) {
                    displayWebServiceErrorMessage()
                }
            }
        )
    }

    private fun displayWebServiceErrorMessage() {
        Toast.makeText(
            context,
            getString(R.string.news_ws_err_msg),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun changeDisplay(isLoading: Boolean) {
        btnRefresh.isVisible = !isLoading
        recyclerView.isVisible = !isLoading
        loadingCircle.isVisible = isLoading
    }
}
