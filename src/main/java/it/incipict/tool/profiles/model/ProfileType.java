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

/**
*
* @author Daniele Tellina
*/
public class ProfileType {
	private final String type;
	private final String description;

	public ProfileType(final String type, final String description) {
		this.type = type;
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object otherSurvey) {
		if (otherSurvey == null) {
			return false;
		}
		
		if (otherSurvey == this) {
			return true;
		}
		
		if (!(otherSurvey instanceof ProfileType)) {
			return false;
		}
		
		ProfileType survey = (ProfileType) otherSurvey;
		if (!this.getType().equals(survey.getType())) {
			return false;
		}
		return true;

	}
}
