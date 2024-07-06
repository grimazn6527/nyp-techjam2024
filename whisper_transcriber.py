import whisper

CHUNK_LIM = 480000

def transcribeAudio(filename):
    model = whisper.load_model("medium")
    audios = []
    audio = whisper.load_audio(filename)

    # if smaller than 30 sec, move on
    if len(audio) <= CHUNK_LIM:
        audio = whisper.pad_or_trim(audio)
        audios.append(audio)

    # if larger than 30 sec, chunk it and pad last piece
    else:

        for i in range(0, len(audio), CHUNK_LIM):
            chunk = audio[i : i + CHUNK_LIM]
            chunk_index = len(chunk)
            if chunk_index < CHUNK_LIM:
                chunk = whisper.pad_or_trim(chunk)
            audios.append(chunk)

    results = ""

    for chunk in audios:
        #print(chunk.shape)
        # chunk = whisper.pad_or_trim(chunk)
        # make log-Mel spectrogram and move to the same device as the model
        mel = whisper.log_mel_spectrogram(chunk).to(model.device)

        # decode the audio
        options = whisper.DecodingOptions(fp16=False)
        result = whisper.decode(model, mel, options)
        # print(result)
        results += result.text + ". " 

    print(results)

    f = open("audiotranscribed.txt", "w")
    f.write(results)
    f.close()

    return results;