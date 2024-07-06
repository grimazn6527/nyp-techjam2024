from transformers import pipeline

def summarize(text):
    summarizer = pipeline("summarization", model="facebook/bart-large-cnn")
    summarized_text = summarizer(text, max_length=130, min_length=30, do_sample=False)

    print("SUMMARIZED_TEXT: ", summarized_text)

    return summarized_text