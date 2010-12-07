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
package org.jboss.seam.forge.arquillian;

import java.io.File;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.forge.arquillian.model.ArquillianVersion;
import org.jboss.seam.forge.arquillian.wizard.Wizard;
import org.jboss.seam.forge.arquillian.wizard.install.InstallArquillian;
import org.jboss.seam.forge.project.Facet;
import org.jboss.seam.forge.project.PackagingType;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.constraints.RequiresFacets;
import org.jboss.seam.forge.project.constraints.RequiresPackagingTypes;
import org.jboss.seam.forge.project.facets.DependencyFacet;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;
import org.jboss.seam.forge.project.facets.ResourceFacet;
import org.jboss.seam.forge.shell.plugins.Help;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * 
 */
@Named("arquillian")
@Help("A plugin to manage configuration of Arquillian and related Maven profiles.")
@RequiresFacets({ MavenCoreFacet.class, DependencyFacet.class, ResourceFacet.class})
@RequiresPackagingTypes({PackagingType.JAR, PackagingType.WAR})
public class ArquillianFacet implements Facet
{
   private static final String ARQUILLIAN_XML = "arquillian.xml";
   
   @Inject
   private Wizard wizard;

   @Inject
   private Instance<ArquillianVersion> installedVersion;
   
   private Project project;

   @Override
   public Project getProject()
   {
      return project;
   }

   @Override
   public void setProject(Project project)
   {
      this.project = project;
   }

   @Override
   public Facet install()
   {
      if (!isInstalled())
      {
         wizard.run(InstallArquillian.class);
      }
      project.registerFacet(this);
      return this;
   }

   @Override
   public boolean isInstalled()
   {
      return installedVersion.get() != null;
   }
   
   /**
    * Get the Arquillian XML configuration. 
    * 
    * @return a File reference  
    */
   public File getConfigFile()
   {
      ResourceFacet resources = project.getFacet(ResourceFacet.class);
      return new File(resources.getTestResourceFolder(), ARQUILLIAN_XML);
   }
   
   public void getIntalledVersion()
   {
      
   }
   
   public void getInstalledProfiles()
   {
      
   }
   
   public void getInstalledTestFramework()
   {
      
   }
}