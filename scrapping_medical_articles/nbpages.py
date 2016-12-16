import urllib2
from bs4 import BeautifulSoup

def no_pages(url):
	try :
		htmlfile = urllib2.urlopen(url)
		soup = BeautifulSoup(htmlfile)
		total = soup.find('p', attrs={"class":"subj-coll-display-description"})
		total = total.string
		total = total.split("of")
		total = total[1].split(" ")
		return int(int(total[1])/10)+1
	except :
		#print "no display !"
		return 0