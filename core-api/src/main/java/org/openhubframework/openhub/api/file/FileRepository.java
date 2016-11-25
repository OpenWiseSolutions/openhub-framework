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

package org.openhubframework.openhub.api.file;

import java.util.List;


/**
 * Contract for storing files in the repository and manipulating with them.
 * <p>
 * Supposed workflow:
 * <ol>
 *     <li>save file into temporary store (file will be in this store for limited time only)
 *     <li>commit saving file - the file will be moved from temporary folder to the target place
 * </ol>
 *
 * @author Petr Juza
 */
public interface FileRepository {

    /**
     * Saves temporary file in the repository.
     *
     * @param writerCallback the callback for writing file to specified output stream
     * @return new unique file identifier
     */
    String saveTempFile(OutputStreamWriterCallback writerCallback);

    /**
     * Is specified file ID valid identifier?
     * In other words is there file with specified identifier?
     *
     * @param fileId the file ID
     * @return {@code true} if file is valid otherwise {@code false}
     */
    boolean isFileIdValid(String fileId);

    /**
     * Commits saving file - the file will be moved from temporary folder to the target place.
     * If there is already the file with the same name in the target folder then it's replaced by the new one.
     *
     * @param fileId the file identifier from {@link #saveTempFile(OutputStreamWriterCallback)}
     * @param fileName the original file name
     * @param contentType the file content type
     * @param subFolders the collection of sub-folders which determine where is the target folder for moving the file
     */
    void commitFile(String fileId, String fileName, FileContentTypeExtEnum contentType, List<String> subFolders);
}
