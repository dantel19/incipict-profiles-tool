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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.incipict.tool.profiles.model.ProfileType;
import it.incipict.tool.profiles.util.ProfilesToolUtils;
import it.incipict.tool.profiles.utils.ProfilesToolTestUtils;

/**
 *
 * @author Alexander Perucci
 */
public class ProfilesToolTest {

    private static Logger logger = LoggerFactory.getLogger(ProfilesTool.class);

    private static final String TEST_RESOURCES = "." + File.separator + "src" + File.separator + "test" + File.separator
            + "resources" + File.separator;

    private static final String KNOLEDGEBASE_FILE_PATH = TEST_RESOURCES + "knowledgeBase.txt";

    private final String[] extension = {"csv"};

    ProfilesTool profileTool;

    @Before
    public void setUp() {
        try {
            profileTool = new ProfilesTool();
            FileUtils.writeStringToFile(new File(KNOLEDGEBASE_FILE_PATH), profileTool.knowledgeBaseToString(),
                    Charset.forName("ISO-8859-1"));
        } catch (ProfilesToolException | IOException e) {
            logger.error("error: ", e);
        }
    }

    @Test
    public void scanAllSurveys() {
        try {
            for (File profileTypeKey : FileUtils.listFilesAndDirs(new File(TEST_RESOURCES), FalseFileFilter.FALSE,
                    TrueFileFilter.TRUE)) {
                if (!profileTypeKey.getName().equals((new File(TEST_RESOURCES)).getName())) {
                    StringBuilder report = new StringBuilder();
                    for (File survey : FileUtils.listFiles(new File(profileTypeKey + File.separator), extension,
                            false)) {
                        report.append("Analyzing " + survey.getAbsolutePath() + "\n");

                        Map<ProfileType, Double> checkProfile = profileTool.checkProfiles(survey);
                        ProfileType bestProfile = ProfilesToolUtils.getBestProfile(checkProfile);
                        
                        Assert.assertEquals(profileTypeKey.getName(), bestProfile.getType());

                        report.append(ProfilesToolTestUtils.printDistancesToString(checkProfile) + "\n\n");
                    }
                    FileUtils.writeStringToFile(
                            new File(profileTypeKey + File.separator + "result.txt"),
                            report.toString(), Charset.forName("ISO-8859-1"));
                }
            }
            Assert.assertTrue(true);
        } catch (ProfilesToolException | IOException e) {
            logger.error("error: ", e);
            Assert.assertTrue(false);
        }
    }

}
