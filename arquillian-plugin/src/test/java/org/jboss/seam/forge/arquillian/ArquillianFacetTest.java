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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.shell.Shell;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * 
 */
@RunWith(Arquillian.class)
public class ArquillianFacetTest extends AbstractTestBase
{
   @Test
   public void testCDintoProjectRegistersScaffoldingFacet() throws Exception
   {
      Shell shell = getShell();
      Project project = getProject();

      ArquillianFacet arquillian = project.getFacet(ArquillianFacet.class);
      assertNotNull(arquillian);

      shell.execute("cd /");
      assertNull(getProject());

      shell.execute("cd - ");
      assertNotNull(getProject());

      project = getProject();

      arquillian = project.getFacet(ArquillianFacet.class);
      assertNotNull(arquillian);
   }

   @Test
   public void testGetConfigFile() throws Exception
   {
      Project project = getProject();

      ArquillianFacet arquillian = project.getFacet(ArquillianFacet.class);
      assertNotNull(arquillian);

      File arquillianConfig = arquillian.getConfigFile();
      Assert.assertNotNull(arquillianConfig);
   }
}
