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

/**
*
* @author Alexander Perucci
*/

public class ProfilesToolException extends Exception {
	private static final long serialVersionUID = 9043342553618735200L;

	public ProfilesToolException() {
		super();
	}

	public ProfilesToolException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProfilesToolException(String message) {
		super(message);
	}

	public ProfilesToolException(Throwable cause) {
		super(cause);
	}
}
