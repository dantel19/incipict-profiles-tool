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
package it.incipict.tool.profiles.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;

import it.incipict.tool.profiles.ProfilesToolException;
import it.incipict.tool.profiles.model.Profile;
import it.incipict.tool.profiles.model.ProfileType;
import it.incipict.tool.profiles.model.Survey;

/**
 *
 * @author Daniele Tellina
 * @author Alexander Perucci
 */
public class ProfilesToolUtils {
   
   private static final boolean WEBASSOC_MODE = false;
   // Parsing CONSTANTS declarations
   private static final float SCALAR = 5;
   private static final int RANGE_START = 0;
   private static final int RANGE_END = 26;

   public static List<Survey> parseSurvey(File file) throws ProfilesToolException {
      List<Survey> surveys = new ArrayList<Survey>();

      Iterable<CSVRecord> records;
      try {
         Reader in = new FileReader(file.getCanonicalPath());
         records = CSVFormat.RFC4180.parse(in);
      } catch (IOException e) {
         throw new ProfilesToolException("error while reading files.", e);
      }

      // loop all surveys in CSV line by line
      // note: a single CSV can contain more surveys
      for (CSVRecord record : records) {
         int line = (int) record.getRecordNumber();
         // skip the first line because it simply contains the headers
         if (line > 1) {
            Survey survey = new Survey();
            List<String> answers = new ArrayList<String>();
            List<String> textualAnswers = new ArrayList<String>();
            // in the Survey model we can define a "RECORD OFFSET" by which
            // the parser must start
            for (int i = survey.RECORD_OFFSET; i < record.size(); i++) {
               String r = record.get(i);

               // if the "r" is empty the user has not responded
               // skip to the next answers
               if (r.isEmpty()) {
                  continue;
               }
               // if "r" isn't a numerical value and it is different than
               // ZERO_STRING (Ref. Survey model)
               // Note: the ZERO_STRING is defined in Survey Class. Its
               // value is Zero.
               if ((!r.matches("\\d*") && r.compareTo(survey.ZERO_STRING) != 0)) {
                  // Otherwise the answer is added to a list of Strings.
                  // this list will appended in queue. (*)
                  String[] str = r.split(";");
                  for (String s : str) {
                     textualAnswers.add("\"" + s + "\"");
                  }
               }
               // if "r" is a numeric value, simply add it in the answers
               // list.
               if (r.compareTo(survey.ZERO_STRING) == 0) {
                  answers.add("0");
               }
               if (r.matches("\\d*")) {
                  answers.add(r);
               }
            }
            // add the textual answers in queue (*)
            answers.addAll(textualAnswers);

            String fname = file.getName();
            survey.setProfile(fname.substring(0, fname.lastIndexOf('.')));
            survey.setAnswers(answers);
            surveys.add(survey);
         }
      }
      return (surveys != null) ? surveys : null;
   }

   public static List<Double> calcRanks(List<Survey> surveys) {
      List<Double> ranks = new ArrayList<Double>();
      Map<String, Double> TextualAnswersMap = new Survey().getTextualAnswersMap();

      int count = 0;
      for (Survey survey : surveys) {
         Map<String, Double> surveyMap = survey.getTextualAnswersMap();
         count++;
         for (int i = 0; i < survey.getAnswers().size(); i++) {
            String r = survey.getAnswers().get(i);
            // first of all sum the answersVectors for all surveys of this
            // profile and store the sum in a new list
            // this condition match the numerical values
            if (r.matches("\\d*")) {
               if (count == 1) {
                  // first survey
                  ranks.add(Double.parseDouble(r));
               } else {
                  // sum all elements of vector (REMARK: A+B = a1+b1,
                  // a2+b2, ..., an+bn)
                  ranks.set(i, (ranks.get(i) + Double.parseDouble(r)));
               }
            }
            // now count all strings occurences in textualAnswers
            // (checkbox/radio/etc..)
            // for the current survey.
            else {
               if (surveyMap.containsKey(r)) {
                  Double old = TextualAnswersMap.get(r);
                  TextualAnswersMap.replace(r, (old + surveyMap.get(r)));
               }
            }
         }
      }
      // append in queue all hashmap's values.
      List<Double> mapValues = new ArrayList<Double>(TextualAnswersMap.values());
      ranks.addAll(mapValues);

      // finally calculate the statistical average for each element of vector
      // divided by total of surveys.
      for (int i = 0; i < ranks.size(); i++) {
         Double f = ranks.get(i);
         ranks.set(i, ((f / surveys.size())));
      }
      // Normalizing the matrix with 0-1 probabilistic value.
      // Note: in INCIPICT Survey Form the max range for the questions 1-27 is 5
      // Divide this answers per 5
      // because we want to obtain as result a probabilistic matrix with 0-1 values 
      // for this reason we divide all elements by they max range value
      // in this mode we obtain a percentage values 0%-100% represented by double values (0.00-1.00)
      ranks = (ProfilesToolUtils.divideByScalar(ranks, SCALAR, RANGE_START, RANGE_END));

      // return the ranks list for this profile.
      return ranks;
   }

   // this method calculate the Euclidean Distances between
   // the answers vector of a single survey and the ranks vector of each
   // different profile.
   public static Map<ProfileType, Double> getEuclideanDistances(Survey survey, List<Profile> profilesList) {
      Map<ProfileType, Double> DistancesMap = new HashMap<ProfileType, Double>();
      int size = survey.getAnswersRealVector().size();
      Double[] answers, ranks;
      answers = survey.getAnswersRealVector().toArray(new Double[size]);

      RealVector answersRealVector = new ArrayRealVector(answers);

      for (Profile profile : profilesList) {
         size = profile.getRanks().size();
         ranks = profile.getRanks().toArray(new Double[size]);

         RealVector ranksRealVector = new ArrayRealVector(ranks);

         // Calculate Euclidea Distance
         double distance = answersRealVector.getDistance(ranksRealVector);
         // add "distance" to Distance's Map
         DistancesMap.put(profile.getProfileType(), distance);
      }
      return DistancesMap;
   }

   public static ProfileType getBestProfile(Map<ProfileType, Double> profiles) {
      ProfileType bestProfile = null;
      Double bestDistance = null;
      for (Map.Entry<ProfileType, Double> entry : profiles.entrySet()) {
         if (bestProfile == null || bestDistance > entry.getValue()){
            bestProfile = entry.getKey();
            bestDistance = entry.getValue();
         }
      }
      
        return bestProfile;
   }
   
   // Operation Between vectors and it's elements (Divide by scalar)
   public static List<Double> divideByScalar(final List<Double> vector, float scalar) {
      // Make a copy the original vector
      List<Double> result = new ArrayList<Double>(vector);
      // Divide vector elements between range of position start-end by a scalar
      int index = 0;
      for (Double v : vector) {
            result.set(index++, (v / scalar));
         }
      return result;
   }
   public static List<Double> divideByScalar(final List<Double> vector, float scalar, int start, int end) {
      int index = start;
      // Make a copy the original vector
      List<Double> result = new ArrayList<Double>(vector);
      // Divide vector elements between range of position start-end by a scalar
      for (Double v : vector) {
            if (index <= 26) { 
               result.set(index++, (v / scalar));
            }
      }
      return result;
   }
   public static Survey divideByScalar(final Survey survey, float scalar, int start, int end) {
      // Make a copy of the survey
      Survey result = new Survey(survey);
      // Get the answers vector of this survey
      List<Double> answersRealVector = result.getAnswersRealVector();
      
      // Divide vector elements between range of position start-end by a scalar
      // and set it in the new result survey object
      result.setAnswersRealVector(divideByScalar(answersRealVector, scalar, start, end));
      
      return webAppProfilerManagerAssoc(result);
   }
   public static Survey divideByScalar(final Survey survey, float scalar) {
      // Make a copy of the survey
      Survey result = new Survey(survey);
      // Get the answers vector of this survey
      List<Double> answersRealVector = result.getAnswersRealVector();
      
      // Divide vector elements between range of position start-end by a scalar
      // and set it in the new result survey object
      result.setAnswersRealVector(divideByScalar(answersRealVector, scalar));
      return webAppProfilerManagerAssoc(result);
   }
   public static Survey webAppProfilerManagerAssoc(final Survey survey) {
      if (!WEBASSOC_MODE) return survey;
      else {
         // Make a copy of the survey
         Survey result = new Survey(survey);
         List<Double> answersRealVector = result.getAnswersRealVector();
         System.out.println("\n[1] " +answersRealVector.toString());
   
         int index = 0;
         for (Double d : answersRealVector) {
            if (d > 0.7) answersRealVector.set(index++, 1.0);
            else answersRealVector.set(index++, 0.0);
         }
         result.setAnswersRealVector(answersRealVector);
         System.out.println("[2] " +answersRealVector.toString());
         return result;
      }
   }

}
