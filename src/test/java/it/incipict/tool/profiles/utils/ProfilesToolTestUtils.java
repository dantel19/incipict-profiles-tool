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
package it.incipict.tool.profiles.utils;

import java.util.Map;

import it.incipict.tool.profiles.model.ProfileType;

/**
*
* @author Daniele Tellina
* @author Alexander Perucci
*/
public class ProfilesToolTestUtils {

    private static final String NEW_LINE = "\n";
    //private static DecimalFormat decimalFormat;

    //print all distances
    public static String printDistancesToString(Map<ProfileType, Double> distancesMap) {
        StringBuilder output = new StringBuilder();

        for (Map.Entry<ProfileType, Double> entry : distancesMap.entrySet()) {
           
        	String profile = entry.getKey().getType();
            Double distance = entry.getValue();

            output.append("d(" + profile + ") = " + distance + NEW_LINE);
        }

        return output.toString();
    }

}
