import json

def ReadJSONFile(filename, key):
    with open(filename) as json_file:
        data = json.load(json_file)
        return data[key]
    
def WriteJSONFile(filename, data):
    with open(filename, 'w') as json_file:
        json.dump(data, json_file)