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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;

/**
 * DataProcessor
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class DataProcessor
{
   @Inject
   private HttpClient client;
   
   /**
    * @param client the client to set
    */
   void setClient(HttpClient client)
   {
      this.client = client;
   }
   
   public <T> T process(URL url, final ContentHandler<T> handler)
   {
      HttpGet get = new HttpGet(url.toExternalForm());
      try
      {
         return client.execute(get, new ResponseHandler<T>()
         {
            @Override
            public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException
            {
                StringBuilder content = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while( (line = in.readLine()) != null)
                {
                    content.append(line);
                }
                in.close();
                return handler.proces(content.toString());
            }
         });
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   
   public interface ContentHandler<T> 
   {
      T proces(String content);
   }
}
