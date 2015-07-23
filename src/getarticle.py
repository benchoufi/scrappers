import urllib2
from lxml import etree
from bs4 import BeautifulSoup
import re
import namearticle, getauthors, getreference, getbody, gettitle, getgfx
import traceback

# the path where you want to store your articles
path = ""

def get_article(url) :
	try :
		# charge the source code
		htmlfile = urllib2.urlopen(url)
		soup = BeautifulSoup(htmlfile)
		root = etree.Element("article")
		name = namearticle.name_article(url)
        	# create/open a file
		myfile = open(path+name+".xml", "w+")
		myfile.write('<?xml version="1.0" encoding="UTF-8"?> \n')

		# get title
		root = gettitle.get_title(soup, root)

		# get authors
		root = getauthors.get_authors(soup, root)

		# get paragraphs
		root = getbody.get_body(soup, root)

		getgfx.get_gfx(soup, name, url)

		# get reference
		myfile.write(etree.tostring(getreference.get_reference(soup, root), pretty_print=True))

		# close the file
		myfile.close()
	except Exception, err:
		print "sorry, we can't get this article !"
		print(traceback.format_exc()) 
