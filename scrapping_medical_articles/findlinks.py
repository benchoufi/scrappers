import urllib2
from bs4 import BeautifulSoup

def find_links(url):
	links = []
	base = "http://bmjopen.bmj.com"
	try :
		htmlfile = urllib2.urlopen(url)
		soup = BeautifulSoup(htmlfile)
		for link in soup.findAll('a', attrs={"rel":"full-text"}, href=True):
			links.append(base+link['href'])
		return links
	except :
		print "links not found !"