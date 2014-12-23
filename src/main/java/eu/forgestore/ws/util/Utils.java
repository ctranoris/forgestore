/**
 * Copyright 2014 forgestore.eu, University of Patras 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and limitations under the License.
 */

package eu.forgestore.ws.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

public class Utils {

	public static int executeSystemCommand(String cmdStr) {


		CommandLine cmdLine = CommandLine.parse(cmdStr);
		final Executor executor = new DefaultExecutor();
		// create the executor and consider the exitValue '0' as success
		executor.setExitValue(0);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(out);
		executor.setStreamHandler(streamHandler);

		int exitValue = -1;
		try {
			exitValue = executor.execute(cmdLine);

		} catch (ExecuteException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return exitValue;

	}
}
