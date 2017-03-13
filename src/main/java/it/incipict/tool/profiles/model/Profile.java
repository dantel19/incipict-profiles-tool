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

import java.util.List;

/**
 *
 * @author Daniele Tellina
 */
public class Profile {
    private ProfileType profileType;
    //For each profile, the system know the interest grade associated to every single information
    //we call this information "rank"
    private List<Double> ranks;

    public ProfileType getProfileType() {
		return profileType;
	}

	public void setProfileType(ProfileType profileType) {
		this.profileType = profileType;
	}

	public List<Double> getRanks() {
        return ranks;
    }

    public void setRanks(List<Double> ranks) {
        this.ranks = ranks;
    }
}
