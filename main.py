#! /usr/bin/python
# -*- coding:utf-8 -*-

import getarticle, findlinks, namearticle, collection, nbpages, xml
import unicodedata

#url = "http://bmjopen.bmj.com/content/5/6/e007470.full"
#url = "http://bmjopen.bmj.com/content/5/5/e007898.full"
url = "http://bmjopen.bmj.com/content/3/3/e002489.full"
#url = "http://bmjopen.bmj.com/content/5/4/e006740.full"
#url = "http://bmjopen.bmj.com/content/1/2/bmjopen-2011-000240.full"
#base = "http://bmjopen.bmj.com/collections/bmj_open_"
#type_collection = "epidemiology"
#url = base+type_collection+"?page="+str(1)
#max_pages = nbpages.no_pages(url)
#print("this collection contains "+str(max_pages)+" pages")
getarticle.get_article(url)

#i = max_pages - 2
#i = 1
#while i <= max_pages : 
	#url = "http://bmjopen.bmj.com/collections/bmj_open_epidemiology?page="+str(i)
	#print url
	#for link in findlinks.find_links(url):
		#print link
	#i+=1



#collection.get_collection(url)

#print namearticle.name_article(url)
