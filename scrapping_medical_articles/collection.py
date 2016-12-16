import getarticle
import findlinks
from threading import Thread

def get_collection(url):
	threadlist = []

	for link in findlinks.find_links(url):
		t = Thread(target=getarticle.get_article, args=(link,))
		t.start()
		threadlist.append(t)

	for b in threadlist :
		b.join()