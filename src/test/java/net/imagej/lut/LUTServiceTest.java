/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2019 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.lut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.Test;
import org.scijava.Context;

/**
 * Unit tests for {@link LUTService}.
 * 
 * @author Johannes Schindelin
 * @author Curtis Rueden
 */
public class LUTServiceTest {

	/** Tests {@link LUTService#findLUTs()}. */
	@Test
	public void testFindLUTs() throws Exception {
		final File jarFile = File.createTempFile("listFileContentsTest", ".jar");
		final FileOutputStream out = new FileOutputStream(jarFile);
		final JarOutputStream jarOut = new JarOutputStream(out);
		final String path = "luts/hello world/bang.lut";
		int slash = -1;
		for (;;) {
			slash = path.indexOf('/', slash + 1);
			if (slash < 0) break;
			final JarEntry dir = new JarEntry(path.substring(0, slash + 1));
			jarOut.putNextEntry(dir);
			jarOut.closeEntry();
		}
		jarOut.putNextEntry(new JarEntry(path));
		jarOut.write("world".getBytes());
		jarOut.closeEntry();
		jarOut.close();

		final Context context = new Context(LUTService.class);
		final LUTService lutService = context.getService(LUTService.class);

		final ClassLoader savedLoader = Context.getClassLoader();
		try {
			final ClassLoader loader =
				new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, ClassLoader
					.getSystemClassLoader().getParent());
			Thread.currentThread().setContextClassLoader(loader);

			final Map<String, URL> luts = lutService.findLUTs();
			assertEquals(39, luts.size());
			assertTrue(luts.containsKey("hello world/bang.lut"));
		}
		finally {
			Thread.currentThread().setContextClassLoader(savedLoader);
			if (!jarFile.delete()) {
				jarFile.deleteOnExit();
			}
		}
	}

}
