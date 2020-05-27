/*
* Copyright (C) 2020 Intel Corporation. All rights reserved.
* SPDX-License-Identifier: Apache-2.0
*/

package com.intel.iot.ams.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

  public static String getSha1Hash(byte[] buffer) {
    try {
      MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
      digest.update(buffer);
      byte messageDigest[] = digest.digest();
      // Create Hex String
      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < messageDigest.length; i++) {
        String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
        if (shaHex.length() < 2) {
          hexString.append(0);
        }
        hexString.append(shaHex);
      }
      return hexString.toString();

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";
  }


  public static String getMd5Hash(byte[] buffer) {
    try {
      MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
      digest.update(buffer);
      byte messageDigest[] = digest.digest();
      // Create Hex String
      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < messageDigest.length; i++) {
        String md5Hex = Integer.toHexString(messageDigest[i] & 0xFF);
        if (md5Hex.length() < 2) {
          hexString.append(0);
        }
        hexString.append(md5Hex);
      }
      return hexString.toString();

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";
  }

}
