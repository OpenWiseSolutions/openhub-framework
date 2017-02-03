/*
 *  Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.common.log;

import java.io.*;


/**
 * Represents an output stream of bytes with LAZY strategy of stream initialization.
 *
 * @author Tomas Hanus
 * @since 2.0
 */
public class LazyFileOutputStream extends OutputStream {

    private final Object STREAM_LOCK = new Object();

    private File file;
    private boolean append;
    private boolean streamOpen = false;
    private FileOutputStream oStream;

    /**
     * Create lazy file-based {@link OutputStream}.
     *
     * @param f as file used for flushing log events
     * @see #LazyFileOutputStream(File, boolean)
     */
    public LazyFileOutputStream(File f) {
        this.file = f;
    }

    /**
     * Create lazy file-based {@link OutputStream} where is possible to specify {@code append} flag.
     *
     * @param f      as file used for flushing log events
     * @param append {@code true} if the file is opened for append
     * @see FileOutputStream#append
     * @see #LazyFileOutputStream(File)
     */
    public LazyFileOutputStream(File f, boolean append) {
        this.file = f;
        this.append = append;
    }

    /**
     * Create lazy file-based {@link OutputStream} by converting the given pathname string into an abstract pathname.
     * 
     * @param pathName a pathname string
     * @see File#File(String)
     * @see #LazyFileOutputStream(String, boolean) 
     */
    public LazyFileOutputStream(String pathName) {
        this(pathName != null ? new File(pathName) : null);
    }

    /**
     * Create lazy file-based {@link OutputStream} by converting the given pathname string into an abstract pathname 
     * where is possible to specify {@code append} flag.
     *
     * @param pathName a pathname string
     * @param append   {@code true} if the file is opened for append
     * @see File#File(String)
     * @see #LazyFileOutputStream(String) 
     */
    public LazyFileOutputStream(String pathName, boolean append) {
        this(pathName != null ? new File(pathName) : null, append);
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (streamOpen) {
            outputStream().close();
        }
    }

    @Override
    public void flush() throws IOException {
        super.flush();

        if (streamOpen) {
            outputStream().flush();
        }
    }

    @Override
    public void write(int b) throws IOException {
        outputStream().write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream().write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        outputStream().write(b);
    }

    /**
     * This method is the key component of the class, it gets the wrapped FileOutputStream object if already initialized
     * or if not it generates it in a thread safe way. This kind of implementation allows to call the initialization
     * of the underlying FileOutputStream object only when needed.
     *
     * @return the wrapped FileOutputStream object
     * @throws FileNotFoundException if the file can't be created
     */
    protected FileOutputStream outputStream() throws FileNotFoundException {
        synchronized (STREAM_LOCK) {
            if (!streamOpen) {
                oStream = new FileOutputStream(file, append);

                streamOpen = true;
            }
        }
        return oStream;
    }
}
