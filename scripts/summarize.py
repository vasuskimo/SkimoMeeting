import sys
import spacy
from collections import Counter
from string import punctuation
import en_core_web_lg
nlp = en_core_web_lg.load()

def top_sentence(text, percent):
    keyword = []
    pos_tag = ['PROPN', 'ADJ', 'NOUN', 'VERB']
    doc = nlp(text.lower())
    for token in doc:
        if(token.text in nlp.Defaults.stop_words or token.text in punctuation):
            continue
        if(token.pos_ in pos_tag):
            keyword.append(token.text)
    
    freq_word = Counter(keyword)
    max_freq = Counter(keyword).most_common(1)[0][1]
    for w in freq_word:
        freq_word[w] = (freq_word[w]/max_freq)
        
    sent_strength={}
    cnt = 0
    for sent in doc.sents:
        for word in sent:
            if word.text in freq_word.keys():
                cnt += 1
                if sent in sent_strength.keys():
                    sent_strength[sent]+=freq_word[word.text]
                else:
                    sent_strength[sent]=freq_word[word.text]
    
    summary = []
    
    sorted_x = sorted(sent_strength.items(), key=lambda kv: kv[1], reverse=True)
    
    counter = 0
    limit = percent * cnt/100.0
    for i in range(len(sorted_x)):
        summary.append(str(sorted_x[i][0]).capitalize())
        counter += 1
        if(counter >= limit):
            break
    return ' '.join(summary)

def main(inFileName, outFileName, percent):
   file = open(inFileName)
   lines = file.read().replace("\n", " ")
   file.close()
   summary = top_sentence(lines, float(percent))
   f = open(outFileName, "w")
   f.write(summary)
   f.close()

if __name__ == "__main__":
   main(sys.argv[1], sys.argv[2], sys.argv[3])
