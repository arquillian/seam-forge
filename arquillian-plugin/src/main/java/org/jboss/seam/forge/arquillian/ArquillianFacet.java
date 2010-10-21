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
import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.forge.project.Facet;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.MavenFacet;
import org.jboss.seam.forge.project.facets.ResourceFacet;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * 
 */
public class ArquillianFacet implements Facet
{
   private static final String ARQUILLIAN_XML = "arquillian.xml";
   
   private Project project;

   @Override
   public Set<Class<? extends Facet>> getDependencies()
   {
      Set<Class<? extends Facet>> result = new HashSet<Class<? extends Facet>>();
      result.add(MavenFacet.class);
      result.add(ResourceFacet.class);
      return result;
   }

   @Override
   public Project getProject()
   {
      return project;
   }

   @Override
   public Facet init(final Project project)
   {
      this.project = project;
      return this;
   }

   @Override
   public Facet install()
   {
      if (!isInstalled())
      {
         createConfigFile();
      }
      project.registerFacet(this);
      return this;
   }

   @Override
   public boolean isInstalled()
   {
      return getConfigFile().exists();
   }
   
   private void createConfigFile()
   {
      File arquillianConfig = getConfigFile();
      try
      {
         arquillianConfig.createNewFile();
      }
      catch (Exception e) 
      {
         throw new RuntimeException("Could not create configuraton file, " + arquillianConfig.getAbsolutePath(), e);
      }
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
}
