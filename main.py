from flask import Flask, render_template, request, jsonify
from werkzeug.utils import secure_filename
from summarizer import summarize
import whisper_transcriber
import sentiment
import database
import os
app = Flask(__name__)

# Set the upload folder in the app configuration
app.config['UPLOAD_FOLDER'] = 'uploads'

# Ensure the upload folder exists
os.makedirs(app.config['UPLOAD_FOLDER'], exist_ok=True)

@app.route('/')
def home():
    return render_template('index.html')

@app.route('/customerinfo')
def customer_info():
    return render_template('customerinfo.html')

@app.route('/call_records')
def call_records():
    client_id = request.args.get('clientID')
    # Fetch call records or other relevant data for this clientID from your database
    # For example: call_records = database.get_call_records(client_id)
    # Pass this data to your template (if needed)
    return render_template('callrecords.html', client_id=client_id)

@app.route('/process_audio', methods=['POST'])
def process_audio():
    if 'audioFile' not in request.files:
        return jsonify({'error': 'No file part'}), 400
    file = request.files['audioFile']
    if file.filename == '':
        return jsonify({'error': 'No selected file'}), 400
    if file:
        filename = secure_filename(file.filename)
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(filepath)

    # Extract customer details from form data
    customer_name = request.form['customerName']
    customer_phone = request.form['customerPhone']

    database.Connect()
    database.CreateTables()

    transcribed_script = whisper_transcriber.transcribeAudio(filepath)
    #summarize transcribed script
    summarized_script = summarize(transcribed_script)[0]['summary_text']
    print("Transcription complete.")
    sentiment_scores = sentiment.get_sentiment("audiotranscribed.txt")

    clientID = database.AddClient(customer_phone, customer_name, sentiment_scores['compound'])
    database.AddCallRecord(sentiment_scores['compound'], clientID, summarized_script)
    database.UpdateOverallSentiment(clientID)

    database.Disconnect()

    # Return the transcription and sentiment data
    return jsonify({
        'transcription': transcribed_script,
        'summarized_transcription': summarized_script,  # Add this line
        'sentiment': {
            'pos': sentiment_scores['pos'],
            'neg': sentiment_scores['neg'],
            'neu': sentiment_scores['neu'],
            'compound': sentiment_scores['compound']
        }
    })

@app.route('/get_customers')
def get_customers():
    customers = database.get_all_customers()  # This function needs to be implemented in database.py
    return jsonify(customers)

@app.route('/get_call_records')
def get_call_records():
    client_id = request.args.get('clientID')
    if client_id is None:
        return jsonify({'error': 'Missing clientID parameter'}), 400
    try:
        client_id = int(client_id)  # Ensure client_id is an integer
    except ValueError:
        return jsonify({'error': 'clientID must be an integer'}), 400

    call_records = database.get_call_records(client_id)
    return jsonify(call_records)

@app.route('/get_client_info')
def get_client_info():
    client_id = request.args.get('clientID')
    if client_id is None:
        return jsonify({'error': 'Missing clientID parameter'}), 400
    try:
        client_id = int(client_id)  # Ensure client_id is an integer
    except ValueError:
        return jsonify({'error': 'clientID must be an integer'}), 400

    # Fetch client information from the database
    client_info = database.get_client_info(client_id)
    if client_info is None:
        return jsonify({'error': 'Client not found'}), 404

    return jsonify(client_info)

if __name__ == '__main__':
    app.run(debug=True)