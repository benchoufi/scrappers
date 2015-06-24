def name_article(url):
	name = url.split("/")
	name = name[len(name)-1].split(".")
	return name[0] 