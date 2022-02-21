package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button

    lateinit var client: TwitterClient
    lateinit var etCountTweet: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        etCountTweet = findViewById(R.id.etCountTweet)

        client = TwitterApplication.getRestClient(this)

        btnTweet.setOnClickListener {

            // grab the edited text content
            val tweetContent = etCompose.text.toString()

            // 1. Make sure tweet isn't empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
                // try SnackBar message
            } else
            // 2. the tweet is under character count
                {
                    // Make a Twitter api to publish tweet
                    client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                        override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                            // Send the tweet back to TimelineActivity
                            val tweet = Tweet.fromJson(json.jsonObject)

                            // prepare data intent
                            val data = Intent()

                            // pass relevant data back as a result
                            data.putExtra("tweet",tweet)
                            setResult(RESULT_OK, data)
                            finish()
                        }
                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {
                            Log.e(TAG, "Failed to publish tweet: $statusCode", throwable)
                        }
                    })
                }
        }

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val tweetLength = etCompose.length()
                // remaining character counts
                val remaining = MAX_TWEET_COUNT - tweetLength

                // Fires right as the text is being changed (even supplies the range of text)
                etCountTweet.setText("Characters Left: $remaining")

                if (tweetLength > 140){
                    etCountTweet.setTextColor(Color.parseColor("#FF0000"))
                    etCountTweet.setText("Tweet is too long! Limit is 140 characters.")
                    btnTweet.isEnabled = false
                }else{
                    btnTweet.isEnabled = true
                    etCountTweet.setTextColor(Color.parseColor("#808080"))
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Fires right before text is changing
            }
            override fun afterTextChanged(s: Editable) {
                // Fires right after the text has changed
            }
        })


    }

    companion object {
        val TAG = "ComposeActivity"
        const val MAX_TWEET_COUNT = 140
    }
}