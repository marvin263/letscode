/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * This class is a simple demonstration of how the Velocity Template Engine can
 * be used in a standalone application.
 * 
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: Example.java 463298 2006-10-12 16:10:32Z henning $
 */

public class Example {
	public Example(String templateFile, String propertyName,
			String fullOutputPath) {
		try {
			Velocity.init(propertyName);

			/*
			 * Make a context object and populate with the data. This is where
			 * the Velocity engine gets the data to resolve the references (ex.
			 * $list) in the template
			 */
			VelocityContext context = new VelocityContext();
			context.put("list", getNames());
			context.put("WrapperInfo", new WrapperInfo("I'm a String", 2012,
					false));
			context.put("ebtsn1", new EqualsButtoStringNot());
			context.put("ebtsn2", new EqualsButtoStringNot());
			context.put("coebtsn1", new CopyOfEqualsButtoStringNot());
			context.put("coebtsn2", new CopyOfEqualsButtoStringNot());
			/*
			 * get the Template object. This is the parsed version of your
			 * template input file. Note that getTemplate() can throw
			 * ResourceNotFoundException : if it doesn't find the template
			 * ParseErrorException : if there is something wrong with the VTL
			 * Exception : if something else goes wrong (this is generally
			 * indicative of as serious problem...)
			 */

			Template template = null;

			try {
				template = Velocity.getTemplate(templateFile);
			} catch (ResourceNotFoundException rnfe) {
				System.out.println("Example : error : cannot find template "
						+ templateFile);
			} catch (ParseErrorException pee) {
				System.out.println("Example : Syntax error in template "
						+ templateFile + ":" + pee);
			}

			/*
			 * Now have the template engine process your template using the data
			 * placed into the context. Think of it as a 'merge' of the template
			 * and the data to produce the output stream.
			 */

			BufferedWriter writer = writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(fullOutputPath)));
			if (template != null)
				template.merge(context, writer);
			/*
			 * flush and cleanup
			 */
			writer.flush();
			writer.close();
			System.out.println("Successfull!");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public ArrayList getNames() {
		ArrayList list = new ArrayList();
		list.add("ArrayList element 1");
		list.add("ArrayList element 2");
		list.add("ArrayList element 3");
		list.add("ArrayList element 4");
		return list;
	}

	private static String[] createParameterAndCleanOldOutput(String[] args) {
		String[] arrayTemplatePropertyOutput = new String[3];
		arrayTemplatePropertyOutput[0] = "example.vm";
		String currentDir = System.getProperty("user.dir") + "/";
		if ("1.7".equals(args[0])) {
			arrayTemplatePropertyOutput[1] = "velocity1.7.properties";
			arrayTemplatePropertyOutput[2] = currentDir + "/1.7/output/1.7.txt";
		} else if ("1.3.1".equals(args[0])) {
			arrayTemplatePropertyOutput[1] = "velocity1.3.1.properties";
			arrayTemplatePropertyOutput[2] = currentDir
					+ "/1.3.1/output/1.3.1.txt";
		}
		new File(arrayTemplatePropertyOutput[2]).delete();
		return arrayTemplatePropertyOutput;
	}

	public static void main(String[] args) {
		String[] arrayTemplatePropertyOutput = createParameterAndCleanOldOutput(args);
		String templateName = arrayTemplatePropertyOutput[0];
		String propertyName = arrayTemplatePropertyOutput[1];
		String outputFullPath = arrayTemplatePropertyOutput[2];
		Example t = new Example(templateName, propertyName, outputFullPath);
	}
}
