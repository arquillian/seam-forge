/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.forge.arquillian.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.junit.Test;


/**
 * ExtractVersionNumberTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ExtractVersionNumberTestCase
{

   @Test
   public void shouldBeAbleToExtractVersionNumber() throws Exception
   {
//      DataProcessor processor = new DataProcessor();
//      processor.setClient(new HttpClientProducer().createClient());
      
//      List<String> versions = processor.process(
//            new URL("http://repository.jboss.org/nexus/content/groups/public/org/jboss/arquillian/arquillian-api/"), 
//            new ExtractVersionNumber());

      ExtractVersionNumber extract = new ExtractVersionNumber();
      List<String> versions = extract.proces(readFile("src/test/resources/versions.html"));
      for(String version : versions)
      {
         System.out.println(version);
      }
   }
   
   private String readFile(String file) throws Exception
   {
      StringBuilder content = new StringBuilder();
      BufferedReader in = new BufferedReader(new FileReader(file));
      String line;
      while( (line = in.readLine()) != null)
      {
          content.append(line);
      }
      in.close();
      return content.toString();
   }
}
