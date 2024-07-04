import spacy

def GetName(file_path):
    nlp = spacy.load('en_core_web_sm')
    with open(file_path, 'r') as file:
        text = file.read()
    ner = nlp(text)

    for word in ner.ents:
        if word.label_ == "PERSON":
            return word.text
