/*
 * Copyright (C) 2020 Intel Corporation. All rights reserved. SPDX-License-Identifier: Apache-2.0
 */

package com.intel.iot.ams.utils;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileAndDirUtils {

  /**
   * Save the uploaded application package on to local disk
   *
   * @param file the instance represent the file
   * @param path the path to be saved on the local disk
   * @param filename the file name to be saved on the local disk
   */
  public static void saveFile(MultipartFile file, String path, String filename) throws IOException {

    File f = new File(path);
    // if the repository path is not existed, then create it.
    if (!f.exists()) {
      f.mkdirs();
    }

    FileUtils.copyInputStreamToFile(file.getInputStream(), new File(path + filename));
  }

  /**
   * Rename a file
   *
   * @param path the path of the file
   * @param oldname the old name of the file
   * @param newname the new name of the file
   */
  public static void renameFile(String path, String oldname, String newname) {
    if (!oldname.equals(newname)) {
      File oldfile = new File(path + "/" + oldname);
      File newfile = new File(path + "/" + newname);
      if (!oldfile.exists()) {
        return;
      }
      if (newfile.exists()) {
        newfile.delete();
      }
      oldfile.renameTo(newfile);
    }
  }
}
