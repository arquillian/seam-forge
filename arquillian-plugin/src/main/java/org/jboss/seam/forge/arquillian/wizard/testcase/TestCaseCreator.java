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
package org.jboss.seam.forge.arquillian.wizard.testcase;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.seam.forge.arquillian.wizard.annotation.WizardComplete;
import org.jboss.seam.forge.arquillian.wizard.annotation.WizardTemplate;
import org.jboss.seam.forge.parser.JavaParser;
import org.jboss.seam.forge.parser.java.JavaClass;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.facets.JavaSourceFacet;
import org.jboss.seam.forge.shell.PromptType;
import org.jboss.seam.forge.shell.Shell;

/**
 * TestCaseCreator
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@ApplicationScoped
@WizardTemplate
public class TestCaseCreator
{
   @Inject 
   private Instance<Project> projectInstance;
   
   @Inject 
   private Shell shell;
   
   private JavaClass javaClass;
   
   public void setJavaClass(JavaClass javaClass)
   {
      this.javaClass = javaClass;
   }
   
   @WizardComplete
   public void createTestCase()
   {
      Project project = projectInstance.get();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      
      String testCaseName = shell.promptCommon(
            "What would you like to call the test case?",
            PromptType.JAVA_CLASS, javaClass.getName() + "TestCase");
      
      JavaClass testCase = JavaParser.createClass()
                     .setPackage(javaClass.getPackage())
                     .setName(testCaseName)
                     .setPublic()
                     .addAnnotation("RunWith")
                        .setLiteralValue("Arquillian.class")
                     .getOrigin();
      
      testCase.addImports(
            javaClass.getQualifiedName(), 
            "org.junit.runner.RunWith", 
            "org.junit.Test",
            "javax.ejb.EJB",
            "org.jboss.arquillian.api.Deployment",
            "org.jboss.arquillian.junit.Arquillian", 
            "org.jboss.shrinkwrap.api.ShrinkWrap",
            "org.jboss.shrinkwrap.api.Archive", 
            "org.jboss.shrinkwrap.api.spec.JavaArhcive");

      testCase.addMethod()
               .setName("createDeployment")
               .setPublic()
               .setReturnType("Archive")
               .setBody(createDeploymentBody(javaClass))
               .addAnnotation("Deployment");
      
      testCase.addField()
               .setName(javaClass.getName().toLowerCase())
               .setType(javaClass.getName())
               .setPrivate()
               .addAnnotation("EJB");
      
      testCase.addMethod()
               .setName("shouldBeAbleToInvoke" + javaClass.getName())
               .setPublic()
               .setReturnTypeVoid()
               .setBody("")
               .addAnnotation("Test");
      
      java.saveTestJavaClass(testCase);
   }
   
   private String createDeploymentBody(JavaClass javaClass)
   {
      StringBuilder body = new StringBuilder();
      body.append("return ShrinkWrap.create(JavaArchive.class, \"test.jar\")\n");
      body.append("                   .addClass(" + javaClass.getName() + ".class);");
      return body.toString();
   }
}
