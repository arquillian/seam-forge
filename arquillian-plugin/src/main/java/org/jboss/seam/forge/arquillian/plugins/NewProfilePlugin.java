/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.forge.arquillian.plugins;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.seam.forge.arquillian.ArquillianFacet;
import org.jboss.seam.forge.arquillian.plugins.profile.ProfileCreator;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.constraints.RequiresFacet;
import org.jboss.seam.forge.project.constraints.RequiresProject;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.plugins.DefaultCommand;
import org.jboss.seam.forge.shell.plugins.Help;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * 
 */
@Named("new-test-profile")
@RequiresProject
@RequiresFacet(ArquillianFacet.class)
@Help("A plugin to setup new Maven profile for Arquillian.")
public class NewProfilePlugin implements Plugin
{
   private final Shell shell;

   private final Instance<Project> projectInstance;
   private final Instance<ProfileCreator> profileCreators;
   
   @Inject
   public NewProfilePlugin(final Instance<Project> projectInstance, final Shell shell, Instance<ProfileCreator> profileCreators)
   {
      this.projectInstance = projectInstance;
      this.shell = shell;
      this.profileCreators = profileCreators;
   }
   
   @DefaultCommand(help = "Create a new Arquillian Maven Profile")
   public void newEntity(
            @Option(required = true,
                     name = "named",
                     description = "The Profile name") final String profileName,
            @Option(required = true,
                     name = "container",
                     description = "The Container name") final String container,
            @Option(required = true,
                     name = "type",
                     description = "The Type of container") final String type,
            @Option(required = true,
                     name = "version",
                     description = "The Container version") final String version)
   {

      Project project = projectInstance.get();

      // TODO: should add default container + namespace to arquillian.xml
      //ArquillianFacet scaffold = project.getFacet(ArquillianFacet.class);
      
      ProfileCreator creator = findProfileCreator(container, type); 
      if(creator == null)
      {
         throw new RuntimeException("No handler for container '" + container + "' of type '" + type + "' was found. Can not create Profile.");
      }
      
      
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);

      Model mavenModel = maven.getPOM();
      List<Profile> profiles = mavenModel.getProfiles();
      if(profileAllReadyExists(profiles, profileName))
      {
         throw new RuntimeException("Arquillian profile '" + profileName + "' allready exists in project");
      }
      Profile profile = new Profile();
      profiles.add(profile);

      profile.setId(profileName);
      creator.create(profile, version);
      
      maven.setPOM(mavenModel);
      
//      String entityPackage = shell.promptCommon(
//               "In which package you'd like to create this @Entity, or enter for default:",
//               PromptType.JAVA_PACKAGE, scaffold.getEntityPackage());

      shell.println("Created Profile [" + profileName + "]");
   }

   /**
    * @param container
    * @param type
    * @return
    */
   private ProfileCreator findProfileCreator(String container, String type)
   {
      for(ProfileCreator creator : profileCreators)
      {
         if(container.equals(creator.getName()) && type.equals(creator.getType()))
         {
            return creator;
         }
      }
      return null;
   }

   /**
    * @param profiles
    * @param profileName
    */
   private boolean profileAllReadyExists(List<Profile> profiles, String profileName)
   {
      for(Profile profile : profiles)
      {
         if(profileName.equals(profile.getId()))
         {
            return true;
         }
      }
      return false;
   }
}
