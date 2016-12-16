from bs4 import BeautifulSoup
from lxml import etree
import traceback

# get the date of the publication 
def get_date(ref, root) :
	try :
		date = str(ref.find('span', attrs={"class":"cit-pub-date"}))
		date = date.split('"')[2].split(">")[1].split("<")[0]
		root.set("publ-date", str(date))
	except :
		date = None
	
	return root

# ------------------------------------------------------------------------------------------------
# get location of the publication
def get_location(ref, root) :
	try :
		location = str(ref.find('span', attrs={"class":"cit-publ-loc"}))
		location = location.split('"')[2].split(">")[1].split("<")[0]
		root.set("publ-location", str(location))
	except :
		location = None

	return root

# ------------------------------------------------------------------------------------------------
# get edition
def get_edition(ref, root) :
	try :
		edition = str(ref.find('span', attrs={"class":"cit-edition"}))
		edition = edition.split('"')[2].split(">")[1].split("<")[0]
		root.set("edition", str(edition))
	except :
		edition = None
	
	return root	

# ------------------------------------------------------------------------------------------------
# get source
def get_source(ref, root) :
	try :
		source = str(ref.find('span', attrs={"class":"cit-source"}))
		source = source.split('"')[2].split(">")[1].split("<")[0]	
		root.set("source", str(source))
	except :
		source = None
	
	return root	

# ------------------------------------------------------------------------------------------------
# get publication name
def get_publ_name(ref, root) :
	try :
		publ_name = str(ref.find('span', attrs={"class":"cit-publ-name"}))
		publ_name = publ_name.split('"')[2].split(">")[1].split("<")[0]
		publ_name = publ_name.decode('utf8')
		root.set("publ-name", publ_name)
	except :
		publ_name = None
	
	return root	

# ------------------------------------------------------------------------------------------------
# get collaboration
def get_collaboration(ref, root) :
	try :
		collaboration = str(ref.find('span', attrs={"class":"cit-auth cit-collab"}))
		collaboration = collaboration.split('"')[2].split(">")[1].split("<")[0]
		collaboration = collaboration.decode('utf8')
		root.set("collaboration", collaboration)
	except :
		collaboration = None

	return root	

# ------------------------------------------------------------------------------------------------
# get author username
def get_username(ref, root) :
	try :
		username = ref.findAll('span', attrs={"class":"cit-name-surname"})
		given_username = ref.findAll('span', attrs={"class":"cit-name-given-names"})
		i = 1
		for u in username :
			u = u.renderContents()
			u = u.decode('utf8')
			root.set("name-author-"+str(i), u+","+given_username[i-1].renderContents().decode('utf8'))
			i += 1
	except :
		username = None

	return root

# ------------------------------------------------------------------------------------------------
# get link
def get_link(ref, root) :
	try :
		link = str(ref.findAll('a', href=True))
		link = link.split('"')[1]
		root.set("link", str(link))
	except :
		link = None
		#root.set("link", "")

	return root

# ------------------------------------------------------------------------------------------------
# get collaboration
def get_journal(ref, root) :
	try :
		journal = ref.find('abbr', attrs={"class":"cit-jnl-abbrev"})
		journal = journal.renderContents()
		#journal = collaboration.split('"')[2].split(">")[1].split("<")[0]
		journal = journal.decode('utf8')
		root.set("jnl-abbrev", journal)
	except Exception, err:
		journal = None
		#print(traceback.format_exc())

	return root	

# ------------------------------------------------------------------------------------------------
# ------------------------------------ GETTING EACH REFERENCE ------------------------------------
# ------------------------------------------------------------------------------------------------

def get_reference(soup, root):
	refer = etree.SubElement(root, "references")
	references = soup.findAll('div', attrs={"class":"cit-metadata"})
	for ref in references :
		# create the node
		rnode = etree.SubElement(refer, "reference")
		# edit the node reference
		rnode = get_username(ref, rnode)
		rnode = get_collaboration(ref, rnode)
		rnode = get_journal(ref, rnode)
		rnode = get_source(ref, rnode)
		rnode = get_edition(ref, rnode)
		rnode = get_location(ref, rnode)
		rnode = get_publ_name(ref, rnode)
		rnode = get_date(ref, rnode)
		rnode = get_link(ref, rnode)
	return root