import nltk
nltk.download('vader_lexicon')
from nltk.sentiment import SentimentIntensityAnalyzer

# Initialize the SentimentIntensityAnalyzer
def get_sentiment(file_path):
    sia = SentimentIntensityAnalyzer()

    with open(file_path, 'r') as file:
        text = file.read()
    sentiment_scores = sia.polarity_scores(text)

    # Print the sentiment scores
    print("Positive sentiment score:", sentiment_scores['pos'])
    print("Negative sentiment score:", sentiment_scores['neg'])
    print("Neutral sentiment score:", sentiment_scores['neu'] * 100)
    print("Compound sentiment score:", sentiment_scores['compound'] * 100)