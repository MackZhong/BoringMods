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
        alllines = jfile.readlines();
        #all = jfile.read()
        jfile.close
        ofile = open(argv[1], 'wb')
        for line in alllines:
            line = line.replace('\n', '')
            print(line)
            res = line.encode('unicode_escape')
            print(res)
            ofile.write(res)
            ofile.write(b'\n')
        ofile.close()

if __name__ == '__main__':
    main(sys.argv)
