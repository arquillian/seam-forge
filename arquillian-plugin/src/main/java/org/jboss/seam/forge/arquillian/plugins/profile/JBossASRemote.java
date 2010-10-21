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
package org.jboss.seam.forge.arquillian.plugins.profile;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Profile;

/**
 * JBossASRemote
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class JBossASRemote implements ProfileCreator
{
   @Override
   public String getName()
   {
      return "jbossas";
   }
   
   @Override
   public String getType()
   {
      return "remote";
   }
   
   @Override
   public void create(Profile profile, String version)
   {
      profile.addDependency(createDependency("org.jboss.arquillian.container", "arquillian-jbossas-remote-6", "jar", "1.0.0.Alpha4"));
      profile.addDependency(createDependency("org.jboss.jbossas", "jboss-as-client", "pom", version));
   }
   
   private Dependency createDependency(String group, String artifact, String type, String version)
   {
      Dependency dependency = new Dependency();
      dependency.setGroupId(group);
      dependency.setArtifactId(artifact);
      dependency.setType(type);
      dependency.setVersion(version);
      
      return dependency;
   }
}
