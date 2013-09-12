#!/usr/bin/env python

import sys
import re
import simplejson
import urllib

weightsandKeywords = []
# Open the config.txt file for configurable properties
f = urllib.urlopen('https://s3.amazonaws.com/feed-config-files/reductionOutputConfig.txt')
for line in f:
	if(("premarket") in line.lower()):
		premarket = line.split('=')[1]
	else:
		aftermarket = line.split('=')[1]

def readweightsAndKeywordFile():
    f = urllib.urlopen('https://s3.amazonaws.com/feed-config-files/weightsAndKeywords.txt')
    for line in f:
        line = line.strip()
        weightsandKeywords.append( line )
        

def main(argv):
	try:
		for line in sys.stdin:
			line = line.strip()
			data = simplejson.loads(line)
			key = getKeyValue(data['today_date'], data['pubTime'], data['ticker'])
			getKeyValuePair(key,data['title'])
			getKeyValuePair(key,data['description'])
	except:
		pass
       
                    
def getKeyValue(todayDate, pubTime, ticker):
    key = ''
    if(pubTime < premarket):
        key = "<" +todayDate + '><' +  ticker + '><PM'
    elif(pubTime > aftermarket):
        key =  "<"+ todayDate + '><'+ ticker + '><AM'
    else:
        key =  "<" + todayDate + '><'+ ticker + '><TM'
    return key
    
def getKeyValuePair(key, line):
    words = line.split()
    for word in words:
        for item in weightsandKeywords:
            if(item.split(",",1)[1].lower() in word.lower()):
                print 'LongValueSum:' + key + "\t" + item.split(",",1)[0]

if __name__ == "__main__":
	readweightsAndKeywordFile()
	main(sys.argv)