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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openhubframework.openhub.api.exception.IntegrationException;
import org.openhubframework.openhub.api.exception.InternalErrorEnum;
import org.openhubframework.openhub.api.file.FileContentTypeExtEnum;
import org.openhubframework.openhub.api.file.FileRepository;
import org.openhubframework.openhub.api.file.OutputStreamWriterCallback;
import org.openhubframework.openhub.common.log.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link FileRepository} interface - files are stored in the file system.
 *
 * @author Petr Juza
 */
public class DefaultFileRepository implements FileRepository {

    /**
     * Absolute path to temporary directory where new files are stored.
     */
    @Value("${dir.temp}")
    private File tempDir;

    /**
     * File repository directory where files will be stored.
     */
    @Value("${dir.fileRepository}")
    private File fileRepoDir;

    @PostConstruct
    public void checkDirs() {
        if (tempDir != null && !tempDir.exists()) {
            throw new IllegalStateException("the temporary directory '" + tempDir + "' doesn't exist");
        }
        if (fileRepoDir != null && !fileRepoDir.exists()) {
            throw new IllegalStateException("the file repository directory '" + fileRepoDir + "' doesn't exist");
        }
    }

    @Override
    public String saveTempFile(OutputStreamWriterCallback writerCallback) {
        Assert.notNull(writerCallback, "os must not be null");

        assertDirs();

        // prepare target file
        String fileId = getNewFileId();

        File targetFile = new File(tempDir, fileId);

        // save file
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(targetFile));

            writerCallback.writeTo(os);

            Log.debug("new file was successfully saved: " + targetFile);
        } catch (IOException ex) {
            Log.error("error occurred during saving file " + targetFile, ex);
            throw new IntegrationException(InternalErrorEnum.E115);
        } finally {
            IOUtils.closeQuietly(os);
        }

        return fileId;
    }

    private void assertDirs() {
        if (tempDir == null || fileRepoDir == null) {
            throw new IllegalStateException("tempDir or fileRepoDir can't be null");
        }
    }

    @Override
    public boolean isFileIdValid(String fileId) {
        if (StringUtils.isEmpty(fileId)) {
            return false;
        }

        assertDirs();

        File tmpFile = new File(tempDir, fileId);

        return tmpFile.exists();
    }

    @Override
    public void commitFile(String fileId, String fileName, FileContentTypeExtEnum contentType, List<String> subFolders) {
        Assert.hasText(fileId, "fileId must not be empty");
        Assert.hasText(fileName, "fileName must not be empty");
        Assert.notNull(subFolders, "subFolders must not be null");

        File tmpFile = new File(tempDir, fileId);

        // check file existence
        if (!tmpFile.exists() || !tmpFile.canRead()) {
            String msg = "temp file " + tmpFile + " doesn't exist or can't be read";
            Log.error(msg);
            throw new IntegrationException(InternalErrorEnum.E115, msg);
        }

        // move file to target directory
        String targetDirName = FilenameUtils.concat(fileRepoDir.getAbsolutePath(),
                StringUtils.join(subFolders, File.separator));
        targetDirName = FilenameUtils.normalize(targetDirName);

        File targetDir = new File(targetDirName);

        try {
            FileUtils.moveFileToDirectory(tmpFile, targetDir, true);

            Log.debug("File (" + tmpFile + ") was successfully moved to directory - " + targetDir);
        } catch (IOException e) {
            String msg = "error occurred during moving temp file " + tmpFile + " to target directory - " + targetDirName;
            Log.error(msg);
            throw new IntegrationException(InternalErrorEnum.E115, msg);
        }

        // rename file
        File targetTmpFile = new File(targetDir, fileId);

        String targetFileName = FilenameUtils.concat(targetDir.getAbsolutePath(), getFileName(fileName, contentType));
        targetFileName = FilenameUtils.normalize(targetFileName);

        try {
            FileUtils.moveFile(targetTmpFile, new File(targetFileName));

            Log.debug("File (" + tmpFile + ") was successfully committed. New path: " + targetFileName);
        } catch (IOException e) {
            String msg = "error occurred during renaming temp file " + tmpFile + " to target directory - " + targetDirName;
            Log.error(msg);
            throw new IntegrationException(InternalErrorEnum.E115, msg);
        }
    }

    /**
     * Gets file name that the file will be saved with.
     *
     * @param fileName the original file name (also with extension)
     * @param contentType the content type
     * @return file name
     */
    protected String getFileName(String fileName, FileContentTypeExtEnum contentType) {
        return contentType.getFilePrefix() + "_" + fileName;
    }

    /**
     * Gets new unique file identifier.
     *
     * @return file ID
     */
    protected String getNewFileId() {
        return UUID.randomUUID().toString();
    }
}
