package twitter_sentiment.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import twitter_sentiment.model.internal.TweetSentiment;
import twitter_sentiment.services.TweetSentimentService;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TweetSentimentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TweetSentimentService tweetSentimentService;

    @InjectMocks
    private TweetSentimentController tweetSentimentController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(tweetSentimentController)
                .build();
    }

    @Test
    public void analyzeTweets() throws Exception {

        TweetSentiment tweet = new TweetSentiment("valid tweet");
        ArrayList<TweetSentiment> testData = new ArrayList<>();
        testData.add(tweet);

        when(tweetSentimentService.analyzeTweets("test", 1)).thenReturn(testData);

        mockMvc.perform(get("/tweets")
                .param("user", "test")
                .param("count", "1")
                .param("apikey", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.data[0].tweet").value("valid tweet"));
    }

    @Test
    public void findTweetsByTone() throws Exception {
        TweetSentiment tweet = new TweetSentiment("valid tweet");
        ArrayList<TweetSentiment> testData = new ArrayList<>();
        testData.add(tweet);

        when(tweetSentimentService.findTweetsByUser("test")).thenReturn(testData);

        mockMvc.perform(get("/retrieve/user/test")
                .param("apikey", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.data[0].tweet").value("valid tweet"));
    }

    @Test
    public void findTweetsByUser() throws Exception {
        TweetSentiment tweet = new TweetSentiment("valid tweet");
        ArrayList<TweetSentiment> testData = new ArrayList<>();
        testData.add(tweet);

        when(tweetSentimentService.findTweetsByTone("test")).thenReturn(testData);

        mockMvc.perform(get("/retrieve/tone/test")
                .param("apikey", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.data[0].tweet").value("valid tweet"));
    }
}