/*
 * Copyright 2015 The INCIPICT project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.incipict.tool.profiles.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
*
* @author Daniele Tellina
*/
public class Survey {

    public String ZERO_STRING = "Non mi interessa";

    //offset by which the parsing must start
    //leave 1 if you do not know this
    public int RECORD_OFFSET = 5;

    private String profile;
    private List<String> answers;
    //this Map store occurrences of textual answers provided in all surveys of a CSV File
    //textual answers in GoogleForm are of similar type "checkbox" or "ratio" etc...
    private Map<String, Double> textualAnswersMap;
    
    private List<Double> answersRealVector;
    
    public Survey() {
        this.answers = new ArrayList<String>();
        this.textualAnswersMap = new HashMap<String, Double>();
        this.answersRealVector = new ArrayList<Double>();

        /* 
         answers are customized for "INCIPICT Users profiling survey"
         */
        this.textualAnswersMap.put("\"Lettura\"", 0.0);
        this.textualAnswersMap.put("\"Musica\"", 0.0);
        this.textualAnswersMap.put("\"Teatro\"", 0.0);
        this.textualAnswersMap.put("\"Cinema / Cineforum\"", 0.0);
        this.textualAnswersMap.put("\"Mostre artistiche e/o fotografiche\"", 0.0);
        this.textualAnswersMap.put("\"Viaggi\"", 0.0);
        this.textualAnswersMap.put("\"Assolutamente si.\"", 0.0);
        this.textualAnswersMap.put("\"Visito solamente qualche luogo principale.\"", 0.0);
        this.textualAnswersMap.put("\"Difficilmente trovo del tempo per visitare la città.\"", 0.0);
        this.textualAnswersMap.put("\"Non mi interessa, preferisco fare altro.\"", 0.0);
        this.textualAnswersMap.put("\"si\"", 0.0);
        this.textualAnswersMap.put("\"no\"", 0.0);
        this.textualAnswersMap.put("\"Siti naturali (e.g. parchi, riserve, aree naturali protette etc.)\"", 0.0);
        this.textualAnswersMap.put("\"Beni culturali quali reperti archeologici, opere artistiche, etc.\"", 0.0);
        this.textualAnswersMap.put("\"Espressioni intangibili e patrimoni culturali che caratterizzano una comunità, come tradizioni orali, arti dello spettacolo, feste, artigianato, cucina…\"", 0.0);
        this.textualAnswersMap.put("\"Turismo religioso\"", 0.0);
        this.textualAnswersMap.put("\"Opere architettoniche\"", 0.0);
        this.textualAnswersMap.put("\"Opere scultoree\"", 0.0);
        this.textualAnswersMap.put("\"Opere pittoriche\"", 0.0);
        this.textualAnswersMap.put("\"Reperti archeologici\"", 0.0);
        this.textualAnswersMap.put("\"Centri storici\"", 0.0);
        this.textualAnswersMap.put("\"Siti archeologici\"", 0.0);
    }
    
    public Survey (Survey survey) {
       this.profile = survey.profile;
       this.answers = survey.answers;
       this.textualAnswersMap = survey.textualAnswersMap;
       this.answersRealVector = survey.answersRealVector;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public Map<String, Double> getTextualAnswersMap() {
        return (answers.isEmpty()) ? textualAnswersMap : updateTextualAnswersMap();
    }

    public Map<String, Double> updateTextualAnswersMap() {
        for (String s : this.getAnswers()) {
            //only if "s" isn't a numeric value
            if (!s.matches("\\d*") && this.textualAnswersMap.containsKey(s)) {
                this.textualAnswersMap.put(s, 1.0);
            }
        }
        return this.textualAnswersMap;
    }

    public List<Double> getAnswersRealVector() {
        if (!this.answersRealVector.isEmpty()) {
           return answersRealVector;
        }
        // Else-if the vector is empty calculate now and return it
        else {
       	  // Real vector with each element associated to relative answer of a survey (both numerical and textual)
           List<Double> answersRealVector = new ArrayList<Double>();
           for (String s : this.getAnswers()) {
               if (s.matches("\\d*")) {
                   answersRealVector.add(Double.parseDouble(s));
               }
           }
           //add TextualAnswersMap and return all Vector of answers associated values
           List<Double> mapValues = new ArrayList<Double>(updateTextualAnswersMap().values());
           answersRealVector.addAll(mapValues);
   
           return answersRealVector;
        }
    }

   public void setAnswersRealVector(List<Double> answersRealVector) {
      this.answersRealVector = answersRealVector;
   }

}
