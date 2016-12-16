from bs4 import BeautifulSoup
import re
from lxml import etree

# get the title of the article
def get_title(soup, root) :
	t = etree.SubElement(root, "title")
	try :
		title = str(soup.find('title'))
		title = title.split("<title>")[1].split("</title>")[0].split("--")[0]
		t.text = str(title)
	except :
		t = None
		t.text = None

	return root