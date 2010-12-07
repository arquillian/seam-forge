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
package org.jboss.seam.forge.arquillian.wizard.install;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.seam.forge.arquillian.model.ArquillianVersion;
import org.jboss.seam.forge.arquillian.wizard.annotation.WizardComplete;
import org.jboss.seam.forge.arquillian.wizard.annotation.WizardOption;
import org.jboss.seam.forge.arquillian.wizard.annotation.WizardTemplate;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.facets.DependencyFacet;

/**
 * InstallArquillian
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@WizardTemplate 
@ApplicationScoped
public class InstallArquillian
{
   private static final String ARQUILLIAN_VERSION_PROPERTY = "version.arquillian";

   private String version;
   private String testFramework;
   
   @Inject 
   private Instance<Project> project;

   /**
    * @param version the version to set
    */
   @WizardOption(desc = "Which version of Arquillian would you like to install?", provider = VersionProvider.class)
   public void setVersion(String version)
   {
      this.version = version;
   }
   
   /**
    * @param testFramework the testFramework to set
    */
   @WizardOption(desc = "Which test framework would you like to use?", provider = TestFrameworkProvider.class)
   public void setTestFramework(String testFramework)
   {
      this.testFramework = testFramework;
   }
   
   @WizardComplete
   public void install()
   {
      ArquillianVersion arqVersion = new ArquillianVersion(version, ARQUILLIAN_VERSION_PROPERTY);
      DependencyFacet projectDeps = project.get().getFacet(DependencyFacet.class);

      projectDeps.setProperty(arqVersion.getPropertyName(), arqVersion.getVersion());
      projectDeps.addDependency(
            DependencyBuilder.create(
                  "org.jboss.arquilian:arquillian-api:" + arqVersion.getPropertyExpression() + ":jar"));
      projectDeps.addDependency(
            DependencyBuilder.create(
                  "org.jboss.arquilian:arquillian-" + testFramework.toLowerCase() + arqVersion.getPropertyExpression() + ":jar"));
   }

   @Produces 
   public ArquillianVersion getInstalledVersion()
   {
      String version = extractCurrentVersion(ARQUILLIAN_VERSION_PROPERTY);
      if(version != null)
      {
         return new ArquillianVersion(version, ARQUILLIAN_VERSION_PROPERTY);
      }
      return null;
   }
   
   private String extractCurrentVersion(String propertyName)
   {
      if(project.get() == null)
      {
         return null;
      }
      DependencyFacet projectDependencies = project.get().getFacet(DependencyFacet.class);
      if(projectDependencies == null)
      {
         return null;
      }
      return projectDependencies.getProperties().get(propertyName);
   }
}