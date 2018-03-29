# Twitter Sentiment Analysis

This API integrates the Twitter API with the IBM Watson Tone Analyzer API to detect any strong tones within
a given tweet.

A tone is one of the following:
* Anger
* Fear
* Joy
* Sadness
* Analytical
* Confident
* Tentative

## Endpoints

```
/sentiment?q={text}
```

```
/tweet?user={twitter_handle}&count={count}
```

```
/analyze?user={twitter_handle}&count={count}
```

```
/find?tone={tone_id}
```