#! /usr/bin/python

import getarticle

import traceback

base_url = "https://www.clinicalstudydatarequest.com/Posting.aspx?PostingID="

def run_scrapping():
  try:
    file = open("/Users/mehdibenchoufi/PycharmProjects/untitled/scrappers/scrapp_file.xml", "w+")
    file.write('<?xml version="1.0" encoding="UTF-8"?> \n')
    text = ""
    for index in range(1, 3700):
      text += getarticle.get_article(base_url + str(index))

    file.write(text)
    file.close()
  except Exception, err:
    print "sorry, we can't get this article !"
    print(traceback.format_exc())

run_scrapping()