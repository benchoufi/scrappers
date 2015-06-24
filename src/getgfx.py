from bs4 import BeautifulSoup
import re
import urllib
from urlparse import urljoin
import namearticle
import os
import sys

# get images 
def get_gfx(soup, name, url) :
	for img in soup.findAll('img', attrs={'src':re.compile(str(name)+"(.+?)")}) :
		link = urljoin(url, img['src'])
		print link
		#"/home/youcef/Desktop/bmkopen/articles_parsed/gfx/"
		#urllib.urlretrieve(link, os.path.basename("image_dyali"))
		#path = os.path.join("/home/youcef/Desktop/bmjopen/gfx", "image_dyali")
		urllib.urlretrieve(link, path)
		#print os.path.dirname("/home/youcef/")