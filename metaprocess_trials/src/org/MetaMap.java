package org;

import gov.nih.nlm.nls.metamap.*;

import java.util.List;

/**
 * Created by mehdibenchoufi on 12/06/15.
 */
public class MetaMap {

    private MetaMapApi api;
    private StringBuilder builder;
    private String apiOptions;
    List<Result> resultList;

    public void setApiOptions(String api_options) {
        this.apiOptions = api_options;
    }

    /*
    * Old implementation of Metamap constructor, setting roughly the options @todo: to be deleted
    */
    public MetaMap() {
        api = new MetaMapApiImpl();
        setApiOptions("-y -A -V USAbase -Z 2014AB -J acab,anab,comd,cgab,dsyn,emod,inpo,mobd,neop,patf,sosy");
        api.setOptions(apiOptions);
        builder = new StringBuilder();
    }

    /*
    * New implementation of Metamap constructor, setting options from request
    */
    public MetaMap(String options) {
        api = new MetaMapApiImpl();
        setApiOptions(options);
        api.setOptions(apiOptions);
        builder = new StringBuilder();
    }

    public void processOutput(String keywords){
        resultList = api.processCitationsFromString(keywords);
        api.disconnect();
    }

    private StringBuilder buildingOutput(Object... arguments){
        for (int i = 0; i < arguments.length; ++i) {
            builder.append(arguments[i]);
        }
        return builder;
    }


    public String output() throws Exception {
        Result result = resultList.get(0);
        // if needed
        //extractAbbreviations(result);
        for (Utterance utterance: result.getUtteranceList()) {
            for (PCM pcm: utterance.getPCMList()) {
                buildingOutput("Phrase:", " text: " + pcm.getPhrase().getPhraseText(), "Candidates:");
                // mappings
                for (Mapping map: pcm.getMappingList()) {
                    for (Ev mapEv: map.getEvList()) {
                        buildingOutput(
                                "   Score: " + mapEv.getScore(),
                                "   Concept Id: " + mapEv.getConceptId(),
                                "   Concept Name: " + mapEv.getConceptName(),
                                "   Preferred Name: " + mapEv.getPreferredName(),
                                "   Matched Words: " + mapEv.getMatchedWords(),
                                "   Semantic Types: " + mapEv.getSemanticTypes()
                                );
                    }
                }
            }
        }
        extractNegations(result);
        return builder.toString();
    }

    private void extractCandidates(PCM pcm) throws Exception {
        for (Ev ev: pcm.getCandidateList()) {
            buildingOutput( " Candidate:" + "\n" +
                            "  Score: " + ev.getScore() + "\n" +
                            "  Matched Words: " + ev.getMatchedWords());
        }
    }

    private void extractAbbreviations(Result result) throws Exception {
        List<AcronymsAbbrevs> aaList = result.getAcronymsAbbrevs();
        if (aaList.size() > 0) {
            builder.append("Acronyms and Abbreviations");
            for (AcronymsAbbrevs e: aaList) {
                buildingOutput("Acronym: " + e.getAcronym(),
                        "Expansion: " + e.getExpansion(),
                        "Count list: " + e.getCountList(),
                        "CUI list: " + e.getCUIList());
            }
        } else {
            System.out.println(" None.");
        }
    }

    private void extractNegations(Result result) throws Exception {
        List<Negation> negList = result.getNegations();
        if (negList.size() > 0) {
            buildingOutput("Negations:" + "\n");
            for (Negation e: negList) {
                buildingOutput("type: " + e.getType() + "\n" + "ConceptPairs:");
                for (ConceptPair pair: e.getConceptPairList()) {
                    buildingOutput(pair + ",");
                }
            }
        }
    }
}
