#!/usr/bin/env python

import sys

def main(argv):
    for line in sys.stdin:
        line = line.strip()
        key = line.split('\t',1)[0]
        value = line.split('\t',1)[1]
        if(value > '0'):
            print key[:-1] + 'P>'
        else:
            print key[:-1] + 'N>'
	
if __name__ == "__main__":
    main(sys.argv)
	
		
