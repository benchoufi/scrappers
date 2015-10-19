import urllib2

from lxml import etree
from bs4 import BeautifulSoup
import re
# import namearticle, getauthors, getreference, getbody, gettitle, getgfx
import traceback

# the path where you want to store your articles
# you need to set the path
path = ""


def get_article(url):
    try:
      htmlfile = urllib2.urlopen(url)
      soup = BeautifulSoup(htmlfile)
      root = etree.Element("article")

      root = get_sponsor(soup, root)
      root = get_content(soup, root, "article_title", "UD_STUDY_FULL_NAME")
      root = get_content(soup, root, "sponsor_identification_number", "UD_STUDY_CLINICAL_ID")
      root = get_content(soup, root, "trial_registry_identification_number", "UD_CLINICAL_TRIAL_ID")
      root = get_content(soup, root, "medical_condition", "UD_STUDY_INDICATION")
      root = get_content(soup, root, "phase", "UD_STUDY_PHASE")
      root = get_link_content(soup, root, "link_to_study_details_bi_clinical", "UD_STUDY_LINK")
      root = get_link_content(soup, root, "link_to_study_details_other", "UD_CLINICAL_TRIAL_LINK")
      root = get_nested_content(soup, root, "data_sets_documents", "UD_STUDY_DOCUMENTS")
      root = get_content(soup, root, "date_added_to_site", "UD_STUDY_CITATIONS")
      #root = get_content(soup, root, "additional_information", "UD_CLINICAL_STUDY_COMMENT")
    except Exception, err:
      print "sorry, we can't get this article !"
      print(traceback.format_exc())

    return etree.tostring(root)




def get_sponsor(soup, root):
    aut = etree.SubElement(root, "sponsor")
    article_title = ""
    for field in soup.find('fieldset'):
        for title in field.findAll('label')[1:2]:
            article_title = title.contents[0]
    try:
        aut.set("name", article_title)
    except Exception, err:
        aut.set("study sposonr", "error")
        print(traceback.format_exc())
    return root


def get_content(soup, root, tag_name, id):
    aut = etree.SubElement(root, tag_name)
    text = ""
    for tag in soup.find('div', id=id, text=True):
        text = tag
    try:
        aut.set("name", text)
    except Exception, err:
        aut.set("name", "error")
    return root

def get_link_content(soup, root, tag_name, id):
    aut = etree.SubElement(root, tag_name)
    for tag in soup.findAll('div', id=id):
      for t in tag.findAll('a', href=True):
        text = t["href"]
    try:
        aut.set("name", text)
    except Exception, err:
        aut.set("name", "error")
        print(traceback.format_exc())
    return root

def get_nested_content(soup, root, tag_name, id):
    aut = etree.SubElement(root, tag_name)
    for tag in soup.findAll('div', id=id):
      for input_tag in tag.findAll('input'):
        for_input = input_tag.get("checked")
      for nested_tag in tag.findAll('label'):
        for_label =  nested_tag.get("for")
        for_label_name = tag.find('label',{'for':for_label}).text
        aut_ = etree.SubElement(aut, for_label_name.replace(" ","_"))
        try:
          aut_.set("name", for_input)
        except Exception, err:
          aut.set("name", "error")
          print(traceback.format_exc())
    return root
