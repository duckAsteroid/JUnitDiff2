package org.duck.asteroid.junitdiff.io;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * An input source for analysis (either a file or an entry in a ZIP file)
 */
public abstract class Source
{

	/**
	 * Open an input stream to the source data
	 * @return An input stream
	 * @throws IOException If the stream can't be opened
	 */
	public abstract InputStream open() throws IOException;

	/**
	 * The name of the source (as appears in output)
	 * @return The name of this source file
	 */
	public abstract String getName();


	/**
	 * A source that is a plain old XML file in the filesystem
	 */
	public static class FileSource extends Source
	{

		private final FileInputStream f;

		private final String name;


		public FileSource(File f) throws FileNotFoundException
		{
			this.f = new FileInputStream(f);
			this.name = f.getName();
		}


		@Override
		public InputStream open() throws FileNotFoundException
		{
			return f;
		}


		@Override
		public String getName()
		{
			return name;
		}
	}

	/**
	 * A source that is an XML file in a ZIP file
	 */
	public static class ZipEntrySource extends Source
	{

		private final ZipFile file;

		private final ZipEntry entry;


		public ZipEntrySource(ZipFile file, ZipEntry entry)
		{
			this.file = file;
			this.entry = entry;
		}


		@Override
		public InputStream open() throws IOException
		{
			return file.getInputStream(entry);
		}


		@Override
		public String getName()
		{
			return file.getName() + "#" + entry.getName();
		}
	}
}
