/**
 * Copyright (c) 2013, impossibl.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of impossibl.com nor the names of its contributors may
 *    be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.namphibian.pgudttojava;

import com.google.common.base.CaseFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Name alteration/generation routines
 *
 * @author kdubb
 *
 */
public class Names {

  static final Pattern FULL_CAPS_PATTERN = Pattern.compile("(^[a-zA-Z])|(_[a-zA-Z])");
  static final Pattern CAPS_PATTERN = Pattern.compile("(_[a-zA-Z])");

  /**
   * Underscore-case to Camel-case with upper case first letter
   * 
   * @param name
   *          Underscore-case name to transform
   * @return Camel-case version of name
   */
  public static String getFullCamelCase(String name) {

    /*StringBuffer res = new StringBuffer();

    Matcher capsMatcher = FULL_CAPS_PATTERN.matcher(name);

    while (capsMatcher.find()) {
      res.append(capsMatcher.group().toUpperCase());
      capsMatcher.appendReplacement(res, "");
    }

    capsMatcher.appendTail(res);

    return res.toString();*/
     
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
  }

  /**
   * Underscore-case to Camel-case with lower case first letter
   * 
   * @param name
   *          Underscore-case name to transform
   * @return Camel-case version of name
   */
  public static String getCamelCase(String name) {
/*
    StringBuffer res = new StringBuffer();

    Matcher capsMatcher = CAPS_PATTERN.matcher(name);

    while (capsMatcher.find()) {
      res.append(capsMatcher.group().toUpperCase());
      capsMatcher.appendReplacement(res, "");
    }

    capsMatcher.appendTail(res);

    return res.toString();*/
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
  }

}
