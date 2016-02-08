/*******************************************************************************
 *   Copyright (c) 2016 Kamesh Sampath<kamesh.sampath@hotmail.com)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 ******************************************************************************/

package org.workspace7.populartalkies;

import android.test.suitebuilder.TestMethod;
import android.test.suitebuilder.TestSuiteBuilder;
import android.test.suitebuilder.annotation.SmallTest;

import com.android.internal.util.Predicate;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
@SuppressWarnings("ALL")
public class SmallTestsSuite extends TestSuite {

    public static Test suite() {

        return new TestSuiteBuilder(SmallTestsSuite.class)
                .includeAllPackagesUnderHere().addRequirements(new Predicate<TestMethod>() {
                    @Override
                    public boolean apply(TestMethod testMethod) {

                        return testMethod.getAnnotation(SmallTest.class) != null;
                    }
                }).build();
    }

    public SmallTestsSuite() {

        super();
    }

}
