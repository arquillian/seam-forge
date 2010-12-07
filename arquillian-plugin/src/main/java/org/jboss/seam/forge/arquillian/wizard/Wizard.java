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
package org.jboss.seam.forge.arquillian.wizard;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.seam.forge.arquillian.wizard.annotation.WizardComplete;
import org.jboss.seam.forge.arquillian.wizard.annotation.WizardOption;
import org.jboss.seam.forge.arquillian.wizard.annotation.WizardTemplate;
import org.jboss.seam.forge.shell.Shell;

/**
 * Wizard
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@ApplicationScoped
public class Wizard
{
   @Inject
   private Shell shell;
   
   @Inject @WizardTemplate
   private Instance<Object> templates;
   
   @Inject 
   private Instance<WizardDataProvider<?>> providers;

   public <T> T run(Class<T> description) 
   {
      try
      {
         return run(getInstance(description), description);
      } 
      catch (Exception e) 
      {
         throw new RuntimeException("Could not complete Wizad [" + description + "]", e);
      }
   }
   
   @SuppressWarnings("unchecked")
   public <T> T run(T descriptionInstance, Class<T> description) 
   {
      try
      {
         Method completeMethod = extractWizardCompleteMethod(description);
         if(completeMethod == null)
         {
            throw new IllegalArgumentException("No @" + WizardComplete.class.getSimpleName() + " annotated method found on " + description);
         }
         List<WizardOptionMethod> optionFields = extractWizardFields(description);
        
         for(WizardOptionMethod optionField : optionFields)
         {
            Instance<? extends WizardDataProvider<?>> providerInstance = providers.select(optionField.getProvider());
            WizardDataProvider<?> provider = providerInstance.get();
            Object values = provider.get();
            
            String userInput = null;
            
            if(values instanceof List)
            {
               userInput = shell.promptChoiceTyped(optionField.getDesc(), (List<String>)values);
            }
            
            optionField.getMethod().invoke(descriptionInstance, userInput);
         }
         completeMethod.invoke(descriptionInstance);
         return descriptionInstance;
      }
      catch (Exception e) 
      {
         e.printStackTrace();
         throw new RuntimeException("Could not complete Wizad [" + description + "] " + e.getMessage(), e);
      }
   }
   
   @SuppressWarnings("unchecked")
   private <T> T getInstance(Class<T> clazz) throws Exception
   {
      Instance<? extends Object> template = templates.select(clazz);
      return (T)template.get();
   }
   
   private List<WizardOptionMethod> extractWizardFields(Class<?> clazz) throws Exception
   {
      List<WizardOptionMethod> optionMethods= new ArrayList<WizardOptionMethod>();
      List<Method> methods = extractMethod(WizardOption.class, clazz);
      for(Method method : methods)
      {
         WizardOption option = method.getAnnotation(WizardOption.class);
         optionMethods.add(new WizardOptionMethod(option.desc(), option.provider(), method));
      }
      return optionMethods;  
   }

   private List<Method> extractMethod(Class<? extends Annotation> annotation, Class<?> clazz) throws Exception
   {
      List<Method> found = new ArrayList<Method>();
      Method[] methods = clazz.getDeclaredMethods();
      for(Method method : methods)
      {
         if(method.isAnnotationPresent(annotation))
         {
            if(!method.isAccessible())
            {
               method.setAccessible(true);
            }
            found.add(method);
         }
      }
      return found;
   }
   
   private Method extractWizardCompleteMethod(Class<?> clazz) throws Exception
   {
      return extractMethod(WizardComplete.class, clazz).get(0);
   }
   
   private static class WizardOptionMethod 
   {
      private String desc;
      private Class<? extends WizardDataProvider<?>> provider;
      
      private Method method;

      public WizardOptionMethod(String desc, Class<? extends WizardDataProvider<?>> provider, Method method)
      {
         super();
         this.desc = desc;
         this.provider = provider;
         this.method = method;
      }

      /**
       * @return the desc
       */
      public String getDesc()
      {
         return desc;
      }
      
      /**
       * @return the field
       */
      public Method getMethod()
      {
         return method;
      }
      
      /**
       * @return the provider
       */
      public Class<? extends WizardDataProvider<?>> getProvider()
      {
         return provider;
      }
   }
}
