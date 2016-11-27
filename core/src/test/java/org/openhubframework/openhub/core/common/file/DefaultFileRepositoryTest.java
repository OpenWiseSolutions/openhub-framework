/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.core.common.file;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openhubframework.openhub.api.file.FileContentTypeExtEnum;
import org.openhubframework.openhub.api.file.OutputStreamWriterCallback;
import org.openhubframework.openhub.core.AbstractCoreTest;


/**
 * Test suite for {@link DefaultFileRepository}.
 *
 * @author Petr Juza
 */
public class DefaultFileRepositoryTest extends AbstractCoreTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private DefaultFileRepository fileRepository = new DefaultFileRepository();

    @Before
    public void prepareData() throws Exception {
        setPrivateField(fileRepository, "tempDir", tempFolder.getRoot());
        setPrivateField(fileRepository, "fileRepoDir", tempFolder.getRoot());
    }

    @Test
    public void testSavingFile() throws Exception {
        fileRepository.checkDirs();

        // save temporary file firstly
        String fileId = fileRepository.saveTempFile(new OutputStreamWriterCallback() {
            @Override
            public void writeTo(OutputStream os) throws IOException {
                IOUtils.copy(new StringReader("text to copy"), os);
            }
        });

        File tempFile = new File(tempFolder.getRoot(), fileId);
        assertThat(FileUtils.directoryContains(tempFolder.getRoot(), tempFile), is(true));
        assertThat(fileRepository.isFileIdValid(fileId), is(true));
        assertThat(fileRepository.isFileIdValid(fileId + "sth"), is(false));

        // commit file
        List<String> subFolders = new ArrayList<String>();
        subFolders.add("customerNo");
        subFolders.add("accountNo");

        FileContentTypeExtEnum contentType = new FileContentTypeExtEnum() {
            @Override
            public String getContentType() {
                return "OBCANKA";
            }

            @Override
            public String getFilePrefix() {
                return "doc";
            }
        };

        fileRepository.commitFile(fileId, "orig.doc", contentType, subFolders);

        String fileName = StringUtils.join(subFolders, File.separator) + File.separator
                + contentType.getFilePrefix() + "_" + "orig.doc";
        File targetFile = new File(tempFolder.getRoot(), fileName);

        assertThat(tempFile.exists(), is(false));
        assertThat(targetFile.exists(), is(true));
        assertThat(targetFile.isDirectory(), is(false));
    }
}
