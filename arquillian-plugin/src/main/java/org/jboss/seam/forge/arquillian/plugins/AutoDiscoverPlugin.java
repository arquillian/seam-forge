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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.forge.arquillian.ArquillianFacet;
import org.jboss.seam.forge.arquillian.util.ArquillianUtil;
import org.jboss.seam.forge.arquillian.wizard.Wizard;
import org.jboss.seam.forge.arquillian.wizard.testcase.TestCaseCreator;
import org.jboss.seam.forge.parser.JavaParser;
import org.jboss.seam.forge.parser.java.JavaClass;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.constraints.RequiresFacet;
import org.jboss.seam.forge.project.constraints.RequiresProject;
import org.jboss.seam.forge.project.facets.JavaSourceFacet;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.plugins.DefaultCommand;
import org.jboss.seam.forge.shell.plugins.Help;
import org.jboss.seam.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * 
 */
@Named("arquillian-autodiscover")
@RequiresProject
@RequiresFacet(ArquillianFacet.class)
@Help("A plugin to auto discover possible classes that can be Arquillian tested")
public class AutoDiscoverPlugin implements Plugin
{
   private final Shell shell;
   private Wizard wizard;
   
   private final Instance<Project> projectInstance;
   private Instance<TestCaseCreator> creator;
   
   @Inject
   public AutoDiscoverPlugin(final Instance<Project> projectInstance, final Shell shell, Wizard wizard, @Any Instance<TestCaseCreator> creator)
   {
      this.projectInstance = projectInstance;
      this.shell = shell;
      this.wizard = wizard;
      this.creator = creator;
   }
   
   @DefaultCommand(help = "Start autodiscover of possible classes to add Arquillian testcases for")
   public void startAutoDiscover()
   {
      Project project = projectInstance.get();
      
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      
      List<File> sourceFiles = new ArrayList<File>();
      findAllJavaClasses(sourceFiles, java.getSourceFolder());
      
      for(File sourceFile : sourceFiles)
      {
         try
         {
            JavaClass javaClass = JavaParser.parse(sourceFile);
            if(ArquillianUtil.isSupported(javaClass))
            {
               String testClassName = testClass(javaClass.getQualifiedName());
               File testCaseFile = java.getTestSourceFile(testClassName.replaceAll("\\.", "/") + ".java");
               if(testCaseFile.exists())
               {
                  continue;
               }
               
               boolean createTest = shell.promptBoolean(
                     "Would you like to create a test for [" + javaClass.getQualifiedName()  + "]", true);

               if(createTest)
               {
                  TestCaseCreator testCaseCreator = creator.get();
                  testCaseCreator.setJavaClass(javaClass);
                  wizard.run(testCaseCreator, TestCaseCreator.class);
               }
               System.out.println("");
            }
         } 
         catch (Exception e) 
         {
            System.err.println(e.getMessage());
         }
      }
   }
   
   private void findAllJavaClasses(List<File> sourceFiles, File sourceFolder)
   {
      File[] files = sourceFolder.listFiles(new JavaSourceFileFilter());
      if(files != null && files.length > 0)
      {
         sourceFiles.addAll(Arrays.asList(files));
      }
      for(File directory : sourceFolder.listFiles(new DirectoryFileFilter()))
      {
         findAllJavaClasses(sourceFiles, directory);
      }
   }

   private String testClass(String qualifiedName)
   {
      return qualifiedName.replaceFirst("\\.java", "TestCase.java");
   }
   
   private class JavaSourceFileFilter implements FilenameFilter
   {
      @Override
      public boolean accept(File dir, String name)
      {
         return name.endsWith(".java");
      }
   }
   
   private class DirectoryFileFilter implements FileFilter 
   {
      @Override
      public boolean accept(File pathname)
      {
         return pathname.isDirectory();
      }
   }
}
