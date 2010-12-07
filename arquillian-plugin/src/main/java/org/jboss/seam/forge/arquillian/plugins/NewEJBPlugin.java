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

import java.lang.annotation.Annotation;

import javax.ejb.MessageDriven;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.forge.arquillian.annotation.Created;
import org.jboss.seam.forge.parser.JavaParser;
import org.jboss.seam.forge.parser.java.JavaClass;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.constraints.RequiresFacet;
import org.jboss.seam.forge.project.constraints.RequiresProject;
import org.jboss.seam.forge.project.facets.JavaSourceFacet;
import org.jboss.seam.forge.shell.PromptType;
import org.jboss.seam.forge.shell.Shell;
import org.jboss.seam.forge.shell.plugins.DefaultCommand;
import org.jboss.seam.forge.shell.plugins.Help;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * 
 */
@Named("new-ejb")
@RequiresProject
@RequiresFacet(JavaSourceFacet.class)
@Help("A plugin to create a new Enterprise Java Bean.")
public class NewEJBPlugin implements Plugin
{
   public enum EJBType 
   {
      Stateless(Stateless.class), 
      Stateful(Stateful.class),
      MessageDriven(MessageDriven.class);
      
      private Class<? extends Annotation> type;

      EJBType(Class<? extends Annotation> type) 
      {
         this.type = type;
      }
      
      public Class<? extends Annotation> getType()
      {
         return type;
      }
   }
   
   private final Shell shell;

   private final Instance<Project> projectInstance;
   
   private final Event<JavaClass> beanCreatedEvent;
   
   @Inject
   public NewEJBPlugin(final Instance<Project> projectInstance, final Shell shell, @Created Event<JavaClass> beanCreatedEvent)
   {
      this.projectInstance = projectInstance;
      this.shell = shell;
      this.beanCreatedEvent = beanCreatedEvent;
   }
   
   @DefaultCommand(help = "Create a new Enterprise Java Bean")
   public void newEntity(
            @Option(required = true,
                     name = "named",
                     description = "The EJB name") final String beanName)
   {

      Project project = projectInstance.get();

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      EJBType type = shell.promptChoiceTyped(
            "What type of Enterprise Java Bean would you like to create?", EJBType.values());
 
      String entityPackage = shell.promptCommon(
            "In which package you'd like to create the @" + type + " bean, or enter for default:",
            PromptType.JAVA_PACKAGE, java.getBasePackage() + ".service");

      JavaClass beanClass = JavaParser.createClass()
                     .setPackage(entityPackage)
                     .setName(beanName)
                     .setPublic()
                     .addAnnotation(type.getType())
                     .getOrigin();
      
      java.saveJavaClass(beanClass);

      shell.println("Created [" + type.name() + "] Enterprise Java Bean [" + beanName + "]");
      beanCreatedEvent.fire(beanClass);
   }
}
