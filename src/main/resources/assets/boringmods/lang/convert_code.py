#!/usr/bin/env python3
#-*- coding: utf-8 -*-

import sys,os,shutil,struct

def main(argv):
    if (len(argv) < 2):
        print('need argument')
    else:
        print(argv[1])
        filename, ext = argv[1].split('.')
        print(filename)
        print(ext)
        bakfile = filename + '.bak'
        if not os.path.exists(bakfile):
            shutil.copyfile(argv[1], bakfile)
        jfile = open(bakfile, encoding='utf-8')
        #alllines = jfile.readlines();
        #for line in alllines:
        #    print(line)
        all = jfile.read()
        jfile.close        
        res = all.encode('unicode_escape')
        print(len(res))
        print(res)        
        ofile = open(argv[1], 'wb')
        ofile.write(res)
        ofile.close()

if __name__ == '__main__':
    main(sys.argv)
