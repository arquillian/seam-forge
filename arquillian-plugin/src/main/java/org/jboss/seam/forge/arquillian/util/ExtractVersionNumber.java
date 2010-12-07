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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ExtractVersionNumber
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ExtractVersionNumber implements DataProcessor.ContentHandler<List<String>>
{
   /* (non-Javadoc)
    * @see org.jboss.seam.forge.arquillian.util.DataProcessor.ContentHandler#proces(java.lang.String)
    */
   @Override
   public List<String> proces(String content)
   {
      List<String> versions = new ArrayList<String>();
      try
      {
         Pattern pattern = Pattern.compile("<a\\b[^>]*href=\"[^>]*>(.*?)/</a>");
         Matcher matcher = pattern.matcher(content);
         int mIdx = 0;
         while (matcher.find())
         {
            mIdx++;
            if(mIdx == 1)
            {
               continue;
            }
            String rawVersion = matcher.group(1);
            if(include(rawVersion))
            {
               versions.add(rawVersion);               
            }
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not extract version numbers", e);
      }

      return versions;
   }

   /**
    * @param rawVersion
    * @return
    */
   private boolean include(String rawVersion)
   {
      return !rawVersion.matches(".*(SP1|SP2|SP3|OSGi).*");
   }
}
