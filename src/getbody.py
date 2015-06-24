from bs4 import BeautifulSoup
import re
from lxml import etree

def has_id(tag):
    if tag.has_attr('id') :
    	return str(tag.renderContents())
    else :
    	return None

def get_paragraph(tag, root) :
	try :
		if (has_id(tag) != None) :
			content = has_id(tag)
			p = etree.SubElement(root, "p")
			p.text = content.decode('utf8')
	except :
		#content = None
		i = 0

	return root


def get_body(soup, root) :
	# ['p', 'title', 'h1', 'h2', 'h3']
	b = etree.SubElement(root, "body")
	for tag in soup.findAll('p') :
		b = get_paragraph(tag, b)
	return root