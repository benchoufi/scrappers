from bs4 import BeautifulSoup
import re
from lxml import etree
import traceback, sys

def get_authors(soup, root) :
	aut = etree.SubElement(root, "authors")
	for author in soup.findAll('a', attrs={"class":"name-search"}):
		t = author.renderContents()
		t = t.decode('utf8')
		a = etree.SubElement(aut, "author")
		try :
			a.set("name", t)
		except Exception, err:
			a.set("name", "")
			print(traceback.format_exc())
	return root