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
package it.incipict.tool.profiles;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.incipict.tool.profiles.model.Profile;
import it.incipict.tool.profiles.model.ProfileType;
import it.incipict.tool.profiles.model.Survey;
import it.incipict.tool.profiles.util.ProfilesToolUtils;

/**
*
* @author Daniele Tellina
*/
public class ProfilesTool {
	private static Logger logger = LoggerFactory.getLogger(ProfilesTool.class);

	private static final String CONFIGURATION = "configuration.properties";
	private static final String CONFIGURATION_SURVEYS = "surveys";
	private static final String CONFIGURATION_SURVEYS_SEPARATOR = ";";
	private static final String CSV_EXTENSION = ".csv";

	private static final String MAIN_RESOURCES = "." + File.separator + "src" + File.separator + "main" + File.separator
			+ "resources" + File.separator;

	private static final String PROFILE_RESOURCE = MAIN_RESOURCES + "profili" + File.separator;

	private static final String NEW_LINE = "\n";
	
	// Parsing CONSTANTS declarations
	private static final float SCALAR = 5;
	private static final int RANGE_START = 0;
	private static final int RANGE_END = 26;

	private List<Profile> knowledgeBase;

	public ProfilesTool() throws ProfilesToolException {
		knowledgeBase = new ArrayList<Profile>();

		scanProfiles(getSurveys());
	}

	private Map<ProfileType, File> getSurveys() throws ProfilesToolException {
		Map<ProfileType, File> surveyConfiguration = new HashMap<ProfileType, File>();
		final Properties properties = new Properties();
		try {
			final ClassLoader loader = this.getClass().getClassLoader();
			final InputStream propFile = loader.getResourceAsStream(CONFIGURATION);
			if (propFile != null) {
				properties.load(propFile);
				propFile.close();

				String value = properties.getProperty(CONFIGURATION_SURVEYS);
				if (value == null || value.isEmpty()) {
					throw new ProfilesToolException("error to parse the configuration file: no surveys founded");
				}

				for (String surveyName : value.split(CONFIGURATION_SURVEYS_SEPARATOR)) {
					String surveyKey = surveyName.replace(CSV_EXTENSION, "");
					if (surveyKey.isEmpty()) {
						throw new ProfilesToolException(
								"error to parse the configuration file: no survey key founded for the survey "
										+ surveyName);
					}

					String surveyDescription = properties.getProperty(surveyKey);
					if (surveyDescription == null || surveyDescription.isEmpty()) {
						throw new ProfilesToolException(
								"error to parse the configuration file: no surveyd escription foundedfor the survey "
										+ surveyName);
					}

					File surveyFile = new File(PROFILE_RESOURCE + surveyName);
					if (!surveyFile.exists() || !surveyFile.canRead()) {
						throw new ProfilesToolException("error to parse the configuration file: the file "
								+ PROFILE_RESOURCE + surveyName + " not exists or not can read");
					}
					surveyConfiguration.put(new ProfileType(surveyKey, surveyDescription), surveyFile);
				}
			}
			return surveyConfiguration;
		} catch (Exception e) {
			throw new ProfilesToolException("error to parse the configuration file: ", e);
		}
	}

	
	// This method scan all surveys of profiles in src/main/resources/profili
	// parse all associated CSV files,
	// It generates a report of all the questionnaires categorized according to
	// the profile in report.txt
	// finally calculate the interest ranks associated to information-profile
	// for each scanned profile.
	private List<Profile> scanProfiles(Map<ProfileType, File> surveyConfiguration) throws ProfilesToolException {
		logger.info("Survey parsing (known profiles)");
		for (Map.Entry<ProfileType, File> entry : surveyConfiguration.entrySet()) {
			logger.info("Parse survey " + entry.getValue().getName() + " '" + entry.getKey().getDescription() + "'");
			List<Survey> surveys = ProfilesToolUtils.parseSurvey(entry.getValue());

			if (surveys != null) {
				// At each cycle save the list of all the questionnaires for the
				// current profile
				Profile profile = new Profile();
				profile.setProfileType(entry.getKey());
				// update the ranks of the current profile (Ref. Profile.java)
				List<Double> ranks = ProfilesToolUtils.calcRanks(surveys);
				profile.setRanks(ranks);
				// add the current profile to the profile list.
				knowledgeBase.add(profile);
			} else {
				logger.info("There are no surveys to be scanned.");
			}

		}

		return knowledgeBase;
	}

	
	public Map<ProfileType, Double> checkProfiles(File surveyFile) throws ProfilesToolException {
		if (!surveyFile.exists() || !surveyFile.canRead()) {
			throw new ProfilesToolException("error to parse the input survey file");
		}

		// fetch the list of all surveys contained in files
		List<Survey> surveys = ProfilesToolUtils.parseSurvey(surveyFile);

		// we assume that the file contains only one survey
		if (surveys == null) {
			throw new ProfilesToolException("error to parse the input survey file: no survey founded");
		} else if (surveys.size() != 1) {
			throw new ProfilesToolException(
					"error to load the input survey file: more than one survey contained, please split in multi files");
		}

		// before getting Euclidean distance we divide the survey answers vector by defined scalar, 
		// because we have different range values in INCIPIC Survey Form,
		// and we want to obtain as result a probabilistic matrix with 0-1 values 
		// for this reason we divide all elements by they max range value
		// in this mode we obtain a percentage values 0%-100% represented by double values (0.00-1.00)
		return ProfilesToolUtils.getEuclideanDistances(
		      ProfilesToolUtils.divideByScalar(surveys.get(0), SCALAR, RANGE_START, RANGE_END), knowledgeBase);
	}

	public String knowledgeBaseToString() {
		StringBuilder output = new StringBuilder();
		// return the knowledge base matrix
		// the matrix contains the associated ranks "informations-profile" for
		// each profile.
		output.append("Knowledge Base Matrix" + NEW_LINE + NEW_LINE);

		//int profileCounter = 0;
		for (Profile profile : knowledgeBase) {
		   //profileCounter++;
			List<Double> ranksVector = profile.getRanks();
			
			//int informationCounter = 1;
			for (Double rank : ranksVector) {
			   output.append("|" + rank);
			   /* SQL-like INSERT statement (Web APP kind for me): */
			   //output.append("(" + profileCounter + ", " + informationCounter++ + ", " + rank + "),");
				//if (rank >= 3.5 && rank < 4.5) { output.append("*A*"); }
			   //if (rank >= 4.5) { output.append("*TOP*"); }
			}
			output.append("| " + profile.getProfileType().getDescription() + NEW_LINE);
		}
		return output.toString();
	}

}